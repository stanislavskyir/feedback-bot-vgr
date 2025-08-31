package dev.stanislavskyi.feedback_bot_vgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("dev.stanislavskyi.feedback_bot_vgr.telegram.config")
public class FeedbackBotVgrApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedbackBotVgrApplication.class, args);
	}

}
