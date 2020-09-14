package com.demo.whereby.controller;

import com.demo.whereby.entity.Orders;
import com.demo.whereby.entity.OrdersDtls;
import com.demo.whereby.entity.User;
import com.demo.whereby.model.PaymentModel;
import com.demo.whereby.service.interfaces.OrderDtlService;
import com.demo.whereby.service.interfaces.OrderService;
import com.demo.whereby.service.interfaces.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PaymentController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDtlService orderDtlService;
    @Autowired
    private RazorpayClient razorpayClient;

    @PostMapping("/orders")
    public String createNewOrder(@Value("paymentKeyId") String key,
                                 @RequestParam("meetings") int meetings,
                                 @RequestParam("phoneNumber") String phoneNumber,
                                 Model model) throws RazorpayException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        JSONObject options = new JSONObject();
        options.put("amount", meetings * 2 * 100);
        options.put("currency", "INR");
        options.put("receipt", "txn " + " " + user + " " + meetings * 2 * 100);
        Order order = razorpayClient.Orders.create(options);
        System.out.println(order);

        model.addAttribute("receipt", "txn " + " " + user + " " + meetings * 2 * 100);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("user", user);
        model.addAttribute("key", key);
        model.addAttribute("numberOfMeeting", meetings);
        model.addAttribute("amount", meetings * 2 * 100);
        model.addAttribute("order", order.get("id"));
        return "payment";
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName != "anonymousUser") {
            User user = userService.findByEmail(userName);
            if (user != null) {
                model.addAttribute("orderList", user.getOrders().stream().map(orders -> {
                    Map<String, Object> order = new HashMap<>();
                    order.put("reciept", (Object) orders.getReceipt());
                    order.put("amount", (Object) orders.getAmount());
                    order.put("orderId", (Object) orders.getOrderId());
                    return order;
                }).collect(Collectors.toList()));
            }
        }
        return "orders";
    }

    @RequestMapping("/processPayment")
    public String processPayment(@ModelAttribute PaymentModel paymentModel,
                                 @RequestParam("razorpay_payment_id") String razorpayPaymentId,
                                 @RequestParam("razorpay_order_id") String razorpayOrderId,
                                 @RequestParam("razorpay_signature") String razorpaySignature,
                                 Principal principal) {
        User user = userService.findByEmail(principal.getName());
        if (user != null) {
            Orders order = new Orders();
            order.setAmount(Integer.parseInt(paymentModel.getAmount()));
            order.setReceipt(paymentModel.getReceipt());
            order.setOrderId(paymentModel.getOrderId());
            OrdersDtls ordersDtls = new OrdersDtls();
            ordersDtls.setOrders(order);
            ordersDtls.setRazorpay_order_id(razorpayOrderId);
            ordersDtls.setRazorpay_payment_id(razorpayPaymentId);
            ordersDtls.setRazorpay_signature(razorpaySignature);
            order.setOrdersDtls(ordersDtls);
            user.setOrders(order);
            user.setMeetingsLeft(user.getMeetingsLeft() + Integer.parseInt(paymentModel.numberOfMeeting));
            userService.save(user);
            orderService.save(order);
            orderDtlService.save(ordersDtls);
        }

        return "redirect:/user-dashboard?paymentSuccess";
    }

}















