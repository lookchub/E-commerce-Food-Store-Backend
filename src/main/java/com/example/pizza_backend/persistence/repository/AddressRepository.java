package com.example.pizza_backend.persistence.repository;

import com.example.pizza_backend.persistence.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    Optional<Address> findAddressByProfileProfileId(Long profileId);
}
