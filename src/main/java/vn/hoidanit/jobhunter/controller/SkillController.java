package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annontaion.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    @ApiMessage("fetch All skill")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkill(
            @Filter Specification<Skill> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkill(spec, pageable));
    }

    @PostMapping("/skills")
    @ApiMessage("create a skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {

        if (this.skillService.checkExitsByName(skill.getName())) {
            throw new IdInvalidException("Skill name = " + skill.getName() + " đã tồn tại");
        }

        return ResponseEntity.ok().body(this.skillService.handleCreateSkill(skill));
    }

    @PutMapping("/skills")
    @ApiMessage("update a skill")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) throws IdInvalidException {
        Optional<Skill> skOptional = this.skillService.handleFetchById(skill.getId());

        if (!skOptional.isPresent()) {
            throw new IdInvalidException("Skill id không tồn tại");
        }

        if (this.skillService.checkExitsByName(skill.getName())) {
            throw new IdInvalidException("Skill name = " + skill.getName() + " đã tồn tại");
        }

        return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(skill));
    }

    @GetMapping("/skills/{id}")
    @ApiMessage("fetch a skill")
    public ResponseEntity<Skill> fetchSkillByID(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Skill> skOptional = this.skillService.handleFetchById(id);

        if (!skOptional.isPresent()) {
            throw new IdInvalidException("Skill không tồn tại");
        }

        return ResponseEntity.ok().body(this.skillService.handleFetchById(id).get());
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Skill> skOptional = this.skillService.handleFetchById(id);

        if (!skOptional.isPresent()) {
            throw new IdInvalidException("Skill không tồn tại");
        }

        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}
