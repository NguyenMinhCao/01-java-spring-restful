package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanySevice;
import vn.hoidanit.jobhunter.util.annontaion.ApiMessage;

@Controller
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanySevice companySevice;

    public CompanyController(CompanySevice companySevice) {
        this.companySevice = companySevice;
    }

    @PostMapping("/companies")
    @ApiMessage("create company")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company comp) {
        Company company = this.companySevice.handleCreateCompany(comp);
        return ResponseEntity.ok().body(company);
    }

    @GetMapping("/companies")
    @ApiMessage("get all company")
    public ResponseEntity<ResultPaginationDTO> getAllCompany(
            @Filter Specification<Company> spec, Pageable pageable) {

        return ResponseEntity.ok().body(this.companySevice.fetchAllCompany(spec, pageable));
    }

    @PutMapping("/companies")
    @ApiMessage("update company")
    public ResponseEntity<Company> handleUpdateCompany(@RequestBody Company comp) {
        Company companyUpdate = this.companySevice.handleUpdateCompany(comp);
        return ResponseEntity.ok().body(companyUpdate);
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("delete company by id")
    public ResponseEntity<Void> handleDeleteCompany(@PathVariable("id") long id) {
        this.companySevice.handleDeleteCompany(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> findCompanyById(@PathVariable("id") long id) {
        Company company = this.companySevice.findCompanyById(id).get();
//        return ResponseEntity.ok().body(company);
        return ResponseEntity.ok().body(company);
    }

}
