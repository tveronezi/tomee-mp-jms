package chatter.it;

import com.mashape.unirest.http.Unirest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.jms.Session;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class CucumberTest {

    private static final String QUEUE_NAME = "myQueue";
    private static final Duration TIMEOUT = Duration.ofSeconds(120);
    private static DockerComposeContainer stack;

    @BeforeAll
    @SneakyThrows(IOException.class)
    public static void init() {
        val dcFile = File.createTempFile("docker-compose", ".yaml");
        FileUtils.copyInputStreamToFile(CucumberTest.class.getResourceAsStream("/dc-tests.yaml"), dcFile);
        stack = new DockerComposeContainer(dcFile)
                .withPull(false)
                .withExposedService(
                        "chatter_1",
                        8080,
                        Wait.forListeningPort().withStartupTimeout(TIMEOUT)
                )
                .withExposedService(
                        "amq_1",
                        61616,
                        Wait.forListeningPort().withStartupTimeout(TIMEOUT)
                )
                .withLogConsumer("chatter_1", new org.testcontainers.containers.output.Slf4jLogConsumer(log))
                .withLogConsumer("amq_1", new org.testcontainers.containers.output.Slf4jLogConsumer(log));
        log.info("Starting the stack...");
        stack.start();
    }

    @AfterAll
    public static void shutdown() {
        log.info("Stopping the stack...");
        stack.stop();
    }

    @Test
    @SneakyThrows
    public void test() {
        val amqUrl = "tcp://localhost:" + stack.getServicePort("amq_1", 61616);
        val apiUrl = "http://localhost:" + stack.getServicePort("chatter_1", 8080);
        val connectionFactory = ActiveMQJMSClient.createConnectionFactory(amqUrl, null);
        try (val connection = connectionFactory.createConnection();
             val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             val producer = session.createProducer(session.createQueue(QUEUE_NAME))) {
            val msg = session.createMessage();
            msg.setStringProperty("content", "a");
            producer.send(msg);
        }
        for (int i = 0; i < 5; i++) {
            val request = Unirest.get(URI.create(apiUrl).resolve("/api/chat").toASCIIString());
            val reqResult = request.asJson();
            log.info("reqResult = {}", new java.util.Scanner(reqResult.getRawBody()).useDelimiter("\\A").next());
            val actual = StreamSupport.stream(reqResult.getBody().getArray().spliterator(), false)
                    .map(o -> ((JSONObject) o).getString("content")).collect(Collectors.toList());
            log.info("val actual = {}", actual);
            if (Collections.singletonList("a").toString().equals(actual.toString())) {
                return;
            }
            TimeUnit.SECONDS.sleep(1);
        }
        Assertions.fail("Could not read the message we were expecting");
    }

}
