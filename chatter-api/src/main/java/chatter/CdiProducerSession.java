package chatter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

@Slf4j
@ApplicationScoped
public class CdiProducerSession {

    @Produces
    @ApplicationScoped
    @SneakyThrows(JMSException.class)
    public Session getSession(Connection connection) {
        log.info("Creating the Session...");
        return connection.createSession();
    }

    @SneakyThrows(JMSException.class)
    public void stopServer(@Disposes Session session) {
        session.close();
    }

}
