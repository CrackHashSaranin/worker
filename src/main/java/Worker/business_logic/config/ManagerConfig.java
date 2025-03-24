package Worker.business_logic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "manager")
public class ManagerConfig {
    private String url;

    public ManagerConfig(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
