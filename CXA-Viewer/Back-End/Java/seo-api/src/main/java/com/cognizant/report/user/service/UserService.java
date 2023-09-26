/*
 *
 *   Copyright (C) 2023 - Cognizant Technology Solutions
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.cognizant.report.user.service;

import com.cognizant.report.base.models.Account;
import com.cognizant.report.base.models.User;
import com.cognizant.report.base.models.UserPrincipal;
import com.cognizant.report.base.repos.UserRepository;
import com.cognizant.report.user.repo.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;

    public User getCurrentUser(){
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        return user.isPresent() ? user.get() : null;
    }

    public void addProjectIdToAccount(String id) {
        User currentUser = getCurrentUser();
        if (currentUser!=null){
            Account account = currentUser.getAccount();
            if (account.getProjectIds() == null) account.setProjectIds(new ArrayList<>());
            if (account.getOwnProjectIds() == null) account.setOwnProjectIds(new ArrayList<>());

            account.getOwnProjectIds().add(id);
            account.getProjectIds().add(id);
            accountRepository.save(account);
        }
    }
}
