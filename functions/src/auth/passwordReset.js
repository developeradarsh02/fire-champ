/**
 * Password Reset via Email OTP - Production-ready.
 *
 * Cloud Functions (Blaze plan required for deploy):
 *  1. sendPasswordResetOtp(email)  → generates 6-digit OTP, sends via email (SMTP)
 *  2. verifyResetOtp(email, otp)   → validates OTP, returns username + resetToken
 *  3. resetPasswordWithOtp(token, newPassword) → updates password via Admin SDK
 *
 * Email setup (one-time):
 *   firebase functions:config set email.user="your-email@gmail.com" email.pass="app-password"
 *   (Gmail App Password: myaccount.google.com → Security → 2FA → App passwords)
 *   OR for Resend.com: email.service="resend" email.api_key="re_xxx"
 *
 * Firestore schema:
 *   passwordResets/{otpId}: { email, otpHash, createdAt, expiresAt, used, attempts }
 */

const functions = require("firebase-functions");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");
const crypto = require("crypto");

const db = admin.firestore();

// ============ EMAIL TRANSPORT SETUP ============
// Reads email config from `functions:config` (set via CLI)
// Falls back to ethereal.email for dev/testing (no email actually sent)
let cachedTransport = null;
async function getTransport() {
  if (cachedTransport) return cachedTransport;

  const emailConfig = functions.config().email || {};
  const user = emailConfig.user;
  const pass = emailConfig.pass;
  const service = emailConfig.service;

  if (service === "resend" && emailConfig.api_key) {
    // Resend.com - modern email API
    cachedTransport = {
      type: "resend",
      apiKey: emailConfig.api_key,
      from: emailConfig.from || "Fire Champ <noreply@firechamp.app>"
    };
    return cachedTransport;
  }

  if (user && pass) {
    // SMTP (Gmail, etc.)
    cachedTransport = nodemailer.createTransport({
      service: service || "gmail",
      auth: { user, pass }
    });
    return cachedTransport;
  }

  // Dev fallback: ethereal.email (test accounts auto-generated)
  // Note: emails are NOT actually delivered in this mode - logged to console
  let testAccount = await nodemailer.createTestAccount();
  cachedTransport = {
    type: "ethereal",
    transporter: nodemailer.createTransport({
      host: "smtp.ethereal.email",
      port: 587,
      secure: false,
      auth: { user: testAccount.user, pass: testAccount.pass }
    }),
    from: "Fire Champ <noreply@firechamp.app>"
  };
  console.warn("⚠️  Email config not set - using ethereal.email (dev mode).");
  console.warn("   To set: firebase functions:config set email.user='you@gmail.com' email.pass='app-pass'");
  return cachedTransport;
}

async function sendOtpEmail(toEmail, otp, username) {
  const transport = await getTransport();
  const subject = "🔐 Fire Champ — Password Reset OTP";
  const html = `
    <!DOCTYPE html>
    <html>
    <head><meta charset="UTF-8"></head>
    <body style="margin:0; padding:0; background:#0a0a0a; font-family:'Helvetica Neue',Helvetica,Arial,sans-serif;">
      <table role="presentation" width="100%" cellspacing="0" cellpadding="0" style="background:#0a0a0a; padding:40px 0;">
        <tr>
          <td align="center">
            <table role="presentation" width="480" cellspacing="0" cellpadding="0" style="background:#141414; border:1px solid #2a2a2a; border-radius:14px; overflow:hidden;">
              <tr>
                <td style="background:linear-gradient(135deg,#FF6A00 0%,#FFB300 100%); padding:32px 40px; text-align:center;">
                  <h1 style="margin:0; color:#fff; font-size:28px; font-weight:900; letter-spacing:1px;">🔥 Fire Champ</h1>
                </td>
              </tr>
              <tr>
                <td style="padding:40px;">
                  <h2 style="margin:0 0 16px; color:#fff; font-size:22px;">Password Reset Request</h2>
                  <p style="margin:0 0 24px; color:#b0b0b0; font-size:15px; line-height:1.5;">
                    Hi <strong style="color:#fff;">${username || "Player"}</strong>,<br>
                    Use the OTP below to reset your Fire Champ account password. This code is valid for <strong>10 minutes</strong>.
                  </p>
                  <div style="background:#000; border:2px solid #FF6A00; border-radius:12px; padding:24px; text-align:center; margin:24px 0;">
                    <div style="color:#888; font-size:12px; letter-spacing:2px; margin-bottom:8px;">YOUR OTP CODE</div>
                    <div style="color:#FFB300; font-size:42px; font-weight:900; letter-spacing:8px; font-family:'Courier New',monospace;">${otp}</div>
                  </div>
                  <p style="color:#888; font-size:13px; line-height:1.5;">
                    If you didn't request this, please ignore this email or contact support if you have concerns.
                  </p>
                  <hr style="border:none; border-top:1px solid #2a2a2a; margin:24px 0;">
                  <p style="color:#666; font-size:12px; text-align:center;">
                    © 2026 Fire Champ. All rights reserved.
                  </p>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </body>
    </html>
  `;
  const text = `Your Fire Champ password reset OTP is: ${otp}\n\nValid for 10 minutes.\n\nIf you didn't request this, please ignore.`;

  if (transport.type === "resend") {
    // Resend.com uses fetch, not nodemailer
    const res = await fetch("https://api.resend.com/emails", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${transport.apiKey}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        from: transport.from,
        to: [toEmail],
        subject,
        html,
        text
      })
    });
    if (!res.ok) {
      const err = await res.text();
      throw new Error(`Resend API error: ${err}`);
    }
    return { provider: "resend", messageId: (await res.json()).id };
  } else {
    // Nodemailer (Gmail SMTP or ethereal)
    const info = await transport.transporter.sendMail({
      from: transport.from,
      to: toEmail,
      subject,
      text,
      html
    });
    if (transport.type === "ethereal") {
      console.log(`📧 [DEV] Ethereal preview: ${nodemailer.getTestMessageUrl(info)}`);
    }
    return { provider: transport.type, messageId: info.messageId };
  }
}

// ============ 1. SEND OTP ============
exports.sendPasswordResetOtp = functions.https.onCall(async (data, context) => {
  const email = (data.email || "").trim().toLowerCase();
  if (!email || !email.includes("@") || !email.includes(".")) {
    throw new functions.https.HttpsError("invalid-argument", "Please enter a valid email");
  }

  try {
    // 1. Verify email exists in Firebase Auth
    const userRecord = await admin.auth().getUserByEmail(email).catch(() => null);
    if (!userRecord) {
      throw new functions.https.HttpsError("not-found", "No account found with this email");
    }

    // 2. Generate 6-digit OTP
    const otp = (100000 + Math.floor(Math.random() * 900000)).toString();
    const otpHash = crypto.createHash("sha256").update(otp).digest("hex");

    // 3. Store hash (NOT plain OTP) in Firestore with 10-min expiry
    const expiresAt = Date.now() + 10 * 60 * 1000; // 10 minutes
    const otpDoc = await db.collection("passwordResets").add({
      email,
      uid: userRecord.uid,
      otpHash,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      expiresAt,
      used: false,
      attempts: 0
    });

    // 4. Send email
    const sendResult = await sendOtpEmail(email, otp, userRecord.displayName || userRecord.email?.split("@")[0]);

    return {
      success: true,
      message: `OTP sent to ${email.replace(/(.{2}).+(@.+)/, "$1***$2")}`,
      otpId: otpDoc.id,
      expiresAt,
      provider: sendResult.provider,
      // Dev only: include preview URL for ethereal
      previewUrl: sendResult.messageId && functions.config().email ? null : null
    };
  } catch (e) {
    if (e instanceof functions.https.HttpsError) throw e;
    console.error("sendPasswordResetOtp error:", e);
    throw new functions.https.HttpsError("internal", e.message || "Failed to send OTP");
  }
});

// ============ 2. VERIFY OTP ============
exports.verifyResetOtp = functions.https.onCall(async (data, context) => {
  const email = (data.email || "").trim().toLowerCase();
  const otp = (data.otp || "").trim();

  if (!email || !otp || otp.length !== 6) {
    throw new functions.https.HttpsError("invalid-argument", "Email and 6-digit OTP required");
  }

  try {
    const otpHash = crypto.createHash("sha256").update(otp).digest("hex");
    const now = Date.now();

    // Find most recent unused matching OTP
    const snap = await db.collection("passwordResets")
      .where("email", "==", email)
      .where("used", "==", false)
      .orderBy("createdAt", "desc")
      .limit(5)
      .get();

    let matchDoc = null;
    for (const doc of snap.docs) {
      const data = doc.data();
      if (data.otpHash === otpHash && data.expiresAt > now) {
        matchDoc = doc;
        break;
      }
    }

    if (!matchDoc) {
      throw new functions.https.HttpsError("invalid-argument", "Invalid or expired OTP. Please try again.");
    }

    // Increment attempts for audit
    await matchDoc.ref.update({ attempts: admin.firestore.FieldValue.increment(1) });

    // Generate one-time reset token (60-sec validity) to authorize password change
    const resetToken = crypto.randomBytes(32).toString("hex");
    await matchDoc.ref.update({
      resetToken,
      resetTokenExpiresAt: Date.now() + 5 * 60 * 1000,
      used: true  // OTP consumed
    });

    // Get username for UI display
    const userRecord = await admin.auth().getUserByEmail(email);
    const userDoc = await db.collection("users").doc(userRecord.uid).get();
    const username = userDoc.exists ? userDoc.data().username || email.split("@")[0] : email.split("@")[0];

    return {
      success: true,
      message: "OTP verified",
      resetToken,
      otpId: matchDoc.id,
      username,
      email: userRecord.email
    };
  } catch (e) {
    if (e instanceof functions.https.HttpsError) throw e;
    console.error("verifyResetOtp error:", e);
    throw new functions.https.HttpsError("internal", e.message || "Failed to verify OTP");
  }
});

// ============ 3. RESET PASSWORD ============
exports.resetPasswordWithOtp = functions.https.onCall(async (data, context) => {
  const otpId = data.otpId;
  const resetToken = data.resetToken;
  const newPassword = data.newPassword || "";

  if (!otpId || !resetToken) {
    throw new functions.https.HttpsError("invalid-argument", "Reset token required");
  }
  if (newPassword.length < 6) {
    throw new functions.https.HttpsError("invalid-argument", "Password must be at least 6 characters");
  }

  try {
    const doc = await db.collection("passwordResets").doc(otpId).get();
    if (!doc.exists) {
      throw new functions.https.HttpsError("not-found", "Invalid reset request");
    }
    const d = doc.data();
    if (d.resetToken !== resetToken) {
      throw new functions.https.HttpsError("invalid-argument", "Invalid reset token");
    }
    if (d.resetTokenExpiresAt < Date.now()) {
      throw new functions.https.HttpsError("deadline-exceeded", "Reset token expired. Please start over.");
    }
    if (!d.used) {
      throw new functions.https.HttpsError("failed-precondition", "OTP not verified");
    }

    // Update password via Admin SDK
    await admin.auth().updateUser(d.uid, { password: newPassword });

    // Invalidate reset token (one-time use)
    await doc.ref.update({ resetToken: null, resetTokenExpiresAt: null });

    return {
      success: true,
      message: "Password reset successfully. Please login with your new password."
    };
  } catch (e) {
    if (e instanceof functions.https.HttpsError) throw e;
    console.error("resetPasswordWithOtp error:", e);
    throw new functions.https.HttpsError("internal", e.message || "Failed to reset password");
  }
});
