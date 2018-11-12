package gov.dhs.devsecops.hrpartner;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class HrFeedAPITest {
	private static final Logger logger = LoggerFactory.getLogger(HrFeedAPITest.class);

	private HrRunner mockRunner = Mockito.mock(HrRunner.class);
	private Receiver mockReceiver = Mockito.mock(Receiver.class);
	private HttpHeaders mockHeaders = Mockito.mock(HttpHeaders.class);

	@Test
	public void shouldPostStixDoc() {
		HrFeedImpl hrFeed = new HrFeedImpl();
		hrFeed.setHrRunner(mockRunner);
		hrFeed.setReceiver(mockReceiver);
		ResponseEntity<String> response = hrFeed.hrPostStixDoc(mockHeaders,
				getDocument("/identifying_threat_actor_profile.json"));
		assertNotNull(response);
	}

	@Test
	public void shouldGetStixDoc() {
		HrFeedImpl hrFeed = new HrFeedImpl();
		hrFeed.setHrRunner(mockRunner);
		hrFeed.setReceiver(mockReceiver);
		Mockito.when(mockReceiver.getJsonDoc())
				.thenReturn(getDocument("/identifying_threat_actor_profile.json"));
		ResponseEntity<String> response = hrFeed.hrGetStixDoc(mockHeaders);
		assertNotNull(response);
	}

	@Test
	public void shouldGetNoData() {
		HrFeedImpl hrFeed = new HrFeedImpl();
		hrFeed.setHrRunner(mockRunner);
		hrFeed.setReceiver(mockReceiver);
		Mockito.when(mockReceiver.getJsonDoc()).thenReturn(null);
		ResponseEntity<String> response = hrFeed.hrGetStixDoc(mockHeaders);
		assertNotNull(response);
	}

	private String readFromInputStream(InputStream inputStream) throws IOException {
		StringBuilder resultStringBuilder = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				resultStringBuilder.append(line).append("\n");
			}
		}
		return resultStringBuilder.toString();

	}

	public String getDocument(String fileName) {
		logger.info("fileName:" + fileName);
		Class<HrFeedAPITest> clazz = HrFeedAPITest.class;
		InputStream inputStream = clazz.getResourceAsStream(fileName);

		try {
			return readFromInputStream(inputStream);
		} catch (IOException e) {
			logger.info("Error: " + e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.info("Error: " + e);
				}
			}
		}
		return "";
	}
}
