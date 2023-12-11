package com.stripe.integration.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.integration.dto.CheckoutForm;
import com.stripe.integration.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CheckOutController {

    @Value("${stripe.key.public}")
    String stripePublicKey;

    @Value("${stripe.key.secret}")
    String stripeKey;

    @Autowired
    StripeService stripeService;

    @PostMapping("/checkoutp")
    public String checkout(@ModelAttribute
                           @Valid CheckoutForm checkoutForm,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "checkout";
        }
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("amount", checkoutForm.getAmount());
        model.addAttribute("email", checkoutForm.getEmail());
        model.addAttribute("featureRequest", checkoutForm.getFeatureRequest());
        return "checkoutpage";
    }

    @RequestMapping("/create-checkout-session")
    public void createSession() throws StripeException {
        Stripe.apiKey = stripeKey;
        stripeService.createSession();
    }
}
