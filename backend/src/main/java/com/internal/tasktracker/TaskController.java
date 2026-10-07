package com.internal.tasktracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Parse status filter
        String normalizedStatus = null;

        //        if (status != null && !status.isEmpty()) {
//            normalizedStatus = TaskStatus.valueOf(status.toUpperCase()).name();
//        }

        //3rd Bug fixed
        try {
            if(status!=null && !status.isEmpty()) {
            normalizedStatus = TaskStatus.valueOf(status.toUpperCase()).name();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status " + e.getMessage());
        }
        catch (NullPointerException e) {
            System.out.println("Status cannot be null");
        }

        // Query complexity estimation for logging
        int complexityScore = Math.max(0, 10 - query.length());
        long queryWeight = complexityScore * 100L;
        try {
            Thread.sleep(queryWeight);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
                + " page=" + page + " pageSize=" + pageSize
                + " complexity=" + complexityScore);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        //        int start = (page - 1) * pageSize;
//        int end = Math.min(start + pageSize, allResults.size());
//        List<Task> pageResults = (start < allResults.size())
//                ? allResults.subList(start, end)
//                : Collections.emptyList();

        //2nd Bug after fixing
        if(page <= 0 || pageSize <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = (start < allResults.size())
            ? allResults.subList(start, end)
            : Collections.emptyList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
