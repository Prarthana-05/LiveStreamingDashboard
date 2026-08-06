package com.prarthana.livestreamingdashboard.repository;

import com.prarthana.livestreamingdashboard.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StreamRepository extends JpaRepository<Stream, Long> {


    List<Stream> findByStreamNameContainingIgnoreCase(String name);


    long countByStatus(String status);


    List<Stream> findByStatus(String status);

}