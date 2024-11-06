//package com.oralie.search.config;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.oralie.search.repository")
//@ComponentScan(basePackages = {"com.oralie.search.service"})
//public class ElasticsearchConfig extends ElasticsearchConfiguration {
//
//    @Value("${elasticsearch.url}")
//    private String url;
//
//    @Value("${elasticsearch.username}")
//    private String username;
//
//    @Value("${elasticsearch.password}")
//    private String password;
//
//    @Override
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo(url)
//                .withBasicAuth(username, password)
//                .build();
//    }
//}