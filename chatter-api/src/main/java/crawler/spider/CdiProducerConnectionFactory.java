package crawler.spider;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;

@Slf4j
@ApplicationScoped
public class CdiProducerConnectionFactory {

    @Produces
    @ApplicationScoped
    @SneakyThrows(Exception.class)
    public ConnectionFactory getConnectionFactory(@ConfigProperty(name = "SPIDER_AMQ_PATH") String amqPath) {
        log.info("Creating the ConnectionFactory...");
        return ActiveMQJMSClient.createConnectionFactory(amqPath, null);
    }

}
