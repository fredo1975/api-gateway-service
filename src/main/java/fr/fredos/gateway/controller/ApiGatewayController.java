package fr.fredos.gateway.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGatewayController {

	@GetMapping("/")  
    public String index(Principal principal) {  
        return principal.getName();  
    }
}
