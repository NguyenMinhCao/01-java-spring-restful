package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annontaion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final JobService jobService;
    private final UserService userService;

    public ResumeController(ResumeService resumeService, JobService jobService, UserService userService) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.jobService = jobService;
    }

    @PostMapping("/resumes")
    @ApiMessage("create resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        Optional<Job> jobOptionalById = this.jobService.handleFetchByIdJob(resume.getJob().getId());
        if (!jobOptionalById.isPresent()) {
            throw new IdInvalidException("Id Job không tồn tại");
        }

        if (this.userService.fetchUserById(resume.getUser().getId()) == null) {
            throw new IdInvalidException("Id User không tồn tại");
        }

        ResCreateResumeDTO resumeRes = this.resumeService.handleCreateResume(resume);

        return ResponseEntity.status(HttpStatus.CREATED).body(resumeRes);
    }

    @PutMapping("/resumes")
    @ApiMessage("update resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        if (!this.resumeService.findResumeById(resume.getId()).isPresent()) {
            throw new IdInvalidException("ID Resume không tồn tại");
        }

        ResUpdateResumeDTO resResume = this.resumeService.handleUpdateResume(resume);
        return ResponseEntity.ok().body(resResume);
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") Long id) throws IdInvalidException {
        if (!this.resumeService.findResumeById(id).isPresent()) {
            throw new IdInvalidException("ID Resume không tồn tại");
        }

        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("find resume by id")
    public ResponseEntity<ResResumeDTO> getResume(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Resume> resumeById = this.resumeService.findResumeById(id);
        if (!resumeById.isPresent()) {
            throw new IdInvalidException("ID Resume không tồn tại");
        }

        ResResumeDTO res = this.resumeService.findResumeConvertResResumeDTO(resumeById.get());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/resumes")
    @ApiMessage("find all resume")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumes(
            @Filter Specification<Resume> spec, Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.findAllResume(spec, pageable));
    }

}
