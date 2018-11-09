package gov.dhs.devsecops.hrpartner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

	private List<String> jsonDocs = new ArrayList<String>();

	@JmsListener(destination = "outbound.stix")
	public void receiveMessage(@Payload String jsonDoc) throws IOException {
		LOGGER.info("********Received: " + jsonDoc);
		synchronized (jsonDocs) {
			jsonDocs.add(jsonDoc);
		}
	}

	public String getJsonDoc() {
		String jsonDoc = null;
		synchronized (jsonDocs) {
			if (!jsonDocs.isEmpty()) {
				jsonDoc = jsonDocs.remove(0);
			}
		}
		return jsonDoc;
	}
}
