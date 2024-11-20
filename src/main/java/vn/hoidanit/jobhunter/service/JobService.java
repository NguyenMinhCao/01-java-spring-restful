package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
    }

    public ResultPaginationDTO fetchAllJob(Specification<Job> spec, Pageable pageable) {

        Page<Job> page = jobRepository.findAll(spec, pageable);

        List<Job> lstJob = page.getContent();

        ResultPaginationDTO jobRs = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        jobRs.setMeta(mt);
        jobRs.setResult(lstJob);

        return jobRs;
    }

    public ResCreateJobDTO handleCreateJob(Job job) {
        List<Skill> lstSkill = new ArrayList<>();
        for (Skill lstSkillFetch : job.getSkills()) {
            Skill fetchSkillById = this.skillRepository.findById(lstSkillFetch.getId()).isPresent()
                    ? this.skillRepository.findById(lstSkillFetch.getId()).get()
                    : null;
            if (fetchSkillById != null) {
                lstSkill.add(fetchSkillById);
            }
        }

        if (job.getCompany() != null) {
            Optional<Company> companyById = this.companyRepository.findById(job.getCompany().getId());
            if (companyById.isPresent()) {
                job.setCompany(companyById.get());
            }
        }

        job.setSkills(lstSkill);
        Job jobCreate = this.jobRepository.save(job);

        ResCreateJobDTO resJob = convertJobToResJobDTO(jobCreate);

        return resJob;
    }

    public Optional<Job> handleFetchByIdJob(Long id) {
        return this.jobRepository.findById(id);
    }

    public ResCreateJobDTO handleUpdateJob(Job job) {
        Optional<Job> jobOptional = this.jobRepository.findById(job.getId());
        if (jobOptional.isPresent()) {
            List<Skill> lstSkill = new ArrayList<>();
            for (Skill lstSkillFetch : job.getSkills()) {
                Skill fetchSkillById = this.skillRepository.findById(lstSkillFetch.getId()).isPresent()
                        ? this.skillRepository.findById(lstSkillFetch.getId()).get()
                        : null;
                if (fetchSkillById != null) {
                    lstSkill.add(fetchSkillById);
                }
            }

            Job currentJob = jobOptional.get();
            currentJob.setName(job.getName());
            currentJob.setLocation(job.getLocation());
            currentJob.setSalary(job.getSalary());
            currentJob.setQuantity(job.getQuantity());
            currentJob.setLevel(job.getLevel());
            if (job.getCompany() != null) {
                Optional<Company> companyById = this.companyRepository.findById(job.getCompany().getId());
                if (companyById.isPresent()) {
                    currentJob.setCompany(companyById.get());
                }
            }
            currentJob.setDescription(job.getDescription());
            currentJob.setActive(job.isActive());
            currentJob.setSkills(lstSkill);
            Job jobUpdate = this.jobRepository.save(currentJob);

            ResCreateJobDTO resJob = convertJobToResJobDTO(jobUpdate);

            return resJob;
        }
        return null;
    }

    public ResCreateJobDTO convertJobToResJobDTO(Job job) {
        ResCreateJobDTO resJob = new ResCreateJobDTO();
        resJob.setId(job.getId());
        resJob.setName(job.getName());
        resJob.setLocation(job.getLocation());
        resJob.setSalary(job.getSalary());
        resJob.setQuantity(job.getQuantity());
        resJob.setLevel(job.getLevel());
        resJob.setStartDate(job.getStartDate());
        resJob.setEndDate(job.getEndDate());
        resJob.setActive(job.isActive());
        resJob.setCompanyName(job.getCompany().getName());
        resJob.setCreatedAt(job.getCreatedAt());
        resJob.setCreateBy(job.getCreateBy());

        List<String> lstNameSkill = new ArrayList<>();
        for (Skill skill : job.getSkills()) {
            lstNameSkill.add(skill.getName());
        }
        resJob.setSkills(lstNameSkill);

        return resJob;
    }

    public void handleDeteleJob(Long id) {
        this.jobRepository.deleteById(id);
    }
}
