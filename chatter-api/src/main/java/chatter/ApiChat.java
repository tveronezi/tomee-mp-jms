package chatter;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/chat")
@Produces("application/json")
public class ApiChat {

    @Inject
    private ServiceChat chat;

    @GET
    public List<DtoMessage> getMessages() {
        return chat.getMessages();
    }

}
