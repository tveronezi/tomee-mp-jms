package crawler.spider;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ServiceChat {

    private final List<DtoMessage> messages = Collections.synchronizedList(new ArrayList<>());

    public void addMessage(DtoMessage message) {
        this.messages.add(message);
    }

    public List<DtoMessage> getMessages() {
        synchronized (this.messages) {
            return new ArrayList<>(this.messages);
        }
    }

}
