package com.urbanpulse.infrastructure.repository;

import com.urbanpulse.infrastructure.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Asset entity with geospatial and filtering capabilities.
 */
@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {

    Optional<Asset> findByAssetCode(String assetCode);

    Page<Asset> findByCategory(String category, Pageable pageable);

    Page<Asset> findByStatus(Asset.AssetStatus status, Pageable pageable);

    Page<Asset> findByCity(String city, Pageable pageable);

    @Query("SELECT a FROM Asset a WHERE a.healthScore <= :maxScore ORDER BY a.healthScore ASC")
    Page<Asset> findByHealthScoreLessThanEqual(@Param("maxScore") int maxScore, Pageable pageable);

    @Query(value = "SELECT * FROM assets WHERE ST_DWithin(location::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radiusMeters)", nativeQuery = true)
    List<Asset> findNearby(@Param("lat") double lat, @Param("lon") double lon, @Param("radiusMeters") double radiusMeters);

    @Query("SELECT a.category, COUNT(a) FROM Asset a GROUP BY a.category")
    List<Object[]> countByCategory();

    @Query("SELECT a.status, COUNT(a) FROM Asset a GROUP BY a.status")
    List<Object[]> countByStatus();

    long countByStatus(Asset.AssetStatus status);

    @Query("SELECT AVG(a.healthScore) FROM Asset a WHERE a.status = 'ACTIVE'")
    Double getAverageHealthScore();
}
