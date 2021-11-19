package com.booking.web.controller;

import com.booking.model.entity.User;
import com.booking.model.repository.UserRepository;
import com.booking.web.service.UserXLSXExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminRestController
{
    private final UserRepository userRepository;
    private final UserXLSXExportService userExportService;

    @Autowired
    public AdminRestController(UserRepository userRepository, UserXLSXExportService userExportService) {
        this.userRepository = userRepository;
        this.userExportService = userExportService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
    }

    @GetMapping("/export")
    public void exportToXSLX(HttpServletResponse response) {
        userExportService.export(response);
    }
}
