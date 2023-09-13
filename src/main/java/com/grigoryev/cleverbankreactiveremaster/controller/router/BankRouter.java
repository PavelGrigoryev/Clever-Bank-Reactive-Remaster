package com.grigoryev.cleverbankreactiveremaster.controller.router;

import com.grigoryev.cleverbankreactiveremaster.controller.BankController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BankRouter {

    private static final String BANKS = "/banks";
    private static final String ID = "/{id}";

    @Bean
    public RouterFunction<ServerResponse> bankRoutes(BankController bankController) {
        return RouterFunctions.route()
                .GET(BANKS + ID, bankController::findByIdResponse)
                .GET(BANKS, request -> bankController.findAll())
                .POST(BANKS, bankController::save)
                .PUT(BANKS + ID, bankController::update)
                .DELETE(BANKS + ID, bankController::delete)
                .build();
    }

}
