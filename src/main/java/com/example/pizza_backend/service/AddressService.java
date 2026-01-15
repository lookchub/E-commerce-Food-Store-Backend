package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.AddressRequest;
import com.example.pizza_backend.api.dto.response.AddressResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.AddressMapper;
import com.example.pizza_backend.persistence.entity.Address;
import com.example.pizza_backend.persistence.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService{

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    public AddressResponse getAddressByProfileId(Long profileId) {
        if (profileId == null) {
            throw new IllegalArgumentException("The given profile Id cannot be null");
        }
        Address address = addressRepository.findAddressByProfileProfileId(profileId)
                .orElseThrow(() -> new IdNotFoundException("Address Not Found For This Profile"));
        return addressMapper.toAddressResponse(address);
    }

    public Address getAddressByAddressId(Long addressId) {
        if (addressId == null) {
            throw new IllegalArgumentException("The given address Id cannot be null");
        }
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new IdNotFoundException("Address Not Found"));
    }

    @Transactional
    public AddressResponse updateAddress(AddressRequest addressRequest) {
        if (addressRequest.getAddressId() == null) {
            throw new IllegalArgumentException("The given address Id cannot be null");
        }
        Address address = addressRepository.findById(addressRequest.getAddressId())
                .orElseThrow(()-> new IdNotFoundException("Address Not Found"));
        addressMapper.updateAddressFromInput(addressRequest, address);
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toAddressResponse(updatedAddress);
    }
}
