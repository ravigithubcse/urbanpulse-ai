package com.urbanpulse.infrastructure.repository;

import com.urbanpulse.infrastructure.model.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetTypeRepository extends JpaRepository<AssetType, UUID> {

    Optional<AssetType> findByName(String name);
}
