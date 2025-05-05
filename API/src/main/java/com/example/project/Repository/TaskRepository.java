package com.example.project.Repository;

import com.example.project.Entity.Project;
import com.example.project.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
  boolean existsByProjectAndTitle(Project project, String title);

  void deleteById(Integer id);
}
