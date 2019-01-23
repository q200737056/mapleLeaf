package com.mapleLeaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MapleLeafBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(MapleLeafBootApplication.class, args);
    }
}
