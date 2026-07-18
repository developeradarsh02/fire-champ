/**
 * Scheduled notification functions.
 *
 *  - sendMatchReminderNotification: Har 5 min me check karta hai,
 *      15 min pehle sab participants ko reminder bhejta hai
 *  - onRoomIdUnlock: Match se 10 min pehle room ID reveal karta hai
 *      aur participants ko notification bhejta hai
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

// Har 5 min chalega (Cloud Scheduler)
exports.sendMatchReminderNotification = functions.pubsub
  .schedule("every 5 minutes")
  .timeZone("Asia/Kolkata")
  .onRun(async (context) => {
    const now = admin.firestore.Timestamp.now();
    const fifteenMinLater = new Date(Date.now() + 15 * 60 * 1000);
    const twentyMinLater = new Date(Date.now() + 20 * 60 * 1000);

    const tournamentsSnap = await db.collection("tournaments")
      .where("status", "==", "UPCOMING")
      .where("dateTime", ">=", fifteenMinLater.toISOString())
      .where("dateTime", "<=", twentyMinLater.toISOString())
      .get();

    const messages = [];
    for (const tourDoc of tournamentsSnap.docs) {
      const participantsSnap = await tourDoc.ref.collection("participants").get();
      for (const p of participantsSnap.docs) {
        const userSnap = await db.collection("users").doc(p.id).get();
        const user = userSnap.data();
        if (user && user.fcmToken) {
          messages.push({
            token: user.fcmToken,
            notification: {
              title: "Match starting in 15 min! ⏰",
              body: `${tourDoc.data().title} - Get ready!`,
            },
            data: { type: "MATCH_REMINDER", tournamentId: tourDoc.id },
          });
        }
      }
    }

    if (messages.length > 0) {
      const response = await admin.messaging().sendEach(messages);
      console.log(`Sent ${response.successCount} match reminders`);
    }
  });

// Har min chalega - room ID reveal karta hai
exports.onRoomIdUnlock = functions.pubsub
  .schedule("every 1 minutes")
  .timeZone("Asia/Kolkata")
  .onRun(async (context) => {
    const now = Date.now();
    const tournamentsSnap = await db.collection("tournaments")
      .where("status", "==", "UPCOMING")
      .get();

    for (const tourDoc of tournamentsSnap.docs) {
      const tour = tourDoc.data();
      // If match starts in 10 min, room ID is set (admin already added it)
      // Send notification
      if (tour.roomId && tour.roomUnlockTime) {
        const unlockTime = new Date(tour.roomUnlockTime).getTime();
        if (unlockTime <= now && unlockTime > now - 60_000) {
          // Just unlocked, send notifications
          const participantsSnap = await tourDoc.ref.collection("participants").get();
          const messages = [];
          for (const p of participantsSnap.docs) {
            const userSnap = await db.collection("users").doc(p.id).get();
            const user = userSnap.data();
            if (user && user.fcmToken) {
              messages.push({
                token: user.fcmToken,
                notification: {
                  title: "Room ID Available 🔓",
                  body: `${tour.title} - Check Joined Matches`,
                },
                data: { type: "ROOM_ID_RELEASED", tournamentId: tourDoc.id },
              });
            }
          }
          if (messages.length > 0) {
            await admin.messaging().sendEach(messages);
            console.log(`Room unlock notifications sent: ${messages.length}`);
          }
        }
      }
    }
  });
