package com.sanchez.juarez.application.security.repositories;

import com.sanchez.juarez.application.security.dtos.AppUserDetails;

import java.util.Optional;

public interface UserCredentialsProvider {

    Optional <AppUserDetails> findUserName(String userName);
}
