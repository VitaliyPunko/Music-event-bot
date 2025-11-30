package vpunko.musiceventbot.bot.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static vpunko.musiceventbot.bot.constant.MetricsConstant.APPLICATION_VERSION;


@Configuration
public class MetricsConfig {

    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry);
    }

    @Bean
    public Gauge appInfoGauge(MeterRegistry registry, BuildProperties buildProperties) {
        return Gauge.builder(APPLICATION_VERSION, () -> 1)
                .description("Application version info")
                .tag("version", buildProperties.getVersion())
                .tag("name", buildProperties.getName())
                .register(registry);
    }

}
