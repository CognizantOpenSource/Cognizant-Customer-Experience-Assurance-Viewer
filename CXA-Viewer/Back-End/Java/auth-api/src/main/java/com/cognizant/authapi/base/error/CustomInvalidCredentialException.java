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

package com.cognizant.authapi.base.error;

/**
 * Created by 784420 on 7/23/2019 1:17 PM
 */
public class CustomInvalidCredentialException extends RuntimeException {
    public CustomInvalidCredentialException(String credential, String fieldName, String fieldValue) {
        super(String.format("Invalid %s with details %s : %s", credential, fieldName, fieldValue));
    }

    public CustomInvalidCredentialException(String message) {
        super(message);
    }
}
