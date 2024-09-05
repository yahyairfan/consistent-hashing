package com.example.inmemory.controller;

import com.example.inmemory.service.DBService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class DBController {
    private final DBService dbService;

    public DBController(DBService dbService) {
        this.dbService = dbService;
    }

    @PostMapping("/db")
    public ResponseEntity<Void> createDb(@NotNull @RequestParam("dbname") String dbName) {
        dbService.addDb(dbName);
        return ResponseEntity.created(URI.create("/db")).build();
    }

    @DeleteMapping("/db")
    public ResponseEntity<Void> deleteDb(@NotNull @RequestParam("dbname") String dbName) {
        dbService.removeDb(dbName);
        return ResponseEntity.noContent().build();
    }
}
