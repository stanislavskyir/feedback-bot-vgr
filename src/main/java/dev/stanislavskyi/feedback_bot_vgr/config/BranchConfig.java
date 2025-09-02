package dev.stanislavskyi.feedback_bot_vgr.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class BranchConfig {
    private Map<String, String> branches;
}
