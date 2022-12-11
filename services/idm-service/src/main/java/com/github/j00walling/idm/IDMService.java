package com.github.j00walling.idm;

import com.github.j00walling.idm.config.IDMServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    IDMServiceConfig.class
})
public class IDMService
{
    public static void main(String[] args)
    {
        SpringApplication.run(IDMService.class, args);
    }
}
