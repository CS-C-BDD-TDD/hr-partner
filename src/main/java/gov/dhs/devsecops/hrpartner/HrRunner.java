package gov.dhs.devsecops.hrpartner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class HrRunner implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(HrRunner.class);

	@Override
	public void run(String... args) throws Exception {
	}

	@Autowired
	JmsTemplate jmsTemplate;

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void send(String message) {
		logger.info("Sending to Ingest ... " + message);
		jmsTemplate.convertAndSend("inbound.stix", message);
	}
}
