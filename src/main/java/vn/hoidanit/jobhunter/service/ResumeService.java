package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO.JobResume;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO.UserResume;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.util.SecurityUtil;

@Service
public class ResumeService {
    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public ResCreateResumeDTO handleCreateResume(Resume resume) {
        Resume resumeNew = this.resumeRepository.save(resume);
        ResCreateResumeDTO resResume = new ResCreateResumeDTO();
        resResume.setId(resumeNew.getId());
        resResume.setCreatedAt(resumeNew.getCreatedAt());
        resResume.setCreateBy(resumeNew.getCreateBy());
        return resResume;
    }

    public Optional<Resume> findResumeById(Long id) {
        return this.resumeRepository.findById(id);
    }

    public ResResumeDTO findResumeConvertResResumeDTO(Resume resume) {
        Resume resumeById = this.resumeRepository.findById(resume.getId()).get();
        ResResumeDTO res = new ResResumeDTO();
        UserResume userRes = new UserResume();
        JobResume jobRes = new JobResume();
        if (resumeById != null) {
            res.setId(resumeById.getId());
            res.setEmail(resumeById.getEmail());
            res.setStatus(resumeById.getStatus());
            res.setUrl(resumeById.getUrl());
            res.setCreateBy(resumeById.getCreateBy());
            res.setUpdateBy(resumeById.getUpdateBy());
            res.setCreatedAt(resumeById.getCreatedAt());
            if (resume.getJob().getCompany() != null) {
                res.setCompanyName(resume.getJob().getCompany().getName());
            }
            res.setUpdatedAt(resumeById.getUpdatedAt());
            userRes.setId(resumeById.getUser().getId());
            userRes.setName(resumeById.getUser().getName());
            jobRes.setId(resumeById.getJob().getId());
            jobRes.setName(resumeById.getJob().getName());
            res.setUser(userRes);
            res.setJob(jobRes);
            return res;
        }
        return null;
    }

    public ResUpdateResumeDTO handleUpdateResume(Resume resume) {
        Resume resumeById = this.resumeRepository.findById(resume.getId()).get();
        ResUpdateResumeDTO resUpdate = new ResUpdateResumeDTO();
        if (resumeById != null) {
            resumeById.setStatus(resume.getStatus());
            resUpdate.setUpdatedAt(resume.getUpdatedAt());
            resUpdate.setUpdatedAt(resume.getUpdatedAt());
            this.resumeRepository.save(resumeById);
            resUpdate.setUpdatedAt(resumeById.getUpdatedAt());
            resUpdate.setUpdatedBy(resumeById.getCreateBy());
            return resUpdate;
        }
        return null;
    }

    public void deleteResume(Long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResultPaginationDTO findAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> page = this.resumeRepository.findAll(spec, pageable);

        List<Resume> lstResume = page.getContent();
        List<ResResumeDTO> lstRes = new ArrayList<>();
        for (Resume resume : lstResume) {
            ResResumeDTO res = this.findResumeConvertResResumeDTO(resume);
            lstRes.add(res);
        }

        ResultPaginationDTO resumeRes = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        resumeRes.setMeta(mt);
        resumeRes.setResult(lstRes);
        return resumeRes;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageResume.getNumber() + 1);
        mt.setPageSize(pageResume.getSize());
        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        result.setMeta(mt);
        result.setResult(pageResume.getContent());

        return result;
    }

}
