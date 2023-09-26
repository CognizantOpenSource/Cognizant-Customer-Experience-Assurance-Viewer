package com.cognizant.authapi.team;

import com.cognizant.authapi.team.beans.Team;
import com.cognizant.authapi.team.services.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TeamTest {
    @Autowired
    TeamService service;

    @Test
    void userBelongsToWhichTeams(){
//
    }
}
