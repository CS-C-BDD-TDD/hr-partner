package gov.dhs.devsecops.hrpartner;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.jms.core.JmsTemplate;

public class HrRunnerTest {

	private JmsTemplate mockJmsTemplate = Mockito.mock(JmsTemplate.class);

	@Test
	public void shouldSendMsg() {
		HrRunner runner = new HrRunner();
		runner.setJmsTemplate(mockJmsTemplate);
		runner.send("Test");
	}
}
