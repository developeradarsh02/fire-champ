/**
 * Fire Champ - Cloud Functions (Task 15)
 *
 * Ye sab server-side logic handle karta hai jo client-side se secure nahi ho sakta.
 * Specifically:
 *  - Wallet balance update (security: client directly balance nahi change kar sakta)
 *  - Tournament join (atomic transaction for slot increment + entry fee deduction)
 *  - Result verification (admin-validated)
 *  - Withdrawal processing
 *  - Push notifications
 *
 * Deploy: firebase deploy --only functions
 *
 * IMPORTANT: Ye Blaze (pay-as-you-go) plan pe chahiye, but free quota enough hai
 * small-medium scale ke liye.
 */

const admin = require("firebase-admin");
admin.initializeApp();

const db = admin.firestore();
const messaging = admin.messaging();

// ============ TOURNAMENT FUNCTIONS ============
const { onTournamentJoin, onResultVerified } = require("./tournaments/tournamentTriggers");
const { onWithdrawalRequest, onReferralBonus } = require("./wallet/walletTriggers");
const {
  sendMatchReminderNotification,
  onRoomIdUnlock,
} = require("./notifications/notificationSchedulers");

// ============ CALLABLE FUNCTIONS (Client se direct call) ============

/**
 * Join tournament - atomic transaction me:
 *  1. Entry fee deduct from depositedBalance
 *  2. Add user to participants sub-collection
 *  3. Increment slotsFilled
 *  4. Return success/failure
 */
exports.joinTournament = require("./tournaments/joinTournament");

/**
 * Submit result - user apna result submit karta hai.
 * Verification ke liye admin review queue me add hota hai.
 */
exports.submitResult = require("./tournaments/submitResult");

/**
 * Request withdrawal - user UPI/bank details ke saath withdrawal request karta hai.
 * Admin approval ke baad actual money transfer hota hai (manual UPI/bank).
 */
exports.requestWithdrawal = require("./wallet/requestWithdrawal");

/**
 * Create tournament - admin panel se tournament create.
 * Protected by admin custom claim.
 */
exports.createTournament = require("./tournaments/createTournament");

/**
 * Update tournament - admin panel se tournament edit (room ID set, etc.)
 */
exports.updateTournament = require("./tournaments/updateTournament");

/**
 * Approve/Reject withdrawal - admin se approval.
 */
exports.processWithdrawal = require("./wallet/processWithdrawal");

/**
 * Approve/Reject result - admin se verification.
 */
exports.processResult = require("./tournaments/processResult");

/**
 * Password reset via email OTP (Forgot Password flow).
 * 3 steps: sendPasswordResetOtp → verifyResetOtp → resetPasswordWithOtp
 */
const passwordReset = require("./auth/passwordReset");
exports.sendPasswordResetOtp = passwordReset.sendPasswordResetOtp;
exports.verifyResetOtp = passwordReset.verifyResetOtp;
exports.resetPasswordWithOtp = passwordReset.resetPasswordWithOtp;

// ============ FIRESTORE TRIGGERS (Background) ============

exports.onTournamentJoin = onTournamentJoin;
exports.onResultVerified = onResultVerified;
exports.onWithdrawalRequest = onWithdrawalRequest;
exports.onReferralBonus = onReferralBonus;
exports.sendMatchReminderNotification = sendMatchReminderNotification;
exports.onRoomIdUnlock = onRoomIdUnlock;
