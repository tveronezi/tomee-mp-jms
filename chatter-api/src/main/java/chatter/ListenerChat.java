package chatter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class ListenerChat {

    @Inject
    private ServiceChat chat;

    public void onMessage(@Observes final DtoMessage message) {
        chat.addMessage(message);
    }

}
