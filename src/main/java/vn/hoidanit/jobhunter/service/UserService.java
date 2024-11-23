package vn.hoidanit.jobhunter.service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO.CompanyUser;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    public PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
            RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResUserDTO fetchFRestUserById(long id) {
        User user = this.fetchUserById(id);
        if (user != null) {
            ResUserDTO res = convertUserToResUserDTO(user);
            return res;
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);

        List<User> lstUser = page.getContent();

        List<ResUserDTO> userRs = new ArrayList<>();
        for (User user : lstUser) {
            ResUserDTO res = convertUserToResUserDTO(user);
            userRs.add(res);
        }

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(userRs);
        return rs;
    }

    public ResUserDTO convertUserToResUserDTO(User user) {
        Company companyById = user.getCompany() == null
                ? null
                : this.companyRepository.findById(user.getCompany().getId()).get();

        CompanyUser company = new CompanyUser();

        ResUserDTO dto = new ResUserDTO();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender());
        dto.setAge(user.getAge());
        dto.setCreateAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        if (companyById != null) {
            company.setId(companyById.getId());
            company.setName(companyById.getName());
            dto.setCompany(company);
        }
        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            dto.setRole(roleUser);
        }
        return dto;
    }

    public Boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO handleCreateUser(User user, Long companyId) {
        if (this.isEmailExist(user.getEmail()) == true) {
            return null;
        } else {
            // check company
            Company companyById = !this.companyRepository.findById(companyId).isPresent() || companyId == 0
                    ? null
                    : this.companyRepository.findById(companyId).get();
            if (companyById == null) {
                user.setCompany(null);
            }
            // check role
            if (user.getRole() != null) {
                Role r = this.roleRepository.findById(user.getRole().getId()).get();
                user.setRole(r);
            }
            this.userRepository.save(user);

            ResCreateUserDTO userDTO = new ResCreateUserDTO();
            CompanyUser companyUser = new CompanyUser();
            if (companyById != null) {
                companyUser.setId(companyById.getId());
                companyUser.setName(companyById.getName());
            }

            userDTO.setId(user.getId());
            userDTO.setAge(user.getAge());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setAddress(user.getAddress());
            userDTO.setCompany(companyUser != null ? companyUser : null);
            userDTO.setCreatedAt(user.getCreatedAt());
            userDTO.setGender(user.getGender().toString());
            return userDTO;
        }
    }

    public ResUpdateUserDTO handleUpdateUser(User user, Long companyId) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            Company companyById = !this.companyRepository.findById(companyId).isPresent() || companyId == 0
                    ? null
                    : this.companyRepository.findById(companyId).get();

            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser.setRole(user.getRole());
            currentUser.setCompany(companyById == null ? null : companyById);
            this.userRepository.save(currentUser);

            CompanyUser companyUser = new CompanyUser();
            if (companyById != null) {
                companyUser.setId(companyById.getId());
                companyUser.setName(companyById.getName());
            }

            ResUpdateUserDTO resUd = new ResUpdateUserDTO();
            resUd.setId(currentUser.getId());
            resUd.setAddress(currentUser.getAddress());
            resUd.setName(currentUser.getName());
            resUd.setGender(currentUser.getGender());
            resUd.setCompany(companyUser != null ? companyUser : null);
            resUd.setAge(currentUser.getAge());
            resUd.setUpdatedAt(currentUser.getUpdatedAt());
            return resUd;
        }
        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User findUserWithPermissionsByUsername(String username) {
        return this.userRepository.findUserWithPermissionsByEmail(username);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String refreshToken, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
