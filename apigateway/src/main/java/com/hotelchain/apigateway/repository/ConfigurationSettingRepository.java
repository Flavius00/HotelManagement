package com.hotelchain.apigateway.repository;

import com.hotelchain.apigateway.entity.ConfigurationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConfigurationSettingRepository extends JpaRepository<ConfigurationSetting, Long> {
    Optional<ConfigurationSetting> findBySettingKey(String settingKey);
    boolean existsBySettingKey(String settingKey);
}