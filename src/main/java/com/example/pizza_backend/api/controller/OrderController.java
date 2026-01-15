package com.example.pizza_backend.api.controller;


import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.request.order.OrderAndItemRequest;
import com.example.pizza_backend.api.dto.request.order.OrderItemsRequest;
import com.example.pizza_backend.api.dto.request.order.OrderRequest;
import com.example.pizza_backend.api.dto.request.order.OrderSearchRequest;
import com.example.pizza_backend.api.dto.response.OrderItemResponse;
import com.example.pizza_backend.api.dto.response.OrderResponse;
import com.example.pizza_backend.service.CartItemService;
import com.example.pizza_backend.service.OrderItemService;
import com.example.pizza_backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final CartItemService cartItemService;

    @Autowired
    public OrderController(OrderService orderService, OrderItemService orderItemService, CartItemService cartItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.cartItemService = cartItemService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getOrders(HttpServletRequest request) {
        // ดึง profile_id ที่ถูก set มาจาก Interceptor
        Long profileId = (Long) request.getAttribute("profile_id");

        List<OrderResponse> orders = orderService.getOrdersByProfileId(profileId);

        List<Map<String, Object>> results = new ArrayList<>();
        for (OrderResponse order : orders) {
            Map<String, Object> data = Map.of(
                    "order", order,
                    "orderItems", orderItemService.getOrderItems(order.getOrderId())
            );
            results.add(data);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("results", results);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllOrders(OrderSearchRequest req) {

        List<OrderResponse> orders = orderService.getAllOrders(req);

        List<Map<String, Object>> results = new ArrayList<>();
        for (OrderResponse order : orders) {
            Map<String, Object> data = Map.of(
                    "order", order,
                    "orderItems", orderItemService.getOrderItems(order.getOrderId())
            );
            results.add(data);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("results", results);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(HttpServletRequest request,
                                         @RequestBody OrderAndItemRequest orderAndItemRequest) {
        Long profileId = (Long) request.getAttribute("profile_id");
        Number roleNum = (Number) request.getAttribute("profile_role");
        Integer role = roleNum != null ? roleNum.intValue() : null;
        if (role==2) {
            return  ResponseEntity.badRequest()
                    .body(Map.of("message", "you are admin"));
        }
        String createLog = orderService.createOrderAndOrderItems(orderAndItemRequest, profileId);
        cartItemService.clearAllCartItem(profileId);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "create order and clear cart success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrder(HttpServletRequest request,
                                         @RequestBody OrderRequest orderRequest) {
        String username = (String) request.getAttribute("username");
        String createLog = orderService.updateOrder(orderRequest, username);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "Update order success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/reorder")
    public ResponseEntity<?> reOrder(HttpServletRequest request,
                                     @RequestBody OrderItemsRequest OrderItemsRequest) {

        Long profileId = (Long) request.getAttribute("profile_id");
        Number roleNum = (Number) request.getAttribute("profile_role");
        Integer role = roleNum != null ? roleNum.intValue() : null;
        if (role==2) {
            return  ResponseEntity.badRequest()
                    .body(Map.of("message", "you are admin"));
        }
        cartItemService.clearAllCartItem(profileId);
        String createLog = "not success";
        for (OrderItemResponse item : OrderItemsRequest.getOrderItems()) {

            CartItemRequest Request = new CartItemRequest();

            // แปลง orderItem เป็น cartItem
            Request.setProductId(item.getProductIdSnapshot());
            Request.setQty(item.getQty());
            Request.setLineTotal(item.getLineTotal());
            Request.setProductName(item.getProductName());
            Request.setProductDetail(item.getProductDetail());
            Request.setProductPrice(item.getProductPrice());

            cartItemService.createCartItem(Request, profileId);
        }


        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "reorder success"));
        }
        return ResponseEntity.badRequest().build();
    }
}
