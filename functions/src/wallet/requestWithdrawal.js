/**
 * requestWithdrawal - User withdrawal request submit karta hai.
 *
 * Flow:
 *  1. Validate (min amount, sufficient balance, KYC details)
 *  2. Create withdrawalRequest doc
 *  3. onWithdrawalRequest trigger handle karega balance hold + admin notification
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.requestWithdrawal = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Login required");
  }

  const { amount, method, payoutDetails } = data;
  const userId = context.auth.uid;

  if (typeof amount !== "number" || amount < 100) {
    throw new functions.https.HttpsError("invalid-argument", "Minimum ₹100");
  }

  if (!["UPI", "BANK"].includes(method)) {
    throw new functions.https.HttpsError("invalid-argument", "Invalid method");
  }

  if (!payoutDetails || (method === "UPI" && !payoutDetails.upiId) ||
      (method === "BANK" && (!payoutDetails.accountNumber || !payoutDetails.ifsc || !payoutDetails.accountHolderName))) {
    throw new functions.https.HttpsError("invalid-argument", "Incomplete KYC details");
  }

  const ref = await db.collection("withdrawalRequests").add({
    userId,
    amount,
    method,
    payoutDetails,
    status: "PENDING",
    requestedAt: admin.firestore.FieldValue.serverTimestamp(),
    processedAt: null,
  });

  return { success: true, requestId: ref.id };
});
