package com.stripe.integration.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.integration.entity.CheckoutForm;
import com.stripe.integration.entity.CustomerData;
import com.stripe.integration.service.StripeService;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Value("${stripe.key.secret}")
    String stripeKey;

    @Autowired
    StripeService stripeService;





    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/checkout")
    public String home(Model model){
        model.addAttribute("checkoutForm",new CheckoutForm());
        return "checkout";
    }

    @RequestMapping("/customer")
    public String getcustomer() {
        return "customer";
    }


    @RequestMapping("/index")
    public String index(Model model) throws StripeException {
        Stripe.apiKey = stripeKey;
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 6);

        CustomerCollection customers = Customer.list(params);
        List<CustomerData> allCustomer = new ArrayList<CustomerData>();
        for (int i = 0; i < customers.getData().size(); i++) {
            CustomerData customerData = new CustomerData();
            customerData.setCustomerId(customers.getData().get(i).getId());
            customerData.setName(customers.getData().get(i).getName());
            customerData.setEmail(customers.getData().get(i).getEmail());
            allCustomer.add(customerData);
        }
        model.addAttribute("customers", allCustomer);
        return "index";
    }



    @RequestMapping("/createCustomer")
    public String shoePage(){
        return "createcustomer";
    }


    @RequestMapping("/addCustomer")
    public String addCustomer(CustomerData customerData) throws StripeException {

        Stripe.apiKey = stripeKey;
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(customerData.getName())
                        .setEmail(customerData.getEmail())
                        .build();
        Customer customer = Customer.create(params);
        customerData.setCustomerId(customer.getId());
         stripeService.createCustomer(customerData);

        return "success";
    }

    @RequestMapping("/deleteCustomer/{id}")
    public String deleteCustomer(@PathVariable("id") String id) throws StripeException {
        Stripe.apiKey = stripeKey;

        Customer customer = Customer.retrieve(id);

        Customer deletedCustomer = customer.delete();
        return "success";
    }


}
