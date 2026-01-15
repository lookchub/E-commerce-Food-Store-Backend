package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.request.discountCode.DiscountCodeSearchRequest;
import com.example.pizza_backend.api.dto.response.DiscountCodeResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.DiscountCodeMapper;
import com.example.pizza_backend.persistence.entity.DiscountCode;
import com.example.pizza_backend.persistence.repository.DiscountCodeRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountCodeServiceTest {

    @Mock
    private DiscountCodeRepository discountCodeRepository;

    @Mock
    private DiscountCodeMapper discountCodeMapper;

    @InjectMocks
    private DiscountCodeService discountCodeService;

    private DiscountCode discountCode;
    private DiscountCodeResponse discountCodeResponse;
    private DiscountCodeSearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        discountCode = new DiscountCode();
        discountCode.setDiscountId(1L);
        discountCode.setCode("SAVE20");

        discountCodeResponse = DiscountCodeResponse.builder()
                .discountId(1L)
                .code("SAVE20")
                .build();

        searchRequest = new DiscountCodeSearchRequest();
        searchRequest.setCode("SAVE20");
    }

    @Nested
    @DisplayName("getDiscountCode")
    class GetDiscountCodeTests {

        @Test
        @DisplayName("should return discount code when code exists")
        void shouldReturnDiscountCode_WhenCodeExists() {
            // SET
            when(discountCodeRepository.findByCode("SAVE20"))
                    .thenReturn(Optional.of(discountCode));
            when(discountCodeMapper.toDiscountCodeDto(discountCode))
                    .thenReturn(discountCodeResponse);

            // TEST
            DiscountCodeResponse result = discountCodeService.getDiscountCode(searchRequest);

            // VERIFY
            assertThat(result).isEqualTo(discountCodeResponse);
            verify(discountCodeRepository).findByCode("SAVE20");
            verify(discountCodeMapper).toDiscountCodeDto(discountCode);
        }

        @Test
        @DisplayName("should throw exception when code is null")
        void shouldThrowException_WhenCodeIsNull() {
            // SET
            searchRequest.setCode(null);

            // TEST & VERIFY
            assertThatThrownBy(() -> discountCodeService.getDiscountCode(searchRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given code cannot be null");

            verifyNoInteractions(discountCodeRepository, discountCodeMapper);
        }

        @Test
        @DisplayName("should throw exception when code not found")
        void shouldThrowException_WhenCodeNotFound() {
            // SET
            when(discountCodeRepository.findByCode("SAVE20"))
                    .thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> discountCodeService.getDiscountCode(searchRequest))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Code not found");

            verifyNoInteractions(discountCodeMapper);
        }
    }
}