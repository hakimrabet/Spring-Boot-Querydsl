package com.hamrasta.springbootquerydsl;

import com.hamrasta.springbootquerydsl.entity.QUser;
import com.hamrasta.springbootquerydsl.entity.User;
import com.hamrasta.springbootquerydsl.repository.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SpringBootQuerydslApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public SpringBootQuerydslApplication(UserRepository userRepository, JPAQueryFactory queryFactory) {
        this.userRepository = userRepository;
        this.queryFactory = queryFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootQuerydslApplication.class, args);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.DEFAULT,
            propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void run(String... args) {
        userRepository.deleteAll();

        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .age(22)
                .build());
        users.add(User.builder()
                .firstName("Tom")
                .lastName("Doe")
                .email("tom@doe.com")
                .age(21)
                .build());
        users.add(User.builder()
                .firstName("Ali")
                .lastName("hkm")
                .email("lala@land.com")
                .age(60)
                .build());
        // save all
        userRepository.saveAll(users);

        ////////////SELECT
        QUser qUser = QUser.user;
        BooleanExpression expression = qUser.email.like("%doe%").and(qUser.age.goe(22));
        System.out.println("First Result: " + userRepository.findAll(expression));

        ////////////SELECT
        List<User> usr = queryFactory.select(qUser).from(qUser).where(qUser.firstName.eq("Tom")).fetch();
        System.out.println("Second Result: " + usr);

        ////////////UPDATE
        //MUST USE @Transactional
        long update = queryFactory.update(qUser).set(qUser.age, 30).where(qUser.firstName.eq("John")).execute();
        System.out.println("update Result: " + update);


        ////////////DELETE
        //MUST USE @Transactional
        long delete = queryFactory.delete(qUser).where(qUser.firstName.eq("ali")).execute();
        System.out.println("delete result: " + delete);
    }

}
