package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.response.CartResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.CartMapper;
import com.example.pizza_backend.persistence.entity.Cart;
import com.example.pizza_backend.persistence.entity.Profile;
import com.example.pizza_backend.persistence.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartResponse cartResponse;
    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile();
        profile.setProfileId(1L);

        cart = new Cart();
        cart.setCartId(1L);
        cart.setProfile(profile);

        cartResponse = CartResponse.builder()
                .cartId(1L)
                .build();
    }

    @Nested
    @DisplayName("getCartDtoByProfileId")
    class GetCartDtoByProfileIdTests {

        @Test
        @DisplayName("should return cart response when profile exists")
        void shouldReturnCartResponse_WhenProfileExists() {
            // SET
            Long profileId = 1L;
            when(cartRepository.findByProfileProfileId(profileId))
                    .thenReturn(Optional.of(cart));
            when(cartMapper.toCartResponse(cart))
                    .thenReturn(cartResponse);

            // TEST
            CartResponse result = cartService.getCartDtoByProfileId(profileId);

            // VERIFY
            assertThat(result).isEqualTo(cartResponse);
            verify(cartRepository).findByProfileProfileId(profileId);
            verify(cartMapper).toCartResponse(cart);
        }

        @Test
        @DisplayName("should throw exception when profileId is null")
        void shouldThrowException_WhenProfileIdIsNull() {
            // TEST & VERIFY
            assertThatThrownBy(() -> cartService.getCartDtoByProfileId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("profileId cannot be null");

            verifyNoInteractions(cartRepository, cartMapper);
        }

        @Test
        @DisplayName("should throw exception when cart not found")
        void shouldThrowException_WhenCartNotFound() {
            // SET
            Long profileId = 1L;
            when(cartRepository.findByProfileProfileId(profileId))
                    .thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> cartService.getCartDtoByProfileId(profileId))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Cart not found for this user");

            verifyNoInteractions(cartMapper);
        }
    }

    @Nested
    @DisplayName("getCartByProfileId")
    class GetCartByProfileIdTests {

        @Test
        @DisplayName("should return cart when profile exists")
        void shouldReturnCart_WhenProfileExists() {
            // SET
            Long profileId = 1L;
            when(cartRepository.findByProfileProfileId(profileId))
                    .thenReturn(Optional.of(cart));

            // TEST
            Cart result = cartService.getCartByProfileId(profileId);

            // VERIFY
            assertThat(result).isEqualTo(cart);
            verify(cartRepository).findByProfileProfileId(profileId);
        }

        @Test
        @DisplayName("should throw exception when cart not found")
        void shouldThrowException_WhenCartNotFound() {
            // SET
            Long profileId = 1L;
            when(cartRepository.findByProfileProfileId(profileId))
                    .thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> cartService.getCartByProfileId(profileId))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Cart not found for this user");
        }
    }
}