/**
 * Razorpay integration - Cloud Functions
 *
 * createRazorpayOrder: Server-side order create karta hai Razorpay API se.
 *   Client kabhi directly Razorpay API call nahi karega (security).
 *
 * verifyRazorpayPayment: Payment success hone par signature verify karta hai
 *   aur wallet me coins credit karta hai.
 *
 * razorpayWebhook: Webhook to securely verify payment capture from server.
 *
 * Setup:
 *   1. Razorpay Dashboard → Settings → API Keys
 *   2. Generate Test/Live key
 *   3. Set as Firebase Functions config:
 *      firebase functions:config:set razorpay.key_id="rzp_test_xxxxx"
 *      firebase functions:config:set razorpay.key_secret="xxxxx"
 *      firebase functions:config:set razorpay.webhook_secret="xxxxx"
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const crypto = require("crypto");

const db = admin.firestore();
const RAZORPAY_KEY_ID = functions.config().razorpay?.key_id || "rzp_test_xxxxxxxxxxxxx";
const RAZORPAY_KEY_SECRET = functions.config().razorpay?.key_secret || "your_test_key_secret";

/**
 * Create Razorpay order for deposit.
 */
exports.createRazorpayOrder = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Login required");
  }
  const { amount } = data;
  if (typeof amount !== "number" || amount < 10 || amount > 100000) {
    throw new functions.https.HttpsError("invalid-argument", "Invalid amount (₹10-₹100000)");
  }

  const amountInPaise = amount * 100;

  try {
    const auth = Buffer.from(`${RAZORPAY_KEY_ID}:${RAZORPAY_KEY_SECRET}`).toString("base64");
    const response = await fetch("https://api.razorpay.com/v1/orders", {
      method: "POST",
      headers: {
        "Authorization": `Basic ${auth}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        amount: amountInPaise,
        currency: "INR",
        receipt: `rcpt_${Date.now()}_${context.auth.uid.substring(0, 8)}`,
        notes: {
          userId: context.auth.uid,
          purpose: "wallet_topup"
        }
      })
    });

    if (!response.ok) {
      const errText = await response.text();
      throw new Error(`Razorpay API error: ${errText}`);
    }

    const order = await response.json();
    return {
      orderId: order.id,
      amount: amountInPaise,
      currency: "INR",
      key: RAZORPAY_KEY_ID
    };
  } catch (err) {
    console.error("Razorpay order creation failed:", err);
    throw new functions.https.HttpsError("internal", err.message);
  }
});

/**
 * Verify payment after Razorpay checkout success (Client side verification).
 */
exports.verifyRazorpayPayment = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Login required");
  }
  const { orderId, paymentId, signature, amount } = data;
  const userId = context.auth.uid;

  const generated = crypto
    .createHmac("sha256", RAZORPAY_KEY_SECRET)
    .update(`${orderId}|${paymentId}`)
    .digest("hex");

  if (generated !== signature) {
    throw new functions.https.HttpsError("invalid-argument", "Payment signature mismatch");
  }

  try {
    const userRef = db.collection("users").doc(userId);
    await userRef.update({
      depositedBalance: admin.firestore.FieldValue.increment(amount / 100),
      walletBalance: admin.firestore.FieldValue.increment(amount / 100),
    });

    await db.collection("transactions").add({
      userId,
      type: "DEPOSIT",
      amount: amount / 100,
      description: `Added via Razorpay (${paymentId})`,
      status: "COMPLETED",
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
      paymentId,
      orderId,
    });

    return { success: true, message: `credited to wallet` };
  } catch (err) {
    throw new functions.https.HttpsError("internal", err.message);
  }
});

/**
 * Razorpay Webhook Handler
 * Verify and process 'payment.captured' event.
 */
exports.razorpayWebhook = functions.https.onRequest(async (req, res) => {
  const signature = req.headers["x-razorpay-signature"];
  const payload = JSON.stringify(req.body);
  const WEBHOOK_SECRET = functions.config().razorpay?.webhook_secret || "whsec_test_secret";

  const expectedSignature = crypto
    .createHmac("sha256", WEBHOOK_SECRET)
    .update(payload)
    .digest("hex");

  if (signature !== expectedSignature) {
    console.error("Invalid webhook signature");
    return res.status(400).send("Invalid signature");
  }

  const event = req.body.event;

  if (event === "payment.captured") {
    const payment = req.body.payload.payment.entity;
    const userId = payment.notes.userId;
    const amount = payment.amount / 100;
    const paymentId = payment.id;

    if (!userId) {
      console.error("No userId in payment notes", paymentId);
      return res.status(400).send("Missing userId");
    }

    try {
      await db.runTransaction(async (transaction) => {
        const userRef = db.collection("users").doc(userId);

        const existingTransaction = await db.collection("transactions")
          .where("paymentId", "==", paymentId)
          .limit(1)
          .get();

        if (!existingTransaction.empty) {
          return;
        }

        transaction.update(userRef, {
          depositedBalance: admin.firestore.FieldValue.increment(amount),
          walletBalance: admin.firestore.FieldValue.increment(amount),
        });

        const transRef = db.collection("transactions").doc();
        transaction.set(transRef, {
          userId,
          type: "DEPOSIT",
          amount: amount,
          description: `Added via Razorpay (Webhook)`,
          status: "COMPLETED",
          timestamp: admin.firestore.FieldValue.serverTimestamp(),
          paymentId,
        });
      });

      return res.status(200).send("Success");
    } catch (err) {
      console.error("Webhook processing error:", err);
      return res.status(500).send("Internal Error");
    }
  }

  return res.status(200).send("Event ignored");
});
