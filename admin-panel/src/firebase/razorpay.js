/**
 * Razorpay config - Reference for admin panel & future integrations.
 *
 * NOTE: Current Razorpay payment flow runs entirely in the Android app
 * (RazorpayCheckoutScreen.kt → WebView with checkout.razorpay.com/v1/checkout.js).
 * Cloud Function (functions/src/wallet/razorpayOrder.js) handles order creation
 * and signature verification server-side.
 *
 * This file is kept for reference / future admin-side payment management
 * (refunds, payment links, etc).
 *
 * Live key (production): rzp_live_TDbxmUxMQey2Si
 * Test key (sandbox):    rzp_test_xxxxxxxxxxxxx
 * Webhook secret:        (set in Razorpay Dashboard → Webhooks)
 *
 * Set in Firebase Functions config:
 *   firebase functions:config:set razorpay.key_id="rzp_live_..." razorpay.key_secret="..."
 */
export const RAZORPAY_CONFIG = {
  // Test key (sandbox) - use for local development / testing
  TEST_KEY_ID: 'rzp_test_xxxxxxxxxxxxx',

  // Live key (production) - real money transactions
  LIVE_KEY_ID: 'rzp_live_TDbxmUxMQey2Si',

  // Razorpay API base
  API_BASE: 'https://api.razorpay.com/v1',

  // Live mode flag - true = real payments, false = sandbox
  IS_LIVE: true
}

export const getActiveKey = () => {
  return RAZORPAY_CONFIG.IS_LIVE ? RAZORPAY_CONFIG.LIVE_KEY_ID : RAZORPAY_CONFIG.TEST_KEY_ID
}

export const getRazorpayKey = () => RAZORPAY_CONFIG.LIVE_KEY_ID
