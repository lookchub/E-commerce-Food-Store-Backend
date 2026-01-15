package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.response.CartItemResponse;
import com.example.pizza_backend.exception.AccessDeniedException;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.CartItemMapper;
import com.example.pizza_backend.persistence.entity.Cart;
import com.example.pizza_backend.persistence.entity.CartItem;
import com.example.pizza_backend.persistence.entity.Product;
import com.example.pizza_backend.persistence.entity.Profile;
import com.example.pizza_backend.persistence.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @InjectMocks
    CartItemService cartItemService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Nested
    @DisplayName("checkConstrain")
    class CheckConstrainTests {

        @Test
        @DisplayName("should return true when qty and lineTotal are positive")
        void shouldReturnTrue_WhenQtyAndLineTotalArePositive() {
            // TEST
            boolean result = cartItemService.checkConstrain(5, 100);

            // VERIFY
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return true when qty and lineTotal are zero")
        void shouldReturnTrue_WhenQtyAndLineTotalAreZero() {
            // TEST
            boolean result = cartItemService.checkConstrain(0, 0);

            // VERIFY
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false when qty is negative")
        void shouldReturnFalse_WhenQtyIsNegative() {
            // TEST
            boolean result = cartItemService.checkConstrain(-1, 100);

            // VERIFY
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("should return false when lineTotal is negative")
        void shouldReturnFalse_WhenLineTotalIsNegative() {
            // TEST
            boolean result = cartItemService.checkConstrain(5, -1);

            // VERIFY
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("should return false when both are negative")
        void shouldReturnFalse_WhenBothAreNegative() {
            // TEST
            boolean result = cartItemService.checkConstrain(-1, -1);

            // VERIFY
            assertThat(result).isFalse();
        }
    }
    @Nested
    @DisplayName("getCartItemsByCartId")
    class getCartItemsByCartIdTests {
        private CartItem cartItem1;
        private CartItem cartItem2;
        private CartItemResponse cartItemResponse1;
        private CartItemResponse cartItemResponse2;

        @BeforeEach
        void setUp() {
            cartItem1 = new CartItem();
            cartItem1.setCartItemId(1L);

            cartItem2 = new CartItem();
            cartItem2.setCartItemId(2L);

            cartItemResponse1 = CartItemResponse.builder()
                    .cartItemId(1L)
                    .build();

            cartItemResponse2 = CartItemResponse.builder()
                    .cartItemId(2L)
                    .build();

        }

        @Test
        @DisplayName("should return List<CartItemResponse> when cartId exists")
        void shouldReturnCartItemResponse_WhenCartIdExists() {
            // SET
            Long cartId = 1L;
            when(cartItemRepository.findByCartCartId(cartId))
                    .thenReturn(List.of(cartItem1, cartItem2));
            when(cartItemMapper.toCartItemResponse(cartItem1))
                    .thenReturn(cartItemResponse1);
            when(cartItemMapper.toCartItemResponse(cartItem2))
                    .thenReturn(cartItemResponse2);

            // TEST
            List<CartItemResponse> result = cartItemService.getCartItemsByCartId(cartId);

            // VERIFY
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(cartItemResponse1, cartItemResponse2);
            verify(cartItemRepository).findByCartCartId(cartId);
        }

        @Test
        @DisplayName("should return empty list when no cart items found")
        void shouldReturnEmptyList_WhenNoCartItemsFound() {
            // SET
            Long cartId = 1L;
            when(cartItemRepository.findByCartCartId(cartId))
                    .thenReturn(List.of());

            // TEST
            List<CartItemResponse> result = cartItemService.getCartItemsByCartId(cartId);

            // VERIFY
            assertThat(result).isEmpty();
            verify(cartItemRepository).findByCartCartId(cartId);
            verify(cartItemMapper, never()).toCartItemResponse(any());
        }

        @Test
        @DisplayName("should throw exception when cartId is null")
        void shouldThrowException_WhenCartIdIsNull() {
            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.getCartItemsByCartId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("cartId cannot be null");

            verify(cartItemRepository, never()).findByCartCartId(any());
        }
    }

    @Nested
    @DisplayName("createCartItem")
    class CreateCartItemTests {

        private Cart cart;
        private Product product;
        private CartItem cartItem;
        private CartItemRequest cartItemRequest;
        private CartItemResponse cartItemResponse;

        @BeforeEach
        void setUp() {
            cart = new Cart();
            cart.setCartId(1L);

            product = new Product();
            product.setProductId(10L);

            cartItem = new CartItem();
            cartItem.setCartItemId(100L);
            cartItem.setQty(2);
            cartItem.setLineTotal(200);

            cartItemRequest = new CartItemRequest();
            cartItemRequest.setProductId(10L);

            cartItemResponse = CartItemResponse.builder()
                    .cartItemId(100L)
                    .build();
        }

        @Test
        @DisplayName("should create cart item when product not in cart")
        void shouldCreateCartItem_WhenProductNotInCart() {
            // SET
            Long profileId = 1L;
            when(cartService.getCartByProfileId(profileId)).thenReturn(cart);
            when(productService.getProductById(10L)).thenReturn(product);
            when(cartItemRepository.findByCartCartIdAndProductProductId(1L, 10L))
                    .thenReturn(Optional.empty());
            when(cartItemMapper.toCartItem(cartItemRequest)).thenReturn(cartItem);
            when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
            when(cartItemMapper.toCartItemResponse(cartItem)).thenReturn(cartItemResponse);

            // TEST
            CartItemResponse result = cartItemService.createCartItem(cartItemRequest, profileId);

            // VERIFY
            assertThat(result).isEqualTo(cartItemResponse);
            verify(cartItemRepository).save(cartItem);
        }

        @Test
        @DisplayName("should throw exception when productId is null")
        void shouldThrowException_WhenProductIdIsNull() {
            // SET
            Long profileId = 1L;
            cartItemRequest.setProductId(null);
            when(cartService.getCartByProfileId(profileId)).thenReturn(cart);

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.createCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given product Id cannot be null");

            verifyNoInteractions(productService, cartItemMapper);
        }

        @Test
        @DisplayName("should throw exception when product already in cart")
        void shouldThrowException_WhenProductAlreadyInCart() {
            // SET
            Long profileId = 1L;
            when(cartService.getCartByProfileId(profileId)).thenReturn(cart);
            when(productService.getProductById(10L)).thenReturn(product);
            when(cartItemRepository.findByCartCartIdAndProductProductId(1L, 10L))
                    .thenReturn(Optional.of(cartItem));

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.createCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Product already in cart. Use update endpoint instead.");

            verify(cartItemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when qty is negative")
        void shouldThrowException_WhenQtyIsNegative() {
            // SET
            Long profileId = 1L;
            cartItem.setQty(-1);
            when(cartService.getCartByProfileId(profileId)).thenReturn(cart);
            when(productService.getProductById(10L)).thenReturn(product);
            when(cartItemRepository.findByCartCartIdAndProductProductId(1L, 10L))
                    .thenReturn(Optional.empty());
            when(cartItemMapper.toCartItem(cartItemRequest)).thenReturn(cartItem);

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.createCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("qty must be > 0 and lineTotal must be > 0");

            verify(cartItemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when lineTotal is negative")
        void shouldThrowException_WhenLineTotalIsNegative() {
            // SET
            Long profileId = 1L;
            cartItem.setLineTotal(-100);
            when(cartService.getCartByProfileId(profileId)).thenReturn(cart);
            when(productService.getProductById(10L)).thenReturn(product);
            when(cartItemRepository.findByCartCartIdAndProductProductId(1L, 10L))
                    .thenReturn(Optional.empty());
            when(cartItemMapper.toCartItem(cartItemRequest)).thenReturn(cartItem);

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.createCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("qty must be > 0 and lineTotal must be > 0");

            verify(cartItemRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateCartItem")
    class UpdateCartItemTests {

        private CartItem cartItem;
        private CartItemRequest cartItemRequest;
        private CartItemResponse cartItemResponse;
        private Cart cart;
        private Profile profile;

        @BeforeEach
        void setUp() {
            profile = new Profile();
            profile.setProfileId(1L);

            cart = new Cart();
            cart.setCartId(1L);
            cart.setProfile(profile);

            cartItem = new CartItem();
            cartItem.setCartItemId(100L);
            cartItem.setCart(cart);
            cartItem.setQty(2);
            cartItem.setLineTotal(200);

            cartItemRequest = new CartItemRequest();
            cartItemRequest.setCartItemId(100L);

            cartItemResponse = CartItemResponse.builder()
                    .cartItemId(100L)
                    .build();
        }

        @Test
        @DisplayName("should update cart item when valid request")
        void shouldUpdateCartItem_WhenValidRequest() {
            // SET
            Long profileId = 1L;
            when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));
            when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
            when(cartItemMapper.toCartItemResponse(cartItem)).thenReturn(cartItemResponse);

            // TEST
            CartItemResponse result = cartItemService.updateCartItem(cartItemRequest, profileId);

            // VERIFY
            assertThat(result).isEqualTo(cartItemResponse);
            verify(cartItemMapper).updateCartItemFromInput(cartItemRequest, cartItem);
            verify(cartItemRepository).save(cartItem);
        }

        @Test
        @DisplayName("should throw exception when cartItemId is null")
        void shouldThrowException_WhenCartItemIdIsNull() {
            // SET
            Long profileId = 1L;
            cartItemRequest.setCartItemId(null);

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.updateCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given cart item Id cannot be null");

            verifyNoInteractions(cartItemRepository, cartItemMapper);
        }

        @Test
        @DisplayName("should throw exception when cart item not found")
        void shouldThrowException_WhenCartItemNotFound() {
            // SET
            Long profileId = 1L;
            when(cartItemRepository.findById(100L)).thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.updateCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Cart Item Not found");

            verify(cartItemMapper, never()).updateCartItemFromInput(any(), any());
        }

        @Test
        @DisplayName("should throw exception when cart item belongs to different user")
        void shouldThrowException_WhenCartItemBelongsToDifferentUser() {
            // SET
            Long differentProfileId = 999L;
            when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.updateCartItem(cartItemRequest, differentProfileId))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("This cart does not belong to current user");

            verify(cartItemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when qty is negative")
        void shouldThrowException_WhenQtyIsNegative() {
            // SET
            Long profileId = 1L;
            cartItem.setQty(-1);
            when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.updateCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("qty must be > 0 and lineTotal must be > 0");

            verify(cartItemRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when lineTotal is negative")
        void shouldThrowException_WhenLineTotalIsNegative() {
            // SET
            Long profileId = 1L;
            cartItem.setLineTotal(-100);
            when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.updateCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("qty must be > 0 and lineTotal must be > 0");

            verify(cartItemRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteCartItem")
    class DeleteCartItemTests {

        private CartItem cartItem;
        private CartItemRequest cartItemRequest;
        private Cart cart;
        private Profile profile;

        @BeforeEach
        void setUp() {
            profile = new Profile();
            profile.setProfileId(1L);

            cart = new Cart();
            cart.setCartId(1L);
            cart.setProfile(profile);

            cartItem = new CartItem();
            cartItem.setCartItemId(100L);
            cartItem.setCart(cart);

            cartItemRequest = new CartItemRequest();
            cartItemRequest.setCartItemId(100L);
        }

        @Test
        @DisplayName("should delete cart item when valid request")
        void shouldDeleteCartItem_WhenValidRequest() {
            // SET
            Long profileId = 1L;
            when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));

            // TEST
            cartItemService.deleteCartItem(cartItemRequest, profileId);

            // VERIFY
            verify(cartItemRepository).deleteById(100L);
        }

        @Test
        @DisplayName("should throw exception when cartItemId is null")
        void shouldThrowException_WhenCartItemIdIsNull() {
            // SET
            Long profileId = 1L;
            cartItemRequest.setCartItemId(null);

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.deleteCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given cart item Id cannot be null");

            verifyNoInteractions(cartItemRepository);
        }

        @Test
        @DisplayName("should throw exception when cart item not found")
        void shouldThrowException_WhenCartItemNotFound() {
            // SET
            Long profileId = 1L;
            when(cartItemRepository.findById(100L)).thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.deleteCartItem(cartItemRequest, profileId))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Cart Item Not found");

            verify(cartItemRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("should throw exception when cart item belongs to different user")
        void shouldThrowException_WhenCartItemBelongsToDifferentUser() {
            // SET
            Long differentProfileId = 999L;
            when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.deleteCartItem(cartItemRequest, differentProfileId))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("This cart item does not belong to current user");

            verify(cartItemRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("clearAllCartItem")
    class ClearAllCartItemTests {

        private Cart cart;

        @BeforeEach
        void setUp() {
            cart = new Cart();
            cart.setCartId(1L);
        }

        @Test
        @DisplayName("should clear all cart items when cart exists")
        void shouldClearAllCartItems_WhenCartExists() {
            // SET
            Long profileId = 1L;
            when(cartService.getCartByProfileId(profileId)).thenReturn(cart);

            // TEST
            cartItemService.clearAllCartItem(profileId);

            // VERIFY
            verify(cartService).getCartByProfileId(profileId);
            verify(cartItemRepository).deleteByCartCartId(1L);
        }

        @Test
        @DisplayName("should throw exception when cart not found")
        void shouldThrowException_WhenCartNotFound() {
            // SET
            Long profileId = 1L;
            when(cartService.getCartByProfileId(profileId))
                    .thenThrow(new IdNotFoundException("Cart not found for this user"));

            // TEST & VERIFY
            assertThatThrownBy(() -> cartItemService.clearAllCartItem(profileId))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Cart not found for this user");

            verify(cartItemRepository, never()).deleteByCartCartId(any());
        }
    }

}