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

package com.cognizant.workbench.base.services;

import org.springframework.util.Assert;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class WhiteListValidator implements ConstraintValidator<WhiteListed, Object> {

    private WhiteListed it;

    @Override
    public void initialize(final WhiteListed constraintAnnotation) {
        this.it = constraintAnnotation;
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        WhiteListService whiteListService = BeanUtil.getBean(WhiteListService.class);
        Assert.notNull(whiteListService, "whiteListService must not be null");
        String type = it.type().isEmpty() ? it.value() : it.type();
        boolean isValid = whiteListService.isValid(type, String.valueOf(value));
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format(it.message(),type , value)).addConstraintViolation();
        }
        return isValid;
    }

}
