package com.example.project.Repository;

import com.example.project.Entity.Project;
import com.example.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
  Optional<Project> findById(Integer id);

  void deleteById(Integer id);

  boolean existsByNameAndCreateBy(String name, User createBy);

  List<Project> findByCreateBy(User createBy);
}
