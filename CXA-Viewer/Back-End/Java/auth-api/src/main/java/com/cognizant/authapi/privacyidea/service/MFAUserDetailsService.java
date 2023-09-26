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

package com.cognizant.authapi.privacyidea.service;

import com.cognizant.authapi.base.error.InvalidDetailsException;
import com.cognizant.authapi.base.error.ResourceNotFoundException;
import com.cognizant.authapi.privacyidea.models.MFAUserDetails;
import com.cognizant.authapi.privacyidea.repo.MFAUserDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by 784420 on 6/10/2021 3:34 AM
 */
@Service
@AllArgsConstructor
public class MFAUserDetailsService {

    private MFAUserDetailsRepository repository;

    public List<MFAUserDetails> getAll(){
        return repository.findAll();
    }

    public Optional<MFAUserDetails> get(String email){
        return repository.findById(email);
    }

    public MFAUserDetails assertOrGet(String email){
        return get(email).orElseThrow(() -> new ResourceNotFoundException("MFAUserDetails", "email", email));
    }

    public MFAUserDetails save(MFAUserDetails mfaUserDetails){
        return repository.save(mfaUserDetails);
    }

    public void deleteById(String email){
        repository.deleteById(email);
    }
}
