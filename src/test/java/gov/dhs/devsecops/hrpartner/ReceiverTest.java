package gov.dhs.devsecops.hrpartner;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

public class ReceiverTest {
	@Test
	public void shouldReceiveData() {
		Receiver receiver = new Receiver();
		try {
			receiver.receiveMessage(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void shouldGetJsonDoc() {
		Receiver receiver = new Receiver();
		try {
			receiver.receiveMessage("somedoc");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String doc = receiver.getJsonDoc();
		assertNotNull(doc);
	}
}
