const crypto = require('crypto');
const admin = require('firebase-admin');

// Service Account JSON ko Vercel Environment Variable mein 'FIREBASE_SERVICE_ACCOUNT' naam se save karein
const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);

if (!admin.apps.length) {
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount)
    });
}

const db = admin.firestore();

export default async function handler(req, res) {
    if (req.method !== 'POST') return res.status(405).send('Method Not Allowed');

    const signature = req.headers['x-razorpay-signature'];
    const WEBHOOK_SECRET = process.env.RAZORPAY_WEBHOOK_SECRET;

    const expectedSignature = crypto
        .createHmac('sha256', WEBHOOK_SECRET)
        .update(JSON.stringify(req.body))
        .digest('hex');

    if (signature !== expectedSignature) return res.status(400).send('Invalid signature');

    const event = req.body.event;
    const payment = req.body.payload.payment.entity;

    if (event === 'payment.captured') {
        try {
            await db.collection('users').doc(payment.notes.userId).update({
                balance: admin.firestore.FieldValue.increment(payment.amount / 100)
            });
            return res.status(200).send('OK');
        } catch (error) {
            return res.status(500).send('Database Error');
        }
    }
    return res.status(200).send('Event ignored');
}
