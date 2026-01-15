package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.LoginRequest;
import com.example.pizza_backend.api.dto.request.ProfileRequest;
import com.example.pizza_backend.api.dto.response.ProfileResponse;
import com.example.pizza_backend.auth.JwtService;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.AddressMapper;
import com.example.pizza_backend.mapper.ProfileMapper;
import com.example.pizza_backend.persistence.entity.Address;
import com.example.pizza_backend.persistence.entity.Cart;
import com.example.pizza_backend.persistence.entity.Profile;
import com.example.pizza_backend.persistence.repository.AddressRepository;
import com.example.pizza_backend.persistence.repository.CartRepository;
import com.example.pizza_backend.persistence.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    private final JwtService jwtService;
    private final ProfileMapper profileMapper;
    private final AddressMapper addressMapper;
    private final ProfileRepository profileRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;

    @Autowired
    public ProfileService(
            ProfileRepository profileRepository,
            JwtService jwtService,
            ProfileMapper profileMapper,
            AddressRepository addressRepository,
            CartRepository cartRepository,
            AddressMapper addressMapper) {
        this.profileRepository = profileRepository;
        this.jwtService = jwtService;
        this.profileMapper = profileMapper;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.addressMapper = addressMapper;
    }

    public Profile getProfileByProfileId(Long profileId) {
        if (profileId == null) {
            throw new IllegalArgumentException("The given profile Id cannot be null");
        }
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new IdNotFoundException("Profile Not Found"));
    }

    public Optional<Profile> checkLogIn(LoginRequest req) {
        if (req.getUsername() == null || req.getPassword() == null){
            throw new IllegalArgumentException("The given username or password cannot be null");
        }
        Optional<Profile> userOpt = profileRepository
                .findFirstByUsernameAndPassword(req.getUsername(), req.getPassword());
        return userOpt;
    }


    public Boolean checkDuplicateProfile(ProfileRequest req) {
        if (req.getUsername() == null){
            throw new IllegalArgumentException("The given username cannot be null");
        }
        if (profileRepository.existsByUsername(req.getUsername())) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public String createProfileWithAddress(ProfileRequest req, Integer role) {
        Profile user = profileMapper.toProfile(req, role);
        Address address = addressMapper.toAddress(req);

        //role=2=admin admin ไม่มี cart และ address
        if (role == 1) {
            user.setAddress(address);
            address.setProfile(user);
            Cart cart = new Cart();
            cart.setProfile(user);
            cart.setCreatedAt(LocalDate.now());
            cartRepository.save(cart);
            addressRepository.save(address);
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setProfileRole(role);
        profileRepository.save(user);

        //สร้าง JWT แล้วส่ง token กลับไป
        String token = jwtService.generateToken(Map.of(
                "profile_id", user.getProfileId(),
                "username", user.getUsername(),
                "profile_role", user.getProfileRole()
        ));
        return token;
    }


    public String updateProfile(ProfileRequest req, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IdNotFoundException("Profile Not Found"));
        profileMapper.updateProfileFromInput(req, profile);
        profileRepository.save(profile);
        return "success";
    }


    public ProfileResponse getProfileById(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IdNotFoundException("Profile Not Found"));
        return profileMapper.toProfileResponse(profile);
    }
}
