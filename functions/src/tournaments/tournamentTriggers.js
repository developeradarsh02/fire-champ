/**
 * Tournament-related Firestore triggers.
 *
 *  - onTournamentJoin: Jab user join karta hai, additional side-effects:
 *      - Update user's totalKilled stats placeholder
 *      - Add to user's match history
 *  - onResultVerified: Jab admin result verify karta hai:
 *      - Calculate winningAmount (perKill * kills + prize if rank wins)
 *      - Add to user.winningBalance
 *      - Add transaction record
 *      - Add to tournament.results (for public results)
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.onTournamentJoin = functions.firestore
  .document("tournaments/{tournamentId}/participants/{userId}")
  .onCreate(async (snapshot, context) => {
    const { tournamentId, userId } = context.params;
    const participantData = snapshot.data();

    try {
      // Add to user's match history
      await db.collection("users").doc(userId).collection("joinedMatches").doc(tournamentId).set({
        tournamentId,
        joinedAt: participantData.joinedAt,
        matchId: tournamentId,
      });
      console.log(`User ${userId} joined ${tournamentId}`);
    } catch (err) {
      console.error("Error in onTournamentJoin:", err);
    }
  });

exports.onResultVerified = functions.firestore
  .document("tournaments/{tournamentId}/participants/{userId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    const { tournamentId, userId } = context.params;

    // Only act when status changes to VERIFIED
    if (before.submissionStatus === "VERIFIED" || after.submissionStatus !== "VERIFIED") {
      return;
    }

    try {
      const tournamentRef = db.collection("tournaments").doc(tournamentId);
      const tournamentSnap = await tournamentRef.get();
      const tournament = tournamentSnap.data();

      // Calculate winning
      const perKillReward = (tournament.perKill || 0) * (after.submittedKills || 0);
      const rankPrize = (after.submittedRank === 1) ? Math.floor(tournament.prizePool * 0.5) :
                        (after.submittedRank === 2) ? Math.floor(tournament.prizePool * 0.3) :
                        (after.submittedRank === 3) ? Math.floor(tournament.prizePool * 0.2) : 0;
      const totalWinning = perKillReward + rankPrize;

      // Update participant with winning amount
      await change.after.ref.update({ winningAmount: totalWinning });

      // Update user's winning balance
      const userRef = db.collection("users").doc(userId);
      await userRef.update({
        winningBalance: admin.firestore.FieldValue.increment(totalWinning),
      });

      // Add transaction
      await db.collection("transactions").add({
        userId,
        type: "PRIZE",
        amount: totalWinning,
        description: `Prize: ${tournament.title} (Rank #${after.submittedRank}, ${after.submittedKills} kills)`,
        status: "COMPLETED",
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
        tournamentId,
      });

      // Add to public results
      await tournamentRef.collection("results").doc(userId).set({
        rank: after.submittedRank,
        playerName: (await userRef.get()).data().username,
        kills: after.submittedKills,
        winningAmount: totalWinning,
        verifiedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      // Send FCM notification
      const userSnap = await userRef.get();
      const fcmToken = userSnap.data().fcmToken;
      if (fcmToken) {
        await admin.messaging().send({
          token: fcmToken,
          notification: {
            title: "Result Verified ✅",
            body: `You won ₹${totalWinning} in ${tournament.title}`,
          },
          data: { type: "RESULT_DECLARED", tournamentId },
        });
      }
    } catch (err) {
      console.error("Error in onResultVerified:", err);
    }
  });
