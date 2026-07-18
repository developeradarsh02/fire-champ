/**
 * Wallet-related Firestore triggers.
 *
 *  - onWithdrawalRequest: User withdrawal request karta hai:
 *      - Check minimum amount
 *      - Check sufficient winningBalance
 *      - Hold the amount (locked from spending)
 *      - Create admin review record
 *  - onReferralBonus: Jab referred user pehla deposit karta hai:
 *      - Credit referrer's earnings balance
 *      - Update referral status
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const db = admin.firestore();

exports.onWithdrawalRequest = functions.firestore
  .document("withdrawalRequests/{requestId}")
  .onCreate(async (snapshot, context) => {
    const data = snapshot.data();
    if (data.status !== "PENDING") return;
    const { userId, amount } = data;

    try {
      const userRef = db.collection("users").doc(userId);
      const userSnap = await userRef.get();
      const user = userSnap.data();

      if (amount < 100) {
        await snapshot.ref.update({ status: "REJECTED", rejectionReason: "Minimum ₹100" });
        return;
      }
      if (amount > (user.winningBalance || 0)) {
        await snapshot.ref.update({ status: "REJECTED", rejectionReason: "Insufficient balance" });
        return;
      }

      // Hold the amount
      await userRef.update({
        winningBalance: admin.firestore.FieldValue.increment(-amount),
        payouts: admin.firestore.FieldValue.increment(amount),
      });

      // FCM to user
      if (user.fcmToken) {
        await admin.messaging().send({
          token: user.fcmToken,
          notification: {
            title: "Withdrawal Request Submitted",
            body: `₹${amount} withdrawal is being processed (30 min)`,
          },
          data: { type: "WITHDRAWAL_UPDATE" },
        });
      }
    } catch (err) {
      console.error("Error in onWithdrawalRequest:", err);
    }
  });

exports.onReferralBonus = functions.firestore
  .document("users/{userId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    const { userId } = context.params;

    // Detect first successful deposit (depositedBalance increased)
    if ((before.depositedBalance || 0) > 0 || (after.depositedBalance || 0) <= before.depositedBalance) {
      return;
    }

    const referredBy = after.referredBy;
    if (!referredBy) return;

    // Credit referrer ₹50
    try {
      const referrerRef = db.collection("users").doc(referredBy);
      const referrerSnap = await referrerRef.get();
      if (!referrerSnap.exists) return;

      await referrerRef.update({
        earnings: admin.firestore.FieldValue.increment(50),
        walletBalance: admin.firestore.FieldValue.increment(50),
      });

      // Add to referrer's referral history
      await referrerRef.collection("referrals").doc(userId).set({
        referredUser: after.username,
        bonusAmount: 50,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        status: "CREDITED",
      });

      // FCM to referrer
      const referrerData = referrerSnap.data();
      if (referrerData.fcmToken) {
        await admin.messaging().send({
          token: referrerData.fcmToken,
          notification: {
            title: "Referral Bonus! 🎉",
            body: `${after.username} joined with your code. ₹50 credited.`,
          },
          data: { type: "REFERRAL_BONUS" },
        });
      }
    } catch (err) {
      console.error("Error in onReferralBonus:", err);
    }
  });
