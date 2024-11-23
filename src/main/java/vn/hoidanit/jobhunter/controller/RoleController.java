package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.turkraft.springfilter.boot.Filter;

@Controller
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws IdInvalidException {
        if (this.roleService.findRoleByName(role.getName()).isPresent()) {
            throw new IdInvalidException("Role name  đã tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }

    @PutMapping("/roles")
    public ResponseEntity<Role> updateRole(@RequestBody Role role) throws IdInvalidException {
        if (!this.roleService.findRoleById(role.getId()).isPresent()) {
            throw new IdInvalidException("Role Id không tồn tại");
        }

        return ResponseEntity.ok().body(this.roleService.updateRole(role));
    }

    @GetMapping("/roles")
    public ResponseEntity<ResultPaginationDTO> findAllRole(@Filter Specification<Role> spec, Pageable pageable) {

        return ResponseEntity.ok().body(this.roleService.findAllRole(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) throws IdInvalidException {
        if (!this.roleService.findRoleById(id).isPresent()) {
            throw new IdInvalidException("Role Id không tồn tại");
        }

        this.roleService.deleteRoleById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> findRoleById(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Role> roleById = this.roleService.findRoleById(id);
        if (!roleById.isPresent()) {
            throw new IdInvalidException("Role Id không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(roleById.get());
    }

}
