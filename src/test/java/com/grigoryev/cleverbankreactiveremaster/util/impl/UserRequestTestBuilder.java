package com.grigoryev.cleverbankreactiveremaster.util.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.user.UserRequest;
import com.grigoryev.cleverbankreactiveremaster.util.TestBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aUserRequest")
@With
public class UserRequestTestBuilder implements TestBuilder<UserRequest> {

    private String lastname = "Фёдорович";
    private String firstname = "Фёдор";
    private String surname = "Федосович";
    private String mobileNumber = "+7 (900) 222-77-14";

    @Override
    public UserRequest build() {
        return new UserRequest(lastname, firstname, surname, mobileNumber);
    }

}
