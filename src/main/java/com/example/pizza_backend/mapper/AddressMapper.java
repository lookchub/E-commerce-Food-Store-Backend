package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.request.AddressRequest;
import com.example.pizza_backend.api.dto.request.ProfileRequest;
import com.example.pizza_backend.api.dto.response.AddressResponse;
import com.example.pizza_backend.persistence.entity.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    //CREATE
    @Mapping(target = "addressId", ignore = true)
    Address toAddress(ProfileRequest req);

    //UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "addressId", ignore = true)
    void updateAddressFromInput(AddressRequest addressInput, @MappingTarget Address address);

    //READ
    AddressResponse toAddressResponse(Address address);
}
