package com.example.project.Repository;

import com.example.project.Entity.Project;
import com.example.project.Entity.Task;
import com.example.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
  boolean existsByProjectAndTitle(Project project, String title);

  void deleteById(Integer id);

  List<Task> findByProject(Project project);

  List<Task> findByProjectAndUser(Project project, User user);

  List<Task> findByUser(User user);

  Optional<Task> findById(Integer id);
  void deleteAllByProjectId(Integer projectId);
}
