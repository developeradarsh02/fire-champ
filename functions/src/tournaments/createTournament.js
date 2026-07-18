/**
 * createTournament - Admin panel se tournament create karta hai.
 * Admin custom claim required.
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.createTournament = functions.https.onCall(async (data, context) => {
  if (!context.auth || !context.auth.token.admin) {
    throw new functions.https.HttpsError("permission-denied", "Admin only");
  }

  const { title, gameModeId, mode, map, dateTime, prizePool, perKill, entryFee, totalSlots, bannerUrl } = data;
  const newTournament = {
    title, gameModeId, mode, map, dateTime,
    prizePool, perKill, entryFee, totalSlots,
    bannerUrl: bannerUrl || null,
    slotsFilled: 0,
    status: "UPCOMING",
    roomId: null,
    roomPassword: null,
    roomUnlockTime: null,
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    createdBy: context.auth.uid,
  };
  const ref = await db.collection("tournaments").add(newTournament);
  return { success: true, tournamentId: ref.id };
});
