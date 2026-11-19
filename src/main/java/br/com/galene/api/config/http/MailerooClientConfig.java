package br.com.galene.api.config.http;

import br.com.galene.api.domain.email.configuration.MailerooConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
public class MailerooClientConfig {

    public static final String API_KEY_HEADER = "X-Api-Key";

    @Bean
    public RestClient mailerooRestClient(MailerooConfig config) {
        return builder(config).build();
    }

    public static RestClient.Builder builder(MailerooConfig config) {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(config.getConnectTimeout())
                .build();

        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(config.getReadTimeout());

        return RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .requestFactory(factory)
                .defaultHeader(API_KEY_HEADER, config.getApiKey());
    }
}
