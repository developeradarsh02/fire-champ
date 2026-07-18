package com.firechamp.tournament.presentation.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * QR Code generator utility - ZXing library use karta hai.
 *
 * Usage:
 *   val bitmap = QRCodeGenerator.generate(text = "upi://pay?pa=...", sizePx = 512)
 *   val imageBitmap = bitmap.asImageBitmap()
 */
object QRCodeGenerator {

    /**
     * Generate QR code bitmap from text.
     *
     * @param text QR code me encode karna hai (e.g. UPI payment URL)
     * @param sizePx Width/height in pixels (default 512)
     * @return Android Bitmap (black/white QR code)
     */
    fun generate(
        text: String,
        sizePx: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val bitMatrix = QRCodeWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
            hints
        )

        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) {
                    foregroundColor
                } else {
                    backgroundColor
                }
            }
        }

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    /**
     * Generate UPI payment URL (standard UPI deep link format).
     *
     * @param upiId Merchant ka UPI ID (e.g. "firechamp@upi")
     * @param payeeName Merchant name
     * @param amount Amount in INR
     * @param transactionRef Optional reference
     */
    fun buildUpiPaymentUrl(
        upiId: String,
        payeeName: String,
        amount: Double,
        transactionRef: String = "TX${System.currentTimeMillis()}"
    ): String {
        return buildString {
            append("upi://pay?")
            append("pa=").append(upiId)
            append("&pn=").append(java.net.URLEncoder.encode(payeeName, "UTF-8"))
            append("&am=").append(amount)
            append("&cu=INR")
            append("&tn=").append(java.net.URLEncoder.encode("FireChamp Wallet Topup", "UTF-8"))
            append("&tr=").append(transactionRef)
        }
    }
}
