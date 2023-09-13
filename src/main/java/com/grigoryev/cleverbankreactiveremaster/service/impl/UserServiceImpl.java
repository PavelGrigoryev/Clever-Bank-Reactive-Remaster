package com.grigoryev.cleverbankreactiveremaster.service.impl;

import com.grigoryev.cleverbankreactiveremaster.dto.DeleteResponse;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserRequest;
import com.grigoryev.cleverbankreactiveremaster.dto.user.UserResponse;
import com.grigoryev.cleverbankreactiveremaster.exception.badrequest.UniquePhoneNumberException;
import com.grigoryev.cleverbankreactiveremaster.exception.notfound.UserNotFoundException;
import com.grigoryev.cleverbankreactiveremaster.mapper.UserMapper;
import com.grigoryev.cleverbankreactiveremaster.repository.UserRepository;
import com.grigoryev.cleverbankreactiveremaster.service.UserService;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with ID " + id + " is not found!")));
    }

    @Override
    public Mono<UserResponse> findByIdResponse(Long id) {
        return findById(id)
                .map(userMapper::toResponse);
    }

    @Override
    public Flux<UserResponse> findAll() {
        return userRepository.findAll()
                .map(userMapper::toResponse);
    }

    @Override
    public Mono<UserResponse> save(UserRequest request) {
        return Mono.just(request)
                .map(userMapper::fromRequest)
                .map(user -> user.setRegisterDate(LocalDate.now()))
                .flatMap(userRepository::save)
                .map(userMapper::toResponse)
                .switchIfEmpty(Mono.error(new UniquePhoneNumberException("User with phone number "
                                                                         + request.mobileNumber() + " is already exist")));
    }

    @Override
    public Mono<UserResponse> update(Long id, UserRequest request) {
        return findById(id)
                .flatMap(userById -> {
                    User user = userMapper.fromRequest(request);
                    user.setId(userById.getId());
                    user.setRegisterDate(userById.getRegisterDate());
                    return userRepository.update(user);
                })
                .map(userMapper::toResponse)
                .switchIfEmpty(Mono.error(new UniquePhoneNumberException("User with phone number "
                                                                         + request.mobileNumber() + " is already exist")));
    }

    @Override
    public Mono<DeleteResponse> delete(Long id) {
        return userRepository.delete(id)
                .map(user -> new DeleteResponse("User with ID " + id + " was successfully deleted"))
                .switchIfEmpty(Mono.error(new UserNotFoundException("No User with ID " + id + " to delete")));
    }

}
