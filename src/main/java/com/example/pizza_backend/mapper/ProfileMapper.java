package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.request.ProfileRequest;
import com.example.pizza_backend.api.dto.response.ProfileResponse;
import com.example.pizza_backend.persistence.entity.Profile;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    // CREATE
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "profileRole", expression = "java(role)")
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "profileId", ignore = true)
    Profile toProfile(ProfileRequest req, @Context Integer role);

    //UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profileId", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateProfileFromInput(ProfileRequest profileInput, @MappingTarget Profile profile);

    //READ
    ProfileResponse toProfileResponse(Profile profile);
}
