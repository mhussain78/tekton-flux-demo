package com.mhussain.tektonfluxdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    @GetMapping("/api/customers")
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

}