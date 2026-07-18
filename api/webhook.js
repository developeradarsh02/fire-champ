const crypto = require('crypto');
const admin = require('firebase-admin');

// Firebase Admin init - lazily, taaki env var missing par module load hi na tute
function getDb() {
    if (!admin.apps.length) {
        // Service Account JSON ko Vercel Environment Variable mein
        // 'FIREBASE_SERVICE_ACCOUNT' naam se save karein
        const raw = process.env.FIREBASE_SERVICE_ACCOUNT;
        if (!raw) throw new Error('FIREBASE_SERVICE_ACCOUNT env var missing');
        const serviceAccount = JSON.parse(raw);
        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount)
        });
    }
    return admin.firestore();
}

module.exports = async function handler(req, res) {
    if (req.method !== 'POST') return res.status(405).send('Method Not Allowed');

    const WEBHOOK_SECRET = process.env.RAZORPAY_WEBHOOK_SECRET;
    if (!WEBHOOK_SECRET) {
        console.error('Missing RAZORPAY_WEBHOOK_SECRET env var');
        return res.status(500).json({ error: 'Webhook not configured' });
    }

    const signature = req.headers['x-razorpay-signature'];

    const expectedSignature = crypto
        .createHmac('sha256', WEBHOOK_SECRET)
        .update(JSON.stringify(req.body))
        .digest('hex');

    if (signature !== expectedSignature) return res.status(400).send('Invalid signature');

    const event = req.body.event;
    const payment = req.body.payload.payment.entity;

    if (event === 'payment.captured') {
        try {
            const db = getDb();
            const amountRs = payment.amount / 100;
            // App ka wallet model: walletBalance (total) + depositedBalance (added by user)
            await db.collection('users').doc(payment.notes.userId).update({
                walletBalance: admin.firestore.FieldValue.increment(amountRs),
                depositedBalance: admin.firestore.FieldValue.increment(amountRs)
            });
            return res.status(200).send('OK');
        } catch (error) {
            console.error('Webhook DB error:', error);
            return res.status(500).send('Database Error');
        }
    }
    return res.status(200).send('Event ignored');
};
