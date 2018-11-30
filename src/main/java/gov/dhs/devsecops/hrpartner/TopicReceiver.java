package gov.dhs.devsecops.hrpartner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TopicReceiver {
	private static final Logger LOGGER = LoggerFactory.getLogger(TopicReceiver.class);
	private static final String OUTBOUND_TOPIC = "outbound.topic";

	@Bean
	public JmsListenerContainerFactory<?> topicListenerFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setPubSubDomain(true);
		return factory;
	}

	private List<String> jsonDocs = new ArrayList<String>();

	@JmsListener(destination = OUTBOUND_TOPIC, containerFactory = "topicListenerFactory" )
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
