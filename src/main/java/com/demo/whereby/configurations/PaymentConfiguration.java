package com.demo.whereby.configurations;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfiguration {

    @Bean
    public RazorpayClient getRazorpayClient(@Value("${paymentKeyId}") String id, @Value("${paymentKeySecret}") String secret) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(id, secret);
        return razorpayClient;
    }

}
