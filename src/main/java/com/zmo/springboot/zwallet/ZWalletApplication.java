package com.zmo.springboot.zwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZWalletApplication.class, args);
    }

}
