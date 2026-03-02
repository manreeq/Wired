package com.group1.wired.repositories;

import com.group1.wired.entities.ListeningActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningActivityRepository extends JpaRepository<ListeningActivity,Long> {
}
