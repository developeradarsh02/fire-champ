package com.firechamp.tournament.data.model

/**
 * Transaction type - wallet history me dikhta hai.
 */
enum class TransactionType {
    DEPOSIT,        // User ne money add kiya
    WITHDRAWAL,     // User ne withdrawal request kiya
    ENTRY_FEE,      // Tournament join karte time entry fee cuti
    PRIZE,          // Tournament jeetkar prize mila
    PER_KILL,       // Per-kill reward
    REFERRAL_BONUS  // Referral se bonus
}

enum class TransactionStatus { PENDING, COMPLETED, FAILED }

/**
 * Single transaction entry - wallet history me dikhti hai.
 */
data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,            // positive = credit, negative = debit
    val description: String,       // "Added via UPI", "Tournament entry fee", etc.
    val status: TransactionStatus,
    val timestamp: Long = System.currentTimeMillis()
)
