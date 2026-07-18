package com.firechamp.tournament.data.model

/**
 * User data model - already includes wallet balance fields
 * taaki Home screen (Task 2) me bina refactoring ke wallet
 * balance show kar sakein.
 *
 * Important (Task 8): wallet split into:
 *  - depositedBalance: user ne jo add kiya (withdraw nahi ho sakta)
 *  - winningBalance: tournament se jeeta hua (withdraw ho sakta hai)
 *  - earnings: total earnings (referral + bonus)
 *  - payouts: total withdrawn amount
 */
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val mobile: String = "",
    val countryCode: String = "+91",
    val referralCode: String = "",

    // Wallet balance split (Task 5 + Task 8)
    val walletBalance: Double = 0.0,           // Total (deposited + winning) for display
    val depositedBalance: Double = 0.0,        // Added by user (NOT withdrawable)
    val winningBalance: Double = 0.0,          // Tournament winnings (WITHDRAWABLE)
    val earnings: Double = 0.0,                // Referral + bonus earnings
    val payouts: Double = 0.0,                  // Total withdrawn amount

    // Profile fields
    val dob: String = "",                       // Date of birth (DD/MM/YYYY)
    val gender: String = "",                    // Male / Female
    val isVerified: Boolean = false,
    val matchesPlayed: Int = 0,
    val totalKilled: Int = 0,
    val coinsWon: Double = 0.0,

    val createdAt: Long = System.currentTimeMillis()
) {
    /** Available withdrawal amount = winning balance. */
    val withdrawableBalance: Double get() = winningBalance
}
