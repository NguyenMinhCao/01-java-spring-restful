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

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    public PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            ResUserDTO userRs = new ResUserDTO();
            userRs.setId(user.getId());
            userRs.setName(user.getName());
            userRs.setAddress(user.getAddress());
            userRs.setAge(user.getAge());
            userRs.setGender(user.getGender());
            userRs.setCreateAt(user.getCreatedAt());
            userRs.setUpdatedAt(user.getUpdatedAt());
            return userRs;
        }
        return null;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);

        List<User> lstUser = page.getContent();

        List<ResUserDTO> userRs = new ArrayList<>();
        for (User user : lstUser) {
            ResUserDTO dto = new ResUserDTO();

            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setAddress(user.getAddress());
            dto.setGender(user.getGender());
            dto.setAge(user.getAge());
            dto.setCreateAt(user.getCreatedAt());
            dto.setUpdatedAt(user.getUpdatedAt());

            userRs.add(dto);
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

    public Boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO handleCreateUser(User user) {
        if (this.isEmailExist(user.getEmail()) == true) {
            return null;
        } else {
            this.userRepository.save(user);
            ResCreateUserDTO userDTO = new ResCreateUserDTO();
            userDTO.setId(user.getId());
            userDTO.setAge(user.getAge());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setAddress(user.getAddress());
            userDTO.setCreatedAt(user.getCreatedAt());
            userDTO.setGender(user.getGender().toString());
            return userDTO;
        }
    }

    public ResUpdateUserDTO handleUpdateUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            this.userRepository.save(currentUser);
            ResUpdateUserDTO resUd = new ResUpdateUserDTO();
            resUd.setId(currentUser.getId());
            resUd.setAddress(currentUser.getAddress());
            resUd.setName(currentUser.getName());
            resUd.setGender(currentUser.getGender());
            resUd.setAge(currentUser.getAge());
            resUd.setUpdatedAt(currentUser.getUpdatedAt());
            return resUd;
        }
        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
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
