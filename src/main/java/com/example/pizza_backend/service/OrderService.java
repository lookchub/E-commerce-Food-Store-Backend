package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.request.order.OrderAndItemRequest;
import com.example.pizza_backend.api.dto.request.order.OrderRequest;
import com.example.pizza_backend.api.dto.request.order.OrderSearchRequest;
import com.example.pizza_backend.api.dto.response.OrderResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.OrderItemMapper;
import com.example.pizza_backend.mapper.OrdersMapper;
import com.example.pizza_backend.persistence.entity.*;
import com.example.pizza_backend.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private ProfileService profileService;
    private AddressService addressService;
    private OrderItemRepository orderItemRepository;
    private ProductRepository productRepository;
    private DiscountCodeRepository discountCodeRepository;
    private InventoryTransactionRepository invtRepository;
    private OrdersMapper ordersMapper;
    private OrderItemMapper orderItemMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrdersMapper ordersMapper,
                        ProfileService profileService,
                        AddressService addressService,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        DiscountCodeRepository discountCodeRepository,
                        InventoryTransactionRepository invtRepository,
                        OrderItemMapper  orderItemMapper) {
        this.orderRepository = orderRepository;
        this.ordersMapper = ordersMapper;
        this.profileService = profileService;
        this.addressService = addressService;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.discountCodeRepository = discountCodeRepository;
        this.invtRepository = invtRepository;
        this.orderItemMapper = orderItemMapper;
    }
    public List<OrderResponse> getOrdersByProfileId(Long profileId) {
        List<Orders> orders = orderRepository.getOrdersByProfileProfileId(profileId);
        return orders.stream()
                .map(order -> ordersMapper.toOrderResponse(order))
                .toList();
    }


    public List<OrderResponse> getAllOrders(OrderSearchRequest req) {
        List<Orders> orders = orderRepository.searchOrder(req.getOrderId(), req.getStatus());
        return orders.stream()
                .map(order -> ordersMapper.toOrderResponse(order))
                .toList();
    }

    @Transactional
    public String createOrderAndOrderItems(OrderAndItemRequest orderAndItemRequest, Long profileId) {
        OrderRequest orderInput = orderAndItemRequest.getOrderInput();
        List<CartItemRequest> cartItemInputs = orderAndItemRequest.getCartItemInputs();
        Profile profile =  profileService.getProfileByProfileId(profileId);
        Long addressId = profile.getAddress().getAddressId();
        Address address = addressService.getAddressByAddressId(addressId);


        if (orderAndItemRequest.getDiscountCode() != null) {
            Long discountId = orderAndItemRequest.getDiscountCode().getDiscountId();
            DiscountCode code = discountCodeRepository.findById(discountId)
                    .orElseThrow(() -> new IdNotFoundException("Code not found"));
            Integer currentQty = code.getQty();
            Integer codeQtyToDeduct = 1;
            if (currentQty == null || currentQty < codeQtyToDeduct) {
                throw new IllegalArgumentException("Code is out");
            }
            code.setQty(currentQty - codeQtyToDeduct);
            discountCodeRepository.save(code);
        }

        Orders order = ordersMapper.toOrder(orderInput);
        order.setAddress(address);
        order.setProfile(profile);
        orderRepository.save(order);

        for (CartItemRequest cartItemInput : cartItemInputs) {
            OrderItem orderItem = orderItemMapper.toOrderItem(cartItemInput);
            orderItem.setOrder(order);
            orderItemRepository.save(orderItem);

            //reduce stock
            Product product = productRepository.findById(cartItemInput.getProductId())
                    .orElseThrow(() -> new IdNotFoundException("Product not found"));

            Integer currentStock = product.getProductStock(); // สมมติว่ามี field ชื่อ stock ใน Product
            Integer qtyToDeduct = cartItemInput.getQty();

            if (currentStock == null || currentStock < qtyToDeduct) {
                throw new IllegalArgumentException("Not enough stock");
            }

            product.setProductStock(currentStock - qtyToDeduct);
            productRepository.save(product);

            InventoryTransaction ivt = new InventoryTransaction();
            ivt.setProduct(product);
            ivt.setCreatedAt(LocalDateTime.now());
            ivt.setTransactionType(2);
            ivt.setQtyChange(qtyToDeduct);
            invtRepository.save(ivt);
        }
        return "success";
    }

    @Transactional
    public String updateOrder(OrderRequest orderRequest, String name) {
        if (orderRequest.getOrderId() == null) {
            throw new IllegalArgumentException("The given Order ID cannot be null");
        }
        Orders order = orderRepository.findById(orderRequest.getOrderId())
                .orElseThrow(()-> new IdNotFoundException("Order not found"));
        ordersMapper.updateOrderFromInput(orderRequest, order, name);
        orderRepository.save(order);
        return "success";
    }
}
