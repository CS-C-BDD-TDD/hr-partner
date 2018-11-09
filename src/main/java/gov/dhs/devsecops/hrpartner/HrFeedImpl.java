package gov.dhs.devsecops.hrpartner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.eclipse.jetty.http.HttpStatus;
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

	@Override
	@ApiOperation(value = "", nickname = "hrPostStixDoc", notes = "", response = String.class, tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class) })
	@RequestMapping(value = "/humanreview/stixdoc", produces = { "text/plain" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	public ResponseEntity<String> hrPostStixDoc(@RequestHeader HttpHeaders headers,
			@ApiParam(value = "Post Stix Doc", required = true) @Valid @RequestBody String stixDoc) {
		logger.info("GOT DATA ..." + stixDoc);

		logger.info("Replacing guid with new UUID");
		StringBuilder sb = new StringBuilder(stixDoc);
		int index = sb.indexOf("\"id\"");
		int beg = sb.indexOf("--", index) + 2;
		int end = sb.indexOf("\"", beg);
		sb.replace(beg, end, UUID.randomUUID().toString());
		hrRunner.send(sb.toString());

		HttpHeaders responseHeaders = new HttpHeaders();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		responseHeaders.add("Content-type", "text/plain");

		logger.info("Returning ...");
		return ResponseEntity.status(HttpStatus.OK_200).headers(responseHeaders).body("YOOHOO!");
	}
}
