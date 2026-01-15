package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.request.AddressRequest;
import com.example.pizza_backend.api.dto.response.AddressResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.AddressMapper;
import com.example.pizza_backend.persistence.entity.Address;
import com.example.pizza_backend.persistence.repository.AddressRepository;
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
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Address address;
    private AddressResponse addressResponse;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setAddressId(1L);

        addressResponse = AddressResponse.builder()
                .addressId(1L)
                .build();

        addressRequest = new AddressRequest();
        addressRequest.setAddressId(1L);
    }

    // split tests by each method
    @Nested
    @DisplayName("getAddressByProfileId")
    class GetAddressByProfileIdTests {

        @Test
        @DisplayName("should return address when profile exists")
        void shouldReturnAddress_WhenProfileExists() {
            //SET
            Long profileId = 1L;
            when(addressRepository.findAddressByProfileProfileId(profileId))
                    .thenReturn(Optional.of(address));
            when(addressMapper.toAddressResponse(address))
                    .thenReturn(addressResponse);

            //TEST
            AddressResponse result = addressService.getAddressByProfileId(profileId);

            //VERIFY
            assertThat(result).isNotNull();
            assertThat(result.getAddressId()).isEqualTo(1L);

            verify(addressRepository).findAddressByProfileProfileId(profileId);
            verify(addressMapper).toAddressResponse(address);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when profileId is null")
        void shouldThrowException_WhenProfileIdIsNull() {
            //TEST & VERIFY
            assertThatThrownBy(() -> addressService.getAddressByProfileId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given profile Id cannot be null");

            verify(addressMapper, never()).toAddressResponse(any());
        }

        @Test
        @DisplayName("should throw IdNotFoundException when address not found")
        void shouldThrowException_WhenAddressNotFound() {
            //SET
            Long profileId = 999L;
            when(addressRepository.findAddressByProfileProfileId(profileId))
                    .thenReturn(Optional.empty());

            //TEST & VERIFY
            assertThatThrownBy(() -> addressService.getAddressByProfileId(profileId))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Address Not Found For This Profile");

            verify(addressMapper, never()).toAddressResponse(any());
        }
    }

    @Nested
    @DisplayName("updateAddress")
    class UpdateAddressTests {
        @Test
        @DisplayName("should update and return success")
        void shouldUpdateAddress_WhenAddressExists() {
            //SET
            when(addressRepository.findById(1L))
                    .thenReturn(Optional.of(address));
            when(addressRepository.save(address))
                    .thenReturn(address);
            when(addressMapper.toAddressResponse(address))
                    .thenReturn(addressResponse);

            // WHEN
            AddressResponse result = addressService.updateAddress(addressRequest);

            // THEN
            verify(addressMapper).updateAddressFromInput(addressRequest, address);
            verify(addressRepository).save(address);
            assertThat(result).isEqualTo(addressResponse);
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when addressId is null")
        void shouldThrowException_WhenAddressIdIsNull() {
            // SET
            AddressRequest nullIdRequest = new AddressRequest();
            nullIdRequest.setAddressId(null);

            // TEST & VERIFY
            assertThatThrownBy(() -> addressService.updateAddress(nullIdRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given address Id cannot be null");

            verifyNoInteractions(addressRepository);
        }

        @Test
        @DisplayName("should throw IdNotFoundException when address not found")
        void shouldThrowException_WhenAddressNotFound() {
            // SET
            addressRequest.setAddressId(999L);
            when(addressRepository.findById(999L))
                    .thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> addressService.updateAddress(addressRequest))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Address Not Found");
        }
    }
}
