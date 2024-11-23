package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    public boolean isPermissionExits(Permission p) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod());
    }

    public Permission createPermission(Permission p) {
        return this.permissionRepository.save(p);
    }

    public Permission updatePermission(Permission p) {
        Permission getPermission = this.permissionRepository.findById(p.getId()).get();
        if (getPermission != null) {
            getPermission.setName(p.getName());
            getPermission.setMethod(p.getMethod());
            getPermission.setApiPath(p.getApiPath());
            getPermission.setModule(p.getModule());
            return this.permissionRepository.save(getPermission);
        }
        return null;
    }

    public Optional<Permission> findPermissionById(Long id) {
        return this.permissionRepository.findById(id);
    }

    public ResultPaginationDTO findAllPermissionPage(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(spec, pageable);

        List<Permission> lstPermission = page.getContent();

        ResultPaginationDTO res = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        res.setMeta(mt);
        res.setResult(lstPermission);

        return res;
    }

    public void deletePermission(Long permissionId) {
        Permission permission = this.permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        if (permission != null) {
            for (Role role : permission.getRoles()) {
                role.getPermissions().remove(permission);
                this.roleRepository.save(role);
            }
        }

        this.permissionRepository.delete(permission);
    }

    public boolean isSameName(Permission permission) {
        Optional<Permission> permissionDb = this.permissionRepository.findById(permission.getId());
        if (permissionDb.isPresent()) {
            if (permission.getName().equals(permissionDb.get().getName())) {
                return true;
            }
        }
        return false;
    }
}
