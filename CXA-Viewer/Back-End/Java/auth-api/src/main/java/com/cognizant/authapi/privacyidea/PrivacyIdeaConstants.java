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

/**
 * Created by 784420 on 6/9/2021 6:24 AM
 */
public class PrivacyIdeaConstants {
    private PrivacyIdeaConstants() {
    }
    /*Constants*/
    public static final int OTP_LEN = 6;
    public static final String HASHLIB = "sha1";
    public static final int TIME_STEP = 30;
    public static final String OTP_TRIGGER_MESSAGE = "Enter the OTP from the Email:";
    public static final String OTP_VERIFY_MESSAGE = "matching";

    /*error messages*/
    public static final String MFA_IS_NOT_ENABLED_FOR_EMAIL = "MFA is not enabled for the mentioned email (%s)";

    /*Json Request Body*/
    public static final String TRIGGER_OTP_TEMPLATE = "{\n \"user\": \"${email}\",\n \"realm\": \"${realm}\",\n \"pass\" : \"${pin}\"\n }";
    public static final String AUTH_TOKEN_TEMPLATE = "{\n \"username\":\"${username}\",\n \"password\":\"${token}\",\n \"realm\":\"${realm}\"\n}";
    public static final String USER_JSON_TEMPLATE = "{\n \"user\": \"${email}\",\n \"password\": \"${internalToken}\",\n " +
                                                            "\"resolver\": \"${resolver}\",\n \"email\": \"${email}\"\n }";
    public static final String ACCESS_TOKEN_JSON_TEMPLATE = "{\n" +
            "    \"email\": \"${email}\", \"description\": \"Otp is:\", \"dynamic_email\": false, \"genkey\": true,\n" +
            "    \"user\": \"${email}\", \"realm\": \"${realm}\", \"type\": \"email\", \"2stepinit\": false, \"otplen\": ${otplen},\n" +
            "    \"pin\": \"${pin}\", \"hashlib\": \"${hashlib}\", \"validity_period_end\": \"\", \"validity_period_start\": \"\",\n" +
            "    \"radius.system_settings\": true, \"timeStep\": ${timeStep}\n" +
            "}";
}
