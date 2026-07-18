package com.firechamp.tournament.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Password Reset via Cloud Functions (production OTP flow).
 *
 * Flow:
 *  1. sendOtp(email) → calls Cloud Function `sendPasswordResetOtp`
 *     → generates 6-digit OTP, sends via email (SMTP/Resend)
 *     → returns otpId for tracking
 *  2. verifyOtp(email, otp) → calls Cloud Function `verifyResetOtp`
 *     → validates OTP, returns otpId + resetToken + username
 *  3. resetPassword(otpId, resetToken, newPassword) → calls `resetPasswordWithOtp`
 *     → Admin SDK updates password
 *
 * Real production: User receives email with OTP code, enters it, sets new password.
 * No OTP shown in UI (unlike dev mode).
 */
@Singleton
class PasswordResetRepository @Inject constructor() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()

    data class SendOtpResult(
        val success: Boolean,
        val message: String,
        val otpId: String? = null,
        val expiresAt: Long? = null
    )

    data class VerifyOtpResult(
        val success: Boolean,
        val message: String,
        val otpId: String? = null,
        val resetToken: String? = null,
        val username: String? = null
    )

    data class ResetPasswordResult(
        val success: Boolean,
        val message: String
    )

    /**
     * Step 1: Send OTP via email.
     * Cloud Function generates OTP + sends email, returns otpId.
     */
    suspend fun sendOtp(email: String): SendOtpResult {
        return try {
            val result = functions
                .getHttpsCallable("sendPasswordResetOtp")
                .call(mapOf("email" to email.trim().lowercase()))
                .await()

            @Suppress("UNCHECKED_CAST")
            val data = result.data as Map<String, Any>
            SendOtpResult(
                success = data["success"] as? Boolean ?: false,
                message = data["message"] as? String ?: "OTP sent",
                otpId = data["otpId"] as? String,
                expiresAt = (data["expiresAt"] as? Number)?.toLong()
            )
        } catch (e: Exception) {
            SendOtpResult(
                success = false,
                message = parseFirebaseError(e)
            )
        }
    }

    /**
     * Step 2: Verify OTP.
     * Returns resetToken that authorizes password change.
     */
    suspend fun verifyOtp(email: String, otp: String): VerifyOtpResult {
        return try {
            val result = functions
                .getHttpsCallable("verifyResetOtp")
                .call(mapOf(
                    "email" to email.trim().lowercase(),
                    "otp" to otp
                ))
                .await()

            @Suppress("UNCHECKED_CAST")
            val data = result.data as Map<String, Any>
            VerifyOtpResult(
                success = data["success"] as? Boolean ?: false,
                message = data["message"] as? String ?: "Verified",
                otpId = data["otpId"] as? String,
                resetToken = data["resetToken"] as? String,
                username = data["username"] as? String
            )
        } catch (e: Exception) {
            VerifyOtpResult(
                success = false,
                message = parseFirebaseError(e)
            )
        }
    }

    /**
     * Step 3: Reset password using verified resetToken.
     */
    suspend fun resetPassword(
        otpId: String,
        resetToken: String,
        newPassword: String
    ): ResetPasswordResult {
        return try {
            val result = functions
                .getHttpsCallable("resetPasswordWithOtp")
                .call(mapOf(
                    "otpId" to otpId,
                    "resetToken" to resetToken,
                    "newPassword" to newPassword
                ))
                .await()

            @Suppress("UNCHECKED_CAST")
            val data = result.data as Map<String, Any>
            ResetPasswordResult(
                success = data["success"] as? Boolean ?: false,
                message = data["message"] as? String ?: "Password reset"
            )
        } catch (e: Exception) {
            ResetPasswordResult(
                success = false,
                message = parseFirebaseError(e)
            )
        }
    }

    /**
     * Parse Firebase Callable error into human-readable message.
     */
    private fun parseFirebaseError(e: Exception): String {
        val msg = e.message ?: "Operation failed"
        return when {
            msg.contains("UNAVAILABLE") -> "Network error. Check your internet connection."
            msg.contains("DEADLINE_EXCEEDED") -> "Request timed out. Please try again."
            msg.contains("NOT_FOUND") -> "No account found with this email."
            msg.contains("INVALID_ARGUMENT") -> msg.substringAfter("INVALID_ARGUMENT:").trim()
            else -> msg
        }
    }
}
