package com.github.klefstad_teaching.cs122b.gateway.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GatewayRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    public Mono<int[]> insertRequests(List<Object> requests) {
        
        return Mono.empty();
    }
}
