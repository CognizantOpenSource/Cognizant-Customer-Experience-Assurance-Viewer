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

package com.cognizant.report.base.services;

import com.cognizant.report.base.models.Account;
import com.cognizant.report.base.models.JwtSecurityConstants;
import com.cognizant.report.base.models.Permission;
import com.cognizant.report.base.models.User;
import com.cognizant.report.models.AppTokenStore;
import com.cognizant.report.services.AppTokenStoreService;
import com.cognizant.report.services.ProjectMappingService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 784420 on 7/18/2019 7:04 PM
 */
@Component
@Slf4j
public class JwtTokenService {

    @Value("${app.jwt.token.secret.key}")
    private String appSecretKey;
    @Value("${app.jwt.token.expiration.milliSec}")
    private long appExpirationInMs;
    private long oneDayMilliSec = 24 * 60 * 60 * 1000L;

    private String secretKey;

    @Autowired
    AppTokenStoreService appTokenStoreService;
    @Autowired
    ProjectMappingService projectMappingService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(appSecretKey.getBytes());
    }

    public Map<String, Object> generateToken(User user) {
        return generateToken(user, 0);
    }

    public Map<String, Object> generateToken(User user, int days) {
        log.info("Generating token for {}", user.getEmail());
        Date now = new Date();
        Date validity;
        if (days >= 1) {
            validity = new Date(now.getTime() + (days * oneDayMilliSec));
        } else {
            validity = new Date(now.getTime() + appExpirationInMs);
        }
        String permissions = user.getAccount().getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getId).collect(Collectors.joining(","));
        Claims claims = Jwts.claims().setSubject(user.getId());
        Object jwtToken = Jwts.builder()
                .setClaims(claims)
                .claim("permissions", permissions)
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put(JwtSecurityConstants.AUTH_TOKEN, jwtToken);
        result.put(JwtSecurityConstants.EXPIRES_AT, validity);
        return result;
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtSecurityConstants.HEADER_STRING);
        if (bearerToken != null && bearerToken.startsWith(JwtSecurityConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUserIdFromJWT(String token) {
        Claims claims = getClaims(token);

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        if (claims.getBody().getExpiration().before(new Date())) {
            return false;
        }
        log.info("Verified token for subject " + claims.getBody().getSubject());
        log.info("permissions: " + claims.getBody().get("permissions"));
        return true;
    }

    public User getUser(String token) {
        Claims claims = getClaims(token);
        User user = new User();
        user.setId(claims.getSubject());
        user.setFirstName((String) claims.get("firstName"));
        user.setLastName((String) claims.get("lastName"));
        user.setEmail((String) claims.get("email"));
        user.setActive((boolean) claims.get("active"));
        Account account = new Account();

        List<String> userOwnProjectIds = projectMappingService.getUserOwnProjectIds(claims.getSubject());
        List<String> userProjectIds = projectMappingService.getUserProjectIds(claims.getSubject());

        account.setOwnProjectIds(userOwnProjectIds);
        account.setProjectIds(userProjectIds);

        user.setAccount(account);
        return user;
    }

    public List<String> getPermissions(String token) {
        Claims claims = getClaims(token);
        String permissionsString = (String) claims.get("permissions");
        if (StringUtils.isEmpty(permissionsString)) return new ArrayList<>();
        return Arrays.asList(permissionsString.split(","));
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getTokenFromDB(String uuidToken){
        Optional<AppTokenStore> optional = appTokenStoreService.get(uuidToken);
        if (optional.isPresent()) {
            return optional.get().getToken();
        }
        return "";
    }
}
