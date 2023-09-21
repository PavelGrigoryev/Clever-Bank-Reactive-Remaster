package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aUserResponse")
@With
public class UserResponseTestBuilder implements TestBuilder<UserResponse> {

    private Long id = 1L;
    private String lastname = "Иванов";
    private String firstname = "Иван";
    private String surname = "Иванович";
    private LocalDate registerDate = LocalDate.of(1990, Month.JANUARY, 1);
    private String mobileNumber = "+7 (900) 123-45-67";

    @Override
    public UserResponse build() {
        return new UserResponse(id, lastname, firstname, surname, registerDate, mobileNumber);
    }

}
