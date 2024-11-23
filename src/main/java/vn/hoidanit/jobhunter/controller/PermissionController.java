package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
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
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (permissionService.isPermissionExits(permission)) {
            throw new IdInvalidException("Permisson đã tồn tại");
        }
        Permission pNew = this.permissionService.createPermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(pNew);
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (!this.permissionService.findPermissionById(permission.getId()).isPresent()) {
            throw new IdInvalidException("Permisson update không tồn tại");
        }

        if (permissionService.isPermissionExits(permission)) {
            if (this.permissionService.isSameName(permission)) {
                throw new IdInvalidException("Permisson đã tồn tại");
            }
        }
        Permission pNew = this.permissionService.updatePermission(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(pNew);
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> findAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable)
            throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.findAllPermissionPage(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) throws IdInvalidException {
        if (!this.permissionService.findPermissionById(id).isPresent()) {
            throw new IdInvalidException("Permisson id không tồn tại");
        }

        this.permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
