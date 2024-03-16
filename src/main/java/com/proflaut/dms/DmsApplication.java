package com.proflaut.dms;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.proflaut.dms.configuration.TwilioConfig;
import com.twilio.Twilio;

@SpringBootApplication
@ComponentScan({ "com.proflaut.*" })
@EntityScan("com.proflaut.*")
@EnableJpaRepositories("com.proflaut.*")
@EnableScheduling
public class DmsApplication {

	@Autowired
	private TwilioConfig twilioConfig;
	
	

	@PostConstruct
	public void initTwilio() {
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
	}

	public static void main(String[] args) {
		SpringApplication.run(DmsApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
