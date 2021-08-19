package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.DataFiles;

public interface FileRepository extends JpaRepository<DataFiles, Long> {
}
