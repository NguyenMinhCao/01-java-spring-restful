package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public ResultPaginationDTO fetchAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> page = this.skillRepository.findAll(spec, pageable);

        ResultPaginationDTO resPage = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        resPage.setMeta(mt);

        resPage.setResult(page.getContent());
        return resPage;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill handleUpdateSkill(Skill skill) {
        Optional<Skill> skOptional = this.skillRepository.findById(skill.getId());
        if (skOptional.isPresent()) {
            Skill skillUpdate = skOptional.get();
            skillUpdate.setName(skill.getName());
            skillUpdate.setJobs(skill.getJobs());
            return this.skillRepository.save(skillUpdate);
        }
        return null;
    }

    public Optional<Skill> handleFetchById(Long id) {
        return this.skillRepository.findById(id);
    }

    public void handleDeleteSkill(Long id) {
        Optional<Skill> currentSkill = this.skillRepository.findById(id);
        if (currentSkill != null) {
            List<Subscriber> lstSub = currentSkill.get().getSubscribers();
            for (Subscriber subscriber : lstSub) {
                subscriber.getSkills().remove(currentSkill.get());
            }
        }
        this.skillRepository.delete(currentSkill.get());
    }

    public boolean checkExitsByName(String name) {
        return this.skillRepository.existsByName(name);
    }
}
