package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role createRole(Role role) {
        List<Permission> lstP = new ArrayList<>();

        List<Permission> findAllPRole = role.getPermissions();
        if (findAllPRole != null) {
            for (Permission p : findAllPRole) {
                Optional<Permission> findPById = this.permissionRepository.findById(p.getId());
                if (findPById.isPresent()) {
                    lstP.add(findPById.get());
                }
            }
        }

        role.setPermissions(lstP);
        return this.roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        Optional<Role> getRById = this.roleRepository.findById(role.getId());
        List<Permission> lstP = new ArrayList<>();

        List<Permission> findAllPRole = role.getPermissions();
        if (findAllPRole != null) {
            for (Permission p : findAllPRole) {
                Optional<Permission> findPById = this.permissionRepository.findById(p.getId());
                if (findPById.isPresent()) {
                    lstP.add(findPById.get());
                }
            }
        }

        if (getRById.isPresent()) {
            getRById.get().setName(role.getName());
            getRById.get().setDescription(role.getDescription());
            getRById.get().setActive(role.isActive());
            getRById.get().setPermissions(lstP);
            return this.roleRepository.save(getRById.get());
        }
        return null;
    }

    public ResultPaginationDTO findAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        List<Role> lstRole = pageRole.getContent();

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageRole.getNumber() + 1);
        mt.setPageSize(pageRole.getSize());
        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        res.setMeta(mt);
        res.setResult(lstRole);

        return res;
    }

    public Optional<Role> findRoleByName(String name) {
        return this.roleRepository.findRoleByName(name);
    }

    public Optional<Role> findRoleById(Long id) {
        return this.roleRepository.findById(id);
    }

    public void deleteRoleById(Long id) {
        this.roleRepository.deleteById(id);
    }

}
