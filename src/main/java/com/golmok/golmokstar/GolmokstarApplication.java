package com.golmok.golmokstar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.golmok.golmokstar")  //패키지 스캔 추가
public class GolmokstarApplication {
	public static void main(String[] args) {
		SpringApplication.run(GolmokstarApplication.class, args);
	}
}
