package com.firechamp.tournament.presentation.viewmodel.wallet

import androidx.lifecycle.ViewModel
import com.firechamp.tournament.data.model.Transaction
import com.firechamp.tournament.data.model.TransactionType
import com.firechamp.tournament.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import android.util.Log

import androidx.lifecycle.viewModelScope
import javax.inject.Inject

/**
 * Wallet screens ka shared UI state.
 *
 * Balance split (Task 8):
 *  - totalBalance: deposited + winning (for display)
 *  - depositedBalance: NOT withdrawable
 *  - winningBalance: WITHDRAWABLE
 *  - earnings: bonus + referral
 *  - payouts: total withdrawn
 */
data class WalletUiState(
    val totalBalance: Double = 0.0,
    val depositedBalance: Double = 0.0,
    val winningBalance: Double = 0.0,
    val earnings: Double = 0.0,
    val payouts: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),

    // Add Money
    val selectedAmount: String = "",
    val amountError: String? = null,

    // Payment QR
    val pendingAmount: Double = 0.0,
    val countdownSeconds: Int = 300,        // 5 minutes
    val isPaymentProcessing: Boolean = false,
    val paymentStatus: PaymentStatus = PaymentStatus.IDLE,

    // Withdrawal (Task 8)
    val withdrawalAmount: String = "",
    val withdrawalMethod: WithdrawalMethod = WithdrawalMethod.UPI,
    val upiId: String = "",
    val accountNumber: String = "",
    val ifscCode: String = "",
    val accountHolderName: String = "",
    val withdrawalError: String? = null,
    val lastWithdrawalMessage: String? = null
)

enum class PaymentStatus {
    IDLE, PROCESSING, SUCCESS, FAILED, EXPIRED
}

enum class WithdrawalMethod { UPI, BANK }

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        refreshBalance()
    }

    fun refreshBalance() {
        _uiState.update {
            it.copy(
                totalBalance = walletRepository.getTotalBalance(),
                depositedBalance = walletRepository.getDepositedBalance(),
                winningBalance = walletRepository.getWinningBalance(),
                earnings = walletRepository.earnings.value,
                payouts = walletRepository.payouts.value,
                transactions = walletRepository.transactions.value
            )
        }
    }

    fun processPayment(amount: Int, onResult: (orderId: String, key: String) -> Unit) {
        viewModelScope.launch {
            try {
                // Call Cloud Function to get order details
                val functions = com.google.firebase.functions.FirebaseFunctions.getInstance()
                val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                val data = hashMapOf(
                    "amount" to amount,
                    "notes" to hashMapOf("userId" to (user?.uid ?: "unknown"))
                )
                val result = functions.getHttpsCallable("createRazorpayOrder")
                    .call(data)
                    .await()
                
                val response = result.data as Map<String, Any>
                val orderId = response["orderId"] as String
                val key = response["key"] as String
                
                onResult(orderId, key)
            } catch (e: Exception) {
                Log.e("WalletViewModel", "Error creating order", e)
            }
        }
    }


    fun onAmountChange(value: String) {
        val filtered = value.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(selectedAmount = filtered, amountError = null) }
    }

    fun onPresetAmountSelect(amount: Int) {
        _uiState.update { it.copy(selectedAmount = amount.toString(), amountError = null) }
    }

    fun validateAmount(): Boolean {
        val amount = _uiState.value.selectedAmount.toDoubleOrNull()
        return when {
            amount == null -> { _uiState.update { it.copy(amountError = "Enter valid amount") }; false }
            amount < 10 -> { _uiState.update { it.copy(amountError = "Minimum ₹10") }; false }
            amount > 10000 -> { _uiState.update { it.copy(amountError = "Maximum ₹10,000") }; false }
            else -> {
                _uiState.update {
                    it.copy(pendingAmount = amount, paymentStatus = PaymentStatus.PROCESSING, countdownSeconds = 300)
                }
                startCountdown()
                true
            }
        }
    }

    private fun startCountdown() {
        viewModelScope.launch {
            while (_uiState.value.countdownSeconds > 0 && _uiState.value.paymentStatus == PaymentStatus.PROCESSING) {
                delay(1000)
                _uiState.update {
                    if (it.paymentStatus != PaymentStatus.PROCESSING) it
                    else it.copy(countdownSeconds = (it.countdownSeconds - 1).coerceAtLeast(0))
                }
            }
            if (_uiState.value.paymentStatus == PaymentStatus.PROCESSING) {
                _uiState.update { it.copy(paymentStatus = PaymentStatus.EXPIRED) }
            }
        }
    }

    fun onPaymentConfirmed() {
        _uiState.update { it.copy(isPaymentProcessing = true) }
        viewModelScope.launch {
            delay(1500)
            val amount = _uiState.value.pendingAmount
            walletRepository.addMoney(amount)
            _uiState.update {
                it.copy(
                    isPaymentProcessing = false,
                    paymentStatus = PaymentStatus.SUCCESS,
                    totalBalance = walletRepository.getTotalBalance(),
                    depositedBalance = walletRepository.getDepositedBalance()
                )
            }
        }
    }

    fun onPaymentFailed() {
        _uiState.update { it.copy(paymentStatus = PaymentStatus.FAILED) }
    }

    fun onRetryPayment() {
        _uiState.update { it.copy(paymentStatus = PaymentStatus.IDLE, countdownSeconds = 300, pendingAmount = 0.0) }
    }

    fun onPaymentDone() {
        _uiState.update {
            WalletUiState(
                totalBalance = walletRepository.getTotalBalance(),
                depositedBalance = walletRepository.getDepositedBalance(),
                winningBalance = walletRepository.getWinningBalance(),
                earnings = walletRepository.earnings.value,
                payouts = walletRepository.payouts.value,
                transactions = walletRepository.transactions.value
            )
        }
    }

    fun onCancelPayment() {
        _uiState.update { it.copy(paymentStatus = PaymentStatus.IDLE, pendingAmount = 0.0, countdownSeconds = 300) }
    }

    // ============== WITHDRAWAL FLOW (Task 8) ==============

    fun onWithdrawalAmountChange(v: String) {
        val filtered = v.filter { it.isDigit() || it == '.' }
        _uiState.update { it.copy(withdrawalAmount = filtered, withdrawalError = null) }
    }

    fun onWithdrawalMethodSelect(method: WithdrawalMethod) {
        _uiState.update { it.copy(withdrawalMethod = method, withdrawalError = null) }
    }

    fun onUpiIdChange(v: String) = _uiState.update { it.copy(upiId = v) }
    fun onAccountNumberChange(v: String) = _uiState.update { it.copy(accountNumber = v) }
    fun onIfscChange(v: String) = _uiState.update { it.copy(ifscCode = v) }
    fun onAccountHolderChange(v: String) = _uiState.update { it.copy(accountHolderName = v) }

    /**
     * Withdrawal submit karta hai.
     * Validation:
     *  - amount >= 100 (min withdrawal)
     *  - amount <= winning balance
     *  - KYC details present (UPI ID ya bank details)
     */
    fun submitWithdrawal() {
        val current = _uiState.value
        val amount = current.withdrawalAmount.toDoubleOrNull()

        if (amount == null) {
            _uiState.update { it.copy(withdrawalError = "Enter valid amount") }
            return
        }
        if (amount < 100) {
            _uiState.update { it.copy(withdrawalError = "Minimum withdrawal is ₹100") }
            return
        }
        if (amount > current.winningBalance) {
            _uiState.update { it.copy(withdrawalError = "Insufficient winning balance (only ₹${String.format("%.2f", current.winningBalance)} withdrawable)") }
            return
        }

        // KYC check
        if (current.withdrawalMethod == WithdrawalMethod.UPI && current.upiId.isBlank()) {
            _uiState.update { it.copy(withdrawalError = "Enter UPI ID") }
            return
        }
        if (current.withdrawalMethod == WithdrawalMethod.BANK) {
            if (current.accountNumber.isBlank() || current.ifscCode.isBlank() || current.accountHolderName.isBlank()) {
                _uiState.update { it.copy(withdrawalError = "Fill all bank details") }
                return
            }
        }

        val methodLabel = if (current.withdrawalMethod == WithdrawalMethod.UPI) "UPI: ${current.upiId}" else "Bank: ****${current.accountNumber.takeLast(4)}"
        val result = walletRepository.requestWithdrawal(amount, methodLabel)
        if (result == null) {
            _uiState.update { it.copy(withdrawalError = "Withdrawal failed. Please try again.") }
            return
        }

        // Success
        _uiState.update {
            it.copy(
                lastWithdrawalMessage = "✅ Withdrawal request of ₹${String.format("%.2f", amount)} submitted. Processing in 30 minutes.",
                withdrawalAmount = "",
                withdrawalError = null
            )
        }
        refreshBalance()
    }
}
