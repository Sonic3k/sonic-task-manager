package com.sonic.taskmanager.repository;

import com.sonic.taskmanager.model.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferencesRepository extends JpaRepository<Preferences, String> {

    // Find preference by key
    Optional<Preferences> findByKey(String key);

    // Check if preference exists
    boolean existsByKey(String key);
}