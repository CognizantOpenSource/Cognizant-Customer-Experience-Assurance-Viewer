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

package com.cognizant.authapi.privacyidea;

import com.cognizant.authapi.base.beans.ApiResponse;
import com.cognizant.authapi.privacyidea.service.PrivacyIdeaService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Created by 784420 on 6/11/2021 1:57 AM
 */
@RestController
@RequestMapping("/privacy/idea")
@AllArgsConstructor
public class PrivacyIdeaController {

    private PrivacyIdeaService service;

    @PostMapping("/enable-mfa")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse enableMFA(@RequestParam String email, @RequestParam boolean isMFA) {
        service.enableMFA(email, isMFA);
        return new ApiResponse(LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Success",
                String.format("Successfully enabled/disabled MFA for mentioned email(%s)", email)
        );
    }

    @PostMapping("/triggerOTP")
    public ApiResponse triggerOTP(@RequestParam String email) {
        service.triggerOTP(email);
        return new ApiResponse(LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Success",
                String.format("Successfully Triggered OTP for mentioned email(%s)", email)
        );
    }

    @PostMapping("/verifyOTP")
    public ApiResponse verifyOTP(@RequestParam String email, @RequestParam String otp) {
        service.verifyOTP(email, otp);
        return new ApiResponse(LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Success",
                String.format("Successfully verified OTP for mentioned email(%s)", email)
        );
    }

    @DeleteMapping
    public void deleteUserInMFA(@RequestParam String email) {
        service.deleteUser(email);
    }

}
