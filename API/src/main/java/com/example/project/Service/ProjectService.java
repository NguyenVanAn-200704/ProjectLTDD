package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.User;
import com.example.project.Mapper.ProjectMapper;
import com.example.project.Repository.ProjectRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.ProjectRequest;
import com.example.project.Request.UpdateProjectRequest;
import com.example.project.Request.UserRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ResponseEntity<Map<String, Object>> createProject(ProjectRequest projectRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Project project = projectMapper.projectRequestToProject(projectRequest);
            User creator = userRepository.findById(projectRequest.getCreateById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            project.setCreateBy(creator);
            projectRepository.save(project);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Tạo project thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> updateProject(UpdateProjectRequest updateProjectRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Project> optionalProject = projectRepository.findById(updateProjectRequest.getId());

            Project project = optionalProject.orElseThrow(() ->
                    new RuntimeException("Project not found with id: " + updateProjectRequest.getId()));

            project.setName(updateProjectRequest.getName());
            project.setDescription(updateProjectRequest.getDescription());
            project.setStatus(updateProjectRequest.getStatus());
            projectRepository.save(project);
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Tạo project thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
