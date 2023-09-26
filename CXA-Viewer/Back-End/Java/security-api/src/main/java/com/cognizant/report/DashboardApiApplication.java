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

package com.cognizant.report;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Arrays;

@SpringBootApplication
@EnableMongoAuditing
@EnableRetry
public class DashboardApiApplication {

    public static void main(String[] args) {
         new SpringApplicationBuilder(DashboardApiApplication.class)
                .properties("spring.config.name:securityAPI").run(args);
    }

    @Configuration
    public class SpringDocOpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .components(new Components().addSecuritySchemes("bearer-jwt",
                            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                                    .in(SecurityScheme.In.HEADER).name("Authorization")))
                    .info(getInfo())
                    .addSecurityItem(
                            new SecurityRequirement().addList("bearer-jwt", Arrays.asList("read", "write")));
        }

        private Info getInfo() {
            return new Info()
                    .title("DashBoard REST API")
                    .description("DashBoard API REST calls using Spring boot")
                    .version("1.0")
                    .termsOfService("Terms of service")
                    .contact(new Contact().name("cxa").email("cognizantCxa@cognizant.com").url("www.Cognizant.com"))
                    .license(new License().name("License of API").url("API license URL"));
        }

    }

}
