package io.confluent;

import io.confluent.model.avro.OnlineOrder;
import net.datafaker.Faker;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private static final Faker FAKER = new Faker();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Starting the producer...");
        final Properties properties = readProperties(args);

        final String key = UUID.randomUUID().toString();

        final String customerId = FAKER.harryPotter().character().replaceAll("[\\s]+", "-");
        final String orderId = FAKER.harryPotter().spell().replaceAll("[\\s]+", "-");
        final int number = FAKER.number().numberBetween(1, 100);

        final OnlineOrder value = new OnlineOrder(customerId, orderId, number);
        LOGGER.info("Producing {}:{}", key, value);

        final KafkaProducer<String, OnlineOrder> kafkaProducer = new KafkaProducer<>(properties);
        final String topic = properties.getProperty("producer.topic");

        final RecordMetadata metadata = kafkaProducer.send(new ProducerRecord<>(topic, key, value)).get();
        LOGGER.info(metadata.toString());

        kafkaProducer.close();
    }

    private static Properties readProperties(String[] args) throws IOException {
        final String filename = args.length > 0 ? args[0] : "producer.properties";

        LOGGER.info("Reading the file {}", filename);

        final Properties properties = new Properties();
        properties.load(new FileReader(filename));
        return properties;
    }
}
