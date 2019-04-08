package chatter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Slf4j
@ApplicationScoped
public class CdiProducerConnection {

    @Produces
    @ApplicationScoped
    @SneakyThrows(JMSException.class)
    public Connection getConnection(ConnectionFactory factory) {
        log.info("Creating the Connection...");
        val connection = factory.createConnection();
        connection.start();
        return connection;
    }

    @SneakyThrows(JMSException.class)
    public void stopServer(@Disposes Connection connection) {
        connection.close();
    }

}
