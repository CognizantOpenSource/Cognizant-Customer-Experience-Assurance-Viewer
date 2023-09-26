/*
 * Copyright (C) 2023 - Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cognizant.report.base.filters;

import com.cognizant.report.base.models.JwtSecurityConstants;
import com.cognizant.report.models.AppTokenStore;
import com.cognizant.report.services.AppTokenStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class CustomTokenFilter extends OncePerRequestFilter {
    @Autowired
    AppTokenStoreService appTokenStoreService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = null;
        String customTokenId = request.getHeader(JwtSecurityConstants.CUSTOM_TOKEN_ID);
        if (StringUtils.hasText(customTokenId)){
            Optional<AppTokenStore> optional = appTokenStoreService.get(customTokenId);
            if (optional.isPresent()){
                bearerToken = String.format(JwtSecurityConstants.BEARER_TEMPLATE, optional.get().getToken());
            }

            //filling custom header with value from auth
            String finalBearerToken = bearerToken;
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if (name.equals(HttpHeaders.AUTHORIZATION) && finalBearerToken !=null)
                        return finalBearerToken;
                    return super.getHeader(name);
                }

            };
            filterChain.doFilter(wrapper, response);
        }else {
            filterChain.doFilter(request, response);
        }

    }
}
