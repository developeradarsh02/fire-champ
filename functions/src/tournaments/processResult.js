/**
 * processResult - Admin result verify/reject karta hai.
 *
 * Protected: Sirf admin custom claim wale users call kar sakte hain.
 * Body: { tournamentId, userId, action: "APPROVE" | "REJECT", reason?: string }
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.processResult = functions.https.onCall(async (data, context) => {
  if (!context.auth || !context.auth.token.admin) {
    throw new functions.https.HttpsError("permission-denied", "Admin only");
  }

  const { tournamentId, userId, action, reason } = data;
  const participantRef = db.collection("tournaments").doc(tournamentId).collection("participants").doc(userId);

  if (action === "APPROVE") {
    await participantRef.update({ submissionStatus: "VERIFIED" });
    // onResultVerified trigger will handle prize credit
    return { success: true };
  } else if (action === "REJECT") {
    await participantRef.update({
      submissionStatus: "REJECTED",
      rejectionReason: reason || "Screenshot unclear",
    });
    return { success: true };
  }
  throw new functions.https.HttpsError("invalid-argument", "Invalid action");
});
