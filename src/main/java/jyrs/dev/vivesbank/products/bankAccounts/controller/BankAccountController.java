package jyrs.dev.vivesbank.products.bankAccounts.controller;


import jyrs.dev.vivesbank.products.bankAccounts.dto.AccountUpdatedDto;
import jyrs.dev.vivesbank.products.bankAccounts.models.BankAccount;
import jyrs.dev.vivesbank.utils.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/bank-accounts")
@Slf4j
public class BankAccountController {


    /*
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Autowired
    public BankAccountController(AccountService accountService,
                                 AccountMapper accountMapper){
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<BankAccount>> findAll(
            // Parámetros de búsqueda
            @RequestParam(required = false) Optional<String> accountType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sort,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        log.info("Obteniendo cuentas bancarias con las siguientes opciones: " + accountType);
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(sort), sortBy);
        return ResponseEntity.ok(PageResponse.of(accountMapper.toBankAccountPage(accountService.findAll(accountType, pageable)), sortBy, sort));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<BankAccount> getById(@PathVariable Long id){
        log.info("Obteniendo cuenta bancaria con id: " + id);
        return ResponseEntity.ok(accountService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BankAccount> create(@RequestBody AccountDto accountDto){
        log.info("Creando cuenta bancaria: " + accountDto);
        var result = accountService.create(accountMapper.toAccount(accountDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<BankAccount> update(@PathVariable Long id, @RequestBody AccountUpdatedDto updatedAccount){
        log.info("Actualizando cuenta bancaria con id: " + id);
        return ResponseEntity.ok(accountService.update(id, accountMapper.toAccount(updatedAccount)));
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        log.info("Borrando cuenta bancaria con id: " + id);
        accountService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

     */
}
