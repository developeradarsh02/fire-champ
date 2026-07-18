/**
 * submitResult - User tournament ka result submit karta hai.
 *
 * Flow:
 *  1. Match ended? (current time > match start + 30 min)
 *  2. User joined tha? (participants collection me entry)
 *  3. Already submitted? (PENDING/VERIFIED/REJECTED check)
 *  4. Update participant.submissionStatus = PENDING
 *  5. FCM to admin ke devices (review queue notification)
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.submitResult = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Login required");
  }

  const { tournamentId, kills, rank, screenshotUrl } = data;
  const userId = context.auth.uid;

  if (typeof kills !== "number" || typeof rank !== "number" || !screenshotUrl) {
    throw new functions.https.HttpsError("invalid-argument", "Invalid input");
  }

  try {
    const participantRef = db.collection("tournaments").doc(tournamentId).collection("participants").doc(userId);
    const participantSnap = await participantRef.get();

    if (!participantSnap.exists) {
      throw new functions.https.HttpsError("not-found", "Not joined this tournament");
    }

    if (participantSnap.data().submissionStatus !== "NOT_SUBMITTED") {
      throw new functions.https.HttpsError("failed-precondition", "Already submitted");
    }

    await participantRef.update({
      submissionStatus: "PENDING",
      submittedKills: kills,
      submittedRank: rank,
      screenshotUrl,
      submittedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return { success: true, message: "Result submitted for review" };
  } catch (err) {
    if (err instanceof functions.https.HttpsError) throw err;
    throw new functions.https.HttpsError("internal", err.message);
  }
});
