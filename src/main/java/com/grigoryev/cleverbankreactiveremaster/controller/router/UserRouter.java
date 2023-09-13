package com.grigoryev.cleverbankreactiveremaster.controller.router;

import com.grigoryev.cleverbankreactiveremaster.controller.UserController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class UserRouter {

    private static final String USERS = "/users";
    private static final String ID = "/{id}";

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserController userController) {
        return RouterFunctions.route()
                .GET(USERS + ID, userController::findByIdResponse)
                .GET(USERS, request -> userController.findAll())
                .POST(USERS, userController::save)
                .PUT(USERS + ID, userController::update)
                .DELETE(USERS + ID, userController::delete)
                .build();
    }

}
