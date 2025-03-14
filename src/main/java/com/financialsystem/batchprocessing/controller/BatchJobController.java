package com.financialsystem.batchprocessing.controller;


import com.financialsystem.batchprocessing.service.BatchJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {

    private final BatchJobService batchJobService;


    public BatchJobController(BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }
    @PostMapping("/eod")
    public ResponseEntity<String> triggerEodJob() {
        try {
            batchJobService.runEodJobManually();
            return ResponseEntity.ok("EOD job triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to trigger EOD job: " + e.getMessage());
        }
    }

    @PostMapping("/eom")
    public ResponseEntity<String> triggerEomJob() {
        try {
            batchJobService.runEomJobManually();
            return ResponseEntity.ok("EOM job triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to trigger EOM job: " + e.getMessage());
        }
    }

    @PostMapping("/eoy")
    public ResponseEntity<String> triggerEoyJob() {
        try {
            batchJobService.runEoyJobManually();
            return ResponseEntity.ok("EOY job triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to trigger EOY job: " + e.getMessage());
        }
    }
}
