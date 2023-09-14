package com.grigoryev.cleverbankreactiveremaster.controller.router;

import com.grigoryev.cleverbankreactiveremaster.controller.AccountController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class AccountRouter {

    private static final String ACCOUNTS = "/accounts";
    private static final String ID = "/{id}";

    @Bean
    public RouterFunction<ServerResponse> accountRoutes(AccountController accountController) {
        return RouterFunctions.route()
                .GET(ACCOUNTS + ID, accountController::findByIdResponse)
                .GET(ACCOUNTS, request -> accountController.findAllResponses())
                .POST(ACCOUNTS, accountController::save)
                .PUT(ACCOUNTS + ID, accountController::closeAccount)
                .DELETE(ACCOUNTS + ID, accountController::delete)
                .build();
    }

}
