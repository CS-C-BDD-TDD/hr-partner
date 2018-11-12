package gov.dhs.devsecops.hrpartner;

import java.util.UUID;

import javax.validation.Valid;

import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@RequestMapping("${gov.dhs.nppd.base-path:/api/v1}")
public class HrFeedImpl implements HrFeedAPI {
	private static final Logger logger = LoggerFactory.getLogger(HrFeedImpl.class);

	@Autowired
	private HrRunner hrRunner;

	@Autowired
	private Receiver receiver;

	public void setHrRunner(HrRunner hrRunner) {
		this.hrRunner = hrRunner;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	@Override
	@ApiOperation(value = "", nickname = "hrPostStixDoc", notes = "", response = String.class, tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class) })
	@RequestMapping(value = "/humanreview/stixdoc", produces = { "text/plain" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	public ResponseEntity<String> hrPostStixDoc(@RequestHeader HttpHeaders headers,
			@ApiParam(value = "Post Stix Doc", required = true) @Valid @RequestBody String stixDoc) {
		logger.info("GOT DATA ..." + stixDoc);

		JSONObject jsonDoc = new JSONObject(stixDoc);
		String newId = String.format("%s--%s", jsonDoc.get("type").toString(), UUID.randomUUID().toString());

		logger.info("oldId: " + jsonDoc.getString("id"));
		logger.info("newId: " + newId);

		jsonDoc.put("id", newId);
		hrRunner.send(jsonDoc.toString(2));

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-type", "text/plain");

		logger.info("Returning ...");
		String returnMessage = newId;
		return ResponseEntity.status(HttpStatus.OK_200).headers(responseHeaders).body(returnMessage);
	}

	@ApiOperation(value = "", nickname = "hrGetStixDoc", notes = "", response = String.class, tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class) })
	@RequestMapping(value = "/humanreview/stixdoc", produces = { "text/plain" }, method = RequestMethod.GET)
	public ResponseEntity<String> hrGetStixDoc(@RequestHeader HttpHeaders headers) {
		String stixDoc = receiver.getJsonDoc();

		if (stixDoc == null) {
			stixDoc = "*** NO DATA ****";
		} else {
			JSONObject jsonDoc = new JSONObject(stixDoc);
			stixDoc = jsonDoc.toString(2);
		}

		logger.info("Returning ... '" + stixDoc + "'");

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-type", "text/plain");
		return ResponseEntity.status(HttpStatus.OK_200).headers(responseHeaders).body(stixDoc);
	}
}
