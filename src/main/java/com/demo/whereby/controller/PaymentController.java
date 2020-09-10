package com.demo.whereby.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentController {

    @Autowired
    private RazorpayClient razorpayClient;

    @PostMapping("/orders")
    public String createNewOrder(@Value("paymentKeyId") String key, Model model) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("amount", 100);
        options.put("currency", "INR");
        options.put("receipt", "txn_123456");
        Order order = razorpayClient.Orders.create(options);
        System.out.println(order);
        model.addAttribute("key",key);
        model.addAttribute("order",order.get("id"));
        return "payment";
    }
}

//        razorpay_payment_id=pay_FbRaMDvDqhZzU5
//        razorpay_order_id=order_FbRXVtlDvTH4Wd
//        razorpay_signature=c1aaa911f67600f7031a7074344d49ea76493ff82fb1d395ff666d84b26e538e













