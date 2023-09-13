package com.grigoryev.cleverbankreactiveremaster.repository.impl;

import com.grigoryev.cleverbankreactiveremaster.repository.UserRepository;
import com.grigoryev.cleverbankrectiveremaster.tables.pojos.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.grigoryev.cleverbankrectiveremaster.Tables.ACCOUNT;
import static com.grigoryev.cleverbankrectiveremaster.Tables.USER;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dslContext;

    @Override
    public Mono<User> findById(Long id) {
        return Mono.from(dslContext.selectFrom(USER)
                        .where(USER.ID.eq(id)))
                .map(r -> r.into(User.class));
    }

    @Override
    public Flux<User> findAll() {
        return Flux.from(dslContext.selectFrom(USER))
                .map(r -> r.into(User.class));
    }

    @Override
    public Mono<User> save(User user) {
        return Mono.from(dslContext.insertInto(USER)
                        .set(USER.LASTNAME, user.getLastname())
                        .set(USER.FIRSTNAME, user.getFirstname())
                        .set(USER.SURNAME, user.getSurname())
                        .set(USER.REGISTER_DATE, user.getRegisterDate())
                        .set(USER.MOBILE_NUMBER, user.getMobileNumber())
                        .onDuplicateKeyIgnore()
                        .returning())
                .map(r -> r.into(User.class));
    }

    @Override
    public Mono<User> update(User user) {
        return Mono.from(dslContext.update(USER)
                        .set(USER.LASTNAME, user.getLastname())
                        .set(USER.FIRSTNAME, user.getFirstname())
                        .set(USER.SURNAME, user.getSurname())
                        .set(USER.REGISTER_DATE, user.getRegisterDate())
                        .set(USER.MOBILE_NUMBER, user.getMobileNumber())
                        .where(USER.ID.eq(user.getId()))
                        .returning())
                .onErrorComplete()
                .map(r -> r.into(User.class));
    }

    @Override
    public Mono<User> delete(Long id) {
        deleteAllUserAccounts(id);
        return Mono.from(dslContext.deleteFrom(USER)
                        .where(USER.ID.eq(id))
                        .returning())
                .map(r -> r.into(User.class));
    }

    private void deleteAllUserAccounts(Long userId) {
        Mono.from(dslContext.deleteFrom(ACCOUNT)
                        .where(ACCOUNT.USER_ID.eq(userId)))
                .subscribe();
    }

}
