package ru.yandex.practicum.intershop.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.api.DefaultApi;
import ru.yandex.practicum.intershop.client.domain.Balance;
import ru.yandex.practicum.intershop.exceptions.PaymentServiceNotAvailableException;

import java.math.BigDecimal;

@Service
public class ClientService {
    @Value("${spring.security.oauth2.client.registration.intershop.client-id:intershop}")
    private String keyloakClientId;

    private ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    public ClientService(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public Mono<BigDecimal> getBalance() {
        return getDefaultApi().flatMap(defaultApi->defaultApi
                .balanceGet()
                .map(Balance::getAmount)
                .onErrorMap(error -> new PaymentServiceNotAvailableException()));
    }

    public Mono<BigDecimal> buyItems(BigDecimal price) {
        return getDefaultApi().flatMap(defaultApi -> defaultApi
                .buyPostWithResponseSpec(price)
                .bodyToMono(Balance.class)
                .map(Balance::getAmount));
    }

    private Mono<DefaultApi> getDefaultApi() {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId(keyloakClientId)
                .principal("system")
                .build();
        Mono<String> token = authorizedClientManager.authorize(request).map(OAuth2AuthorizedClient::getAccessToken).map(OAuth2AccessToken::getTokenValue);

        return token.map(t-> {
            ApiClient apiClient = new ApiClient();
            apiClient.setBearerToken(t);
            return new DefaultApi(apiClient);
        });
    }
}
