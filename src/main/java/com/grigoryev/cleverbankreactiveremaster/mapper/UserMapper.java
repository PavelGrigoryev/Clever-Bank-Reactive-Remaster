package com.grigoryev.cleverbankreactiveremaster.mapper;

import com.grigoryev.cleverbankreactiveremaster.dto.user.UserRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserResponse toResponse(User user);

    User fromRequest(UserRequest request);

}
