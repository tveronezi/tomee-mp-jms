package crawler.spider;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

@Slf4j
@ApplicationScoped
public class CdiProducerMessageConsumer {

    private static final String QUEUE_NAME = "myQueue";

    @Produces
    @ApplicationScoped
    @SneakyThrows(JMSException.class)
    public MessageConsumer getMessageConsumer(Session session) {
        log.info("Creating the MessageConsumer...");
        return session.createConsumer(session.createQueue(QUEUE_NAME));
    }

    @SneakyThrows(JMSException.class)
    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object startup, Event<DtoMessage> event, MessageConsumer consumer) {
        log.info("Creating the MessageListener...");
        consumer.setMessageListener(message -> {
            try {
                val content = message.getStringProperty("content");
                log.info("New message: {}", content);
                val dto = new DtoMessage();
                dto.setContent(content);
                event.fire(dto);
                message.acknowledge();
            } catch (Exception e) {
                log.error("Error processing message", e);
            }
        });
    }

    @SneakyThrows(JMSException.class)
    void stopServer(@Disposes MessageConsumer consumer) {
        consumer.close();
    }

}
