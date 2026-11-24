package com.kaustack.auth.config;

import com.kaustack.auth.model.Gender;
import com.kaustack.auth.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tags;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableScheduling
class AccountMetrics {
    private final UserRepository users;
    private final MultiGauge accounts;

    AccountMetrics(UserRepository users, MeterRegistry registry) {
        this.users = users;
        this.accounts = MultiGauge.builder("auth.accounts")
                .description("Current registered accounts by recorded gender")
                .register(registry);
    }

    @Scheduled(fixedDelay = 60_000)
    void refresh() {
        var counts = new EnumMap<Gender, Long>(Gender.class);
        try {
            for (var row : users.countAccountsByGender()) {
                Gender gender = row.getGender() == null ? Gender.UNKNOWN : row.getGender();
                counts.merge(gender, row.getAccountCount(), Long::sum);
            }
        } catch (DataAccessException exception) {
            accounts.register(List.of(), true);
            throw exception;
        }

        accounts.register(Arrays.stream(Gender.values())
                .map(gender -> MultiGauge.Row.of(Tags.of("gender", gender.name()), counts.getOrDefault(gender, 0L)))
                .toList(), true);
    }
}
