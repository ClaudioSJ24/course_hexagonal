package com.sanchez.juarez.application.security.repositories;

import com.sanchez.juarez.application.security.dtos.AccountStatus;
import com.sanchez.juarez.application.security.dtos.AppRole;
import com.sanchez.juarez.application.security.dtos.AppUserDetails;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserCredentialsProvider  implements  UserCredentialsProvider{

    private  static final Map<String, AppUserDetails> USERS = Map.of(
            /*
             * ADMIN: $2a$12$OttQLJ.nsMYBgB8Nb05CA.uAn9G51/3bFSwjjfixTio2L.ElqVckS
             * MANAGER: $2a$12$x9zYdTyRRIPaU2MHL5bfoOweCiLX89wVRzvVvJqygdHWrpW5FbeSe
             * EMPLOYEE: $2a$12$/qCw4vcTGJcdb9DpN/WOD.NpUpi9lngXyDkVd1EUswKcweFazla92
             */
            "admin", new AppUserDetails(
                    "admin",
                    // {bcrypt} — Spring Security 7 requiere password encoding
                    "$2a$12$OttQLJ.nsMYBgB8Nb05CA.uAn9G51/3bFSwjjfixTio2L.ElqVckS",
                    AccountStatus.ACTIVE,
                    Set.of(AppRole.admin())
            ),
            "manager", new AppUserDetails(
                    "manager",
                    "$2a$12$x9zYdTyRRIPaU2MHL5bfoOweCiLX89wVRzvVvJqygdHWrpW5FbeSe",
                    AccountStatus.ACTIVE,
                    Set.of(AppRole.user())   // manager = USER role en tu tabla
            ),

            "employee", new AppUserDetails(
                    "employee",
                    "$2a$12$/qCw4vcTGJcdb9DpN/WOD.NpUpi9lngXyDkVd1EUswKcweFazla92",
                    AccountStatus.ACTIVE,
                    Set.of(AppRole.user())
            )
    );

    @Override
    public Optional<AppUserDetails> findUserName(String userName) {
        return Optional.ofNullable(USERS.get(userName));
    }
}
