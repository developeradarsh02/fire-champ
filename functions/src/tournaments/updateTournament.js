/**
 * updateTournament - Admin tournament fields update karta hai
 * (room ID set, status change, etc.)
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.updateTournament = functions.https.onCall(async (data, context) => {
  if (!context.auth || !context.auth.token.admin) {
    throw new functions.https.HttpsError("permission-denied", "Admin only");
  }
  const { tournamentId, updates } = data;
  if (!tournamentId || !updates) {
    throw new functions.https.HttpsError("invalid-argument", "Missing fields");
  }
  await db.collection("tournaments").doc(tournamentId).update(updates);
  return { success: true };
});
