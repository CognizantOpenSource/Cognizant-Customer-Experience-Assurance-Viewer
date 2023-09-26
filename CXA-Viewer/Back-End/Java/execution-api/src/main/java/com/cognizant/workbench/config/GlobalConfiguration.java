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

package com.cognizant.workbench.config;

import com.cognizant.workbench.clients.BitBucketClient;
import com.cognizant.workbench.clients.JenkinsClient;
import com.cognizant.workbench.user.service.ReportPortalClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * Created by 784420 on 7/12/2019 7:34 PM
 */
@Configuration
@Slf4j
@EnableAsync
public class GlobalConfiguration {
    private static InetSocketAddress address;
    @Value("${app.jenkins.client.url}")
    private String jenkinsDefaultUrl;
    @Value("${app.gitlab.client.url}")
    private String gitlabDefaultUrl;
    @Value("${app.bitBucket.client.url}")
    private String bitBucketDefaultUrl;
    @Value("${app.uri.get.proxy}")
    private String proxyURI;

    @Autowired
    HttpProxySelectorBuilder httpProxySelectorBuilder;
    @PostConstruct
    private void init() {
        address = getProxy(URI.create(proxyURI));
    }

    @Bean
    public ReportPortalClient getReportPortalClient() {
        return getFeignBuilder()
                .logger(new Slf4jLogger(ReportPortalClient.class))
                .logLevel(Logger.Level.FULL)
                .target(ReportPortalClient.class, "https://localhost");
    }

    @Bean
    public JenkinsClient getJenkinsFeignClient() {
        return getFeignBuilder()
                .logger(new Slf4jLogger(JenkinsClient.class))
                .logLevel(Logger.Level.FULL)
                .target(JenkinsClient.class, jenkinsDefaultUrl);
    }

    @Bean
    public BitBucketClient getBitBucketFeignClient() {
        return getFeignBuilder()
                .encoder(new FormEncoder(new GsonEncoder()))
                .logger(new Slf4jLogger(BitBucketClient.class))
                .logLevel(Logger.Level.FULL)
                .target(BitBucketClient.class, bitBucketDefaultUrl);
    }


    private Feign.Builder getFeignBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (address != null) {
            builder.proxySelector(httpProxySelectorBuilder.buildFor(new Proxy(Proxy.Type.HTTP, address)));
        }

        return Feign.builder()
                .client(new feign.okhttp.OkHttpClient(builder.build()))
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .options(new Request.Options(20, TimeUnit.SECONDS, 20, TimeUnit.SECONDS, true));
    }

    @Bean
    public HttpTransport getHttpTransport() throws GeneralSecurityException {
        HttpTransport httpTransport ;
        if (address != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
            httpTransport = new NetHttpTransport.Builder()
                    .doNotValidateCertificate()
                    .setProxy(proxy).build();
        } else {
            httpTransport = new NetHttpTransport();
        }
        return httpTransport;
    }

    private InetSocketAddress getProxy(URI uri) {
        System.setProperty("java.net.useSystemProxies", "true");
        Proxy proxy = ProxySelector.getDefault().select(uri).iterator().next();
        log.info("proxy type : " + proxy.type());
        InetSocketAddress inetSocketAddress = (InetSocketAddress) proxy.address();

        if (inetSocketAddress == null) {
            log.info("No Proxy");
        } else {
            log.info("proxy hostname : " + inetSocketAddress.getHostName());
            log.info("proxy port : " + inetSocketAddress.getPort());
        }
        return inetSocketAddress;
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @Primary
    @Scope(SCOPE_PROTOTYPE)
    Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new FormEncoder(new SpringEncoder(messageConverters));
    }
}
