package io.github.bhhan.example.crawler;

import io.github.bhhan.example.domain.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DomainCrawlerService {
    private final KafkaTemplate<String, Domain> kafkaTemplate;
    private final String TOPIC = "web-domains";

    public void crawl(String name) {
        Mono<DomainList> domainListMono = WebClient.create()
                .get()
                .uri("https://api.domainsdb.info/v1/domains/search?domain=" + name)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DomainList.class);

        domainListMono
                .subscribe(domainList -> {
                    domainList.getDomains()
                            .forEach(domain -> {
                                kafkaTemplate.send(TOPIC, domain);
                                log.info("Domain Message: " + domain.getDomain());
                            });
                });
    }
}
