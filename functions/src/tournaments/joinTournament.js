/**
 * joinTournament - Client (Android) is callable function ko call karega.
 *
 * Flow:
 *  1. User authenticated hai? (auth check)
 *  2. Tournament exists aur slots available?
 *  3. User ke paas sufficient depositedBalance hai?
 *  4. Atomic transaction:
 *      - User.depositedBalance -= entryFee
 *      - Tournament.slotsFilled += 1
 *      - participants sub-collection me add
 *      - transactions me record add
 *  5. Return { success: true, joinedAt: timestamp }
 */
const functions = require("firebase-functions");
const admin = require("firebase-admin");

exports.joinTournament = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Login required");
  }

  const { tournamentId } = data;
  const userId = context.auth.uid;
  const db = admin.firestore();

  try {
    const result = await db.runTransaction(async (transaction) => {
      const tournamentRef = db.collection("tournaments").doc(tournamentId);
      const userRef = db.collection("users").doc(userId);
      const participantRef = tournamentRef.collection("participants").doc(userId);

      const [tournamentSnap, userSnap, participantSnap] = await Promise.all([
        transaction.get(tournamentRef),
        transaction.get(userRef),
        transaction.get(participantRef),
      ]);

      if (!tournamentSnap.exists) {
        throw new functions.https.HttpsError("not-found", "Tournament not found");
      }

      if (participantSnap.exists) {
        throw new functions.https.HttpsError("already-exists", "Already joined");
      }

      const tournament = tournamentSnap.data();
      const user = userSnap.data();

      if (tournament.slotsFilled >= tournament.totalSlots) {
        throw new functions.https.HttpsError("resource-exhausted", "Tournament full");
      }

      if ((user.depositedBalance || 0) < tournament.entryFee) {
        throw new functions.https.HttpsError("failed-precondition", "Insufficient balance");
      }

      // Atomic updates
      transaction.update(userRef, {
        depositedBalance: admin.firestore.FieldValue.increment(-tournament.entryFee),
      });
      transaction.update(tournamentRef, {
        slotsFilled: admin.firestore.FieldValue.increment(1),
      });
      transaction.set(participantRef, {
        userId,
        joinedAt: admin.firestore.FieldValue.serverTimestamp(),
        submissionStatus: "NOT_SUBMITTED",
        submittedKills: 0,
        submittedRank: 0,
        screenshotUrl: null,
        winningAmount: 0,
      });
      transaction.set(db.collection("transactions").doc(), {
        userId,
        type: "ENTRY_FEE",
        amount: -tournament.entryFee,
        description: `Entry: ${tournament.title}`,
        status: "COMPLETED",
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
        tournamentId,
      });

      return { success: true, tournamentTitle: tournament.title };
    });

    return result;
  } catch (error) {
    if (error instanceof functions.https.HttpsError) throw error;
    throw new functions.https.HttpsError("internal", error.message);
  }
});
