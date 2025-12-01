package vpunko.musiceventbot;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

public class TestKafkaConsumersUtils {

    /**
     * Create a test Kafka consumer for a given value class and subscribe it to topic.
     */
    public static <T> Consumer<String, T> create(
            EmbeddedKafkaBroker embeddedKafka,
            String topic,
            Class<T> valueClass
    ) {
        // base config from Spring Kafka
        Map<String, Object> props = KafkaTestUtils.consumerProps(
                "producer-test-group",
                "true",
                embeddedKafka
        );

        // add JSON deserializer config
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueClass.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        ConsumerFactory<String, T> cf = new DefaultKafkaConsumerFactory<>(props);
        Consumer<String, T> consumer = cf.createConsumer();

        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, true, topic);

        return consumer;
    }
}
