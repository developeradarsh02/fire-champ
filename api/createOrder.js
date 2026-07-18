const Razorpay = require('razorpay');

module.exports = async function handler(req, res) {
  if (req.method !== 'POST') return res.status(405).send('Method Not Allowed');

  // Env vars check - missing ho to saaf error do (crash ki jagah)
  const keyId = process.env.RAZORPAY_KEY_ID;
  const keySecret = process.env.RAZORPAY_KEY_SECRET;
  if (!keyId || !keySecret) {
    console.error('Missing RAZORPAY_KEY_ID / RAZORPAY_KEY_SECRET env vars');
    return res.status(500).json({ error: 'Payment service not configured' });
  }

  try {
    const { amount, notes } = req.body;

    if (!amount) return res.status(400).send('Amount is required');

    const razorpay = new Razorpay({ key_id: keyId, key_secret: keySecret });

    const order = await razorpay.orders.create({
      amount: amount * 100,
      currency: "INR",
      receipt: `rcpt_${Date.now()}`,
      notes: notes
    });

    res.status(200).json({
      orderId: order.id,
      key: keyId
    });
  } catch (error) {
    console.error('Razorpay Error:', error);
    res.status(500).send('Internal Server Error');
  }
};
