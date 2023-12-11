const options = {
    mode: 'subscription',
    amount: 1099,
    currency: 'usd',
    // Fully customizable with appearance API.
    appearance: {/*...*/},
};

// Set up Stripe.js and Elements to use in checkout form
const elements = stripe.elements(options);

// Create and mount the Payment Element
const paymentElement = elements.create('payment');
paymentElement.mount('#payment-element');
