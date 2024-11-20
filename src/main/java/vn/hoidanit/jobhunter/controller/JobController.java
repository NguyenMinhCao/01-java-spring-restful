package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annontaion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    @ApiMessage("fetch All Jobs")
    public ResponseEntity<ResultPaginationDTO> fetchAllJobs(
            @Filter Specification<Job> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAllJob(spec, pageable));
    }

    @PostMapping("/jobs")
    @ApiMessage("create Jobs")
    public ResponseEntity<ResCreateJobDTO> createJob(@RequestBody Job job) {

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleCreateJob(job));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("fetch by id Jobs")
    public ResponseEntity<Job> fetchById(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.handleFetchByIdJob(id);

        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Id Job không tồn tại");
        }

        Job job = jobOptional.get();
        return ResponseEntity.status(HttpStatus.OK).body(job);
    }

    @PutMapping("/jobs")
    @ApiMessage("update Jobs")
    public ResponseEntity<ResCreateJobDTO> updateJob(@RequestBody Job job) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.handleFetchByIdJob(job.getId());

        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleUpdateJob(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete Jobs")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Job> jobOptional = this.jobService.handleFetchByIdJob(id);

        if (!jobOptional.isPresent()) {
            throw new IdInvalidException("Job không tồn tại");
        }

        this.jobService.handleDeteleJob(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
