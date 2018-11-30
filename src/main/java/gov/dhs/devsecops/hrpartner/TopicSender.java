package gov.dhs.devsecops.hrpartner;

import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class TopicSender implements CommandLineRunner {
	private static final String INBOUND_TOPIC = "inbound.topic";
	private Logger logger = LoggerFactory.getLogger(TopicSender.class);

	@Override
	public void run(String... args) throws Exception {
	}

	@Autowired
	JmsTemplate jmsTemplate;

	public void send(String message) {
		logger.info("Sending to Ingest ... " + message);
		jmsTemplate.convertAndSend(new ActiveMQTopic(INBOUND_TOPIC), message);
	}
}
