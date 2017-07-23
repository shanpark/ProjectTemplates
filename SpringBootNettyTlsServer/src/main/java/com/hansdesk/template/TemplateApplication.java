package com.hansdesk.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application 클래스가 있는 패키지가 루트 패키지가 되며 다른 bean 클래스들은 모두 루트 패키지 안에 포함되어 있어야 자동 검색, 주입이
 * 이루어진다. start.spring.io에서 자동으로 생성해주는 클래스 그대로이다.
 *
 * Created by shanpark on 2017. 7. 21..
 */
@SpringBootApplication
public class TemplateApplication {
	public static void main(String[] args) {
		SpringApplication.run(TemplateApplication.class, args);
	}
}
