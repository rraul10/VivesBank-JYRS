package jyrs.dev.vivesbank.products.bankAccounts.controller;

import jakarta.servlet.http.HttpServletRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountRequest;
import jyrs.dev.vivesbank.products.bankAccounts.dto.BankAccountResponse;
import jyrs.dev.vivesbank.products.bankAccounts.services.BankAccountService;
import jyrs.dev.vivesbank.utils.pagination.PageResponse;
import jyrs.dev.vivesbank.utils.pagination.PaginationLinksUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Controller
@RequestMapping("/bank-accounts")
@Slf4j
public class BankAccountController {

    private final BankAccountService accountService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public BankAccountController(BankAccountService accountService, PaginationLinksUtils paginationLinksUtils){
        this.accountService = accountService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping
    public ResponseEntity<PageResponse<BankAccountResponse>> findAllBankAccounts(
            @RequestParam(required = false) Optional<String> accountType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Buscando todas las cuentas bancarias con las opciones: accountType={}", accountType);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<BankAccountResponse> pageResult = accountService.findAllBankAccounts(accountType, PageRequest.of(page, size, sort) );

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }


    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponse> getById(@PathVariable Long id){
        log.info("Obteniendo cuenta bancaria con id: " + id);
        return ResponseEntity.ok(accountService.findBankAccountById(id));
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> create(@RequestBody BankAccountRequest bankAccountRequest){
        log.info("Creando cuenta bancaria: " + bankAccountRequest);
        var result = accountService.saveBankAccount(bankAccountRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        log.info("Borrando cuenta bancaria con id: " + id);
        accountService.deleteBankAccount(id);
        return ResponseEntity.noContent().build();
    }
}
