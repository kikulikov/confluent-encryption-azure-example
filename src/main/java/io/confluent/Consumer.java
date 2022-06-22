package io.confluent;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting the consumer...");
        final Properties properties = readProperties(args);
        final String topic = properties.getProperty("consumer.topic");

        final KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Collections.singleton(topic), rebalanceListener(kafkaConsumer));

        final ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(30));
        consumerRecords.forEach((record) -> LOGGER.info("Consuming {}:{}", record.key(), record.value()));
    }

    private static Properties readProperties(String[] args) throws IOException {
        final String filename = args.length > 0 ? args[0] : "consumer.properties";

        LOGGER.info("Reading the file {}", filename);

        final Properties properties = new Properties();
        properties.load(new FileReader(filename));
        return properties;
    }

    private static ConsumerRebalanceListener rebalanceListener(KafkaConsumer<String, String> kafkaConsumer) {
        return new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                // Seeking to the beginning just in case there is an offset committed
                // This is not required, I added it just in case of for testing purpose
                kafkaConsumer.seekToBeginning(collection);
            }
        };
    }
}
