package com.example.userproject.services;
import com.example.userproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    //we only add custom queries that are not included in JpaRepository. like the one below
    Boolean existsByEmail(String Email) ; //optional if User is not found in the repo


    Boolean existsByKeycloakId(String userId);

    User findByEmail(String email);
}
