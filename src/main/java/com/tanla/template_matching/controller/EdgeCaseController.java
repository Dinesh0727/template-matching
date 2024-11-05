package com.tanla.template_matching.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanla.template_matching.startup.EdgeCaseCheckerAPI;

@RestController
@RequestMapping("/api/edge-case")
public class EdgeCaseController {

    @Autowired
    private EdgeCaseCheckerAPI edgeCaseChecker;

    @GetMapping("/case1a")
    public ResponseEntity<String> triggerCase1a(@RequestParam boolean caseSensitive) {
        try {
            edgeCaseChecker.runCase1a(caseSensitive);
            return ResponseEntity.ok("Case 1a executed with caseSensitive=" + caseSensitive);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Case 1a");
        }
    }

    @GetMapping("/case1b")
    public ResponseEntity<String> triggerCase1b(@RequestParam boolean caseSensitive) {
        try {
            edgeCaseChecker.runCase1b(caseSensitive);
            return ResponseEntity.ok("Case 1b executed with caseSensitive=" + caseSensitive);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Case 1b");
        }
    }

    @GetMapping("/case2")
    public ResponseEntity<String> triggerCase2() {
        try {
            edgeCaseChecker.runCase2();
            return ResponseEntity.ok("Case 2 executed");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Case 2");
        }
    }

    @GetMapping("/case4")
    public ResponseEntity<String> triggerCase4(@RequestParam boolean withSimilarTokens) {
        try {
            edgeCaseChecker.runCase4(withSimilarTokens);
            return ResponseEntity.ok("Case 4 executed with withSimilarTokens=" + withSimilarTokens);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing Case 4");
        }
    }
}
