package rhizome.core.net.protocol;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    public static final String MESSAGE_TYPES = "messageTypes";

    @Serialize(order = 1) MessageCodes messageType;
    @Serialize(order = 2) Object data;

    private Message(MessageCodes messageType, Object data) {
		this.messageType = messageType;
		this.data = data;
	}

    public static Message of(@Deserialize("messageType") MessageCodes messageType, @Deserialize("data") Object data) {
		return new Message(messageType, data);
	}
}
