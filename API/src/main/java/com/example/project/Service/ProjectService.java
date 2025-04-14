package com.example.project.Service;

import com.example.project.Entity.Project;
import com.example.project.Entity.User;
import com.example.project.Mapper.ProjectMapper;
import com.example.project.Mapper.UpdateProjectMapper;
import com.example.project.Repository.ProjectRepository;
import com.example.project.Repository.UserRepository;
import com.example.project.Request.ProjectRequest;
import com.example.project.Request.UpdateProjectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final UpdateProjectMapper updateProjectMapper;

    @Transactional
    public ResponseEntity<Map<String, Object>> createProject(ProjectRequest projectRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Project project = projectMapper.projectRequestToProject(projectRequest);
            User user = userRepository.findById(projectRequest.getCreateById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            project.setCreateBy(user);
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
            Project project = projectRepository.findById(updateProjectRequest.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Project not found with id: " + updateProjectRequest.getId()));
            updateProjectMapper.updateProjectFromRequest(updateProjectRequest, project);
            projectRepository.save(project);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Cập nhật project thành công");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> deleteProject(Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            projectRepository.deleteById(id);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Xóa project thành công");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
