/**
 * processWithdrawal - Admin withdrawal approve/reject karta hai.
 *
 * Admin custom claim required.
 * On APPROVE: status = APPROVED, user ko notification jayega
 *             (admin actual money transfer UPI/bank se karega manually)
 * On REJECT: status = REJECTED, amount release back to user's winningBalance
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.processWithdrawal = functions.https.onCall(async (data, context) => {
  if (!context.auth || !context.auth.token.admin) {
    throw new functions.https.HttpsError("permission-denied", "Admin only");
  }
  const { requestId, action, note } = data;
  const ref = db.collection("withdrawalRequests").doc(requestId);
  const snap = await ref.get();
  if (!snap.exists) throw new functions.https.HttpsError("not-found", "Request not found");
  const req = snap.data();

  if (action === "APPROVE") {
    await ref.update({
      status: "APPROVED",
      processedAt: admin.firestore.FieldValue.serverTimestamp(),
      adminNote: note,
    });
    // FCM to user
    const userSnap = await db.collection("users").doc(req.userId).get();
    if (userSnap.data().fcmToken) {
      await admin.messaging().send({
        token: userSnap.data().fcmToken,
        notification: {
          title: "Withdrawal Approved ✅",
          body: `₹${req.amount} withdrawal is being processed.`,
        },
        data: { type: "WITHDRAWAL_UPDATE" },
      });
    }
    return { success: true };
  } else if (action === "REJECT") {
    await ref.update({
      status: "REJECTED",
      processedAt: admin.firestore.FieldValue.serverTimestamp(),
      adminNote: note,
    });
    // Refund amount to user
    await db.collection("users").doc(req.userId).update({
      winningBalance: admin.firestore.FieldValue.increment(req.amount),
      payouts: admin.firestore.FieldValue.increment(-req.amount),
    });
    return { success: true };
  }
  throw new functions.https.HttpsError("invalid-argument", "Invalid action");
});
