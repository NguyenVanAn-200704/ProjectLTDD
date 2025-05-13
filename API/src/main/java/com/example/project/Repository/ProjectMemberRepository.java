package com.example.project.Repository;

import com.example.project.Entity.Project;
import com.example.project.Entity.ProjectMember;
import com.example.project.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Integer> {
  Optional<ProjectMember> findByProjectAndUser(Project project, User user);

  Optional<ProjectMember> findById(Integer id);

  void deleteById(Integer id);

  boolean existsByProjectIdAndUserId(Integer projectId, Integer userId);

  List<ProjectMember> findByProject(Project project);
}
