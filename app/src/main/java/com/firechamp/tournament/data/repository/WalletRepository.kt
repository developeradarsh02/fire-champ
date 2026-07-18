package com.firechamp.tournament.data.repository

import com.firechamp.tournament.data.model.Transaction
import com.firechamp.tournament.data.model.TransactionStatus
import com.firechamp.tournament.data.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wallet Repository - abhi MOCK data.
 *
 * Real me ye Firebase Firestore + Cloud Functions ke through balance update hoga
 * (security ke liye balance direct client se update nahi hona chahiye).
 *
 * Balance split (Task 8):
 *  - depositedBalance: user ne jo add kiya (NOT withdrawable)
 *  - winningBalance: tournament jeeta hua (WITHdrawable)
 *  - earnings: referral + bonus
 *  - payouts: total withdrawn
 */
@Singleton
class WalletRepository @Inject constructor() {

    private val _depositedBalance = MutableStateFlow(0.0)
    val depositedBalance: StateFlow<Double> = _depositedBalance.asStateFlow()

    private val _winningBalance = MutableStateFlow(0.0)
    val winningBalance: StateFlow<Double> = _winningBalance.asStateFlow()

    private val _earnings = MutableStateFlow(0.0)
    val earnings: StateFlow<Double> = _earnings.asStateFlow()

    private val _payouts = MutableStateFlow(0.0)
    val payouts: StateFlow<Double> = _payouts.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    /** Total balance (display only - withdrawable sirf winning hai). */
    fun getTotalBalance(): Double = _depositedBalance.value + _winningBalance.value
    fun getWinningBalance(): Double = _winningBalance.value
    fun getDepositedBalance(): Double = _depositedBalance.value

    /**
     * Mock deposit - simulate karta hai user ne money add kiya.
     * Real me payment gateway (Razorpay, Cashfree, etc.) ke callback se hoga.
     */
    fun addMoney(amount: Double): Transaction {
        val transaction = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            type = TransactionType.DEPOSIT,
            amount = amount,
            description = "Added via UPI",
            status = TransactionStatus.COMPLETED
        )
        _depositedBalance.value += amount
        _transactions.value = listOf(transaction) + _transactions.value
        return transaction
    }

    /**
     * Entry fee deduct - tournament join karte time.
     * Real me Cloud Function se hoga (Task 15 me).
     * Note: Entry fee deposited balance se deduct hota hai (winning se nahi).
     */
    fun deductEntryFee(amount: Double, tournamentTitle: String): Transaction {
        val transaction = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            type = TransactionType.ENTRY_FEE,
            amount = -amount,
            description = "Entry: ${tournamentTitle.take(30)}",
            status = TransactionStatus.COMPLETED
        )
        _depositedBalance.value = (_depositedBalance.value - amount).coerceAtLeast(0.0)
        _transactions.value = listOf(transaction) + _transactions.value
        return transaction
    }

    /**
     * Prize credit - tournament jeetne par (winning balance me add).
     */
    fun creditPrize(amount: Double, tournamentTitle: String): Transaction {
        val transaction = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            type = TransactionType.PRIZE,
            amount = amount,
            description = "Prize: ${tournamentTitle.take(30)}",
            status = TransactionStatus.COMPLETED
        )
        _winningBalance.value += amount
        _transactions.value = listOf(transaction) + _transactions.value
        return transaction
    }

    /**
     * Withdrawal request - Task 8 me use hoga.
     * Returns null agar withdrawal possible nahi hai (insufficient winning balance).
     */
    fun requestWithdrawal(amount: Double, method: String): Transaction? {
        if (amount > _winningBalance.value) return null
        val transaction = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            type = TransactionType.WITHDRAWAL,
            amount = -amount,
            description = "Withdrawal via $method (Pending)",
            status = TransactionStatus.PENDING
        )
        // Hold the amount (not deducted yet - admin approval ke baad deduct hoga)
        _winningBalance.value -= amount
        _payouts.value += amount
        _transactions.value = listOf(transaction) + _transactions.value
        return transaction
    }
}
