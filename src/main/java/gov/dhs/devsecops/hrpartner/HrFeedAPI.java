package gov.dhs.devsecops.hrpartner;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Validated
@Api(value = "humanreview", description = "the humanreview API")
public interface HrFeedAPI {
	@ApiOperation(value = "", nickname = "hrPostStixDoc", notes = "", response = String.class, tags = {})
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class) })
	@RequestMapping(value = "/humanreview/stixdoc", produces = { "text/plain" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	default ResponseEntity<String> hrPostStixDoc(@RequestHeader HttpHeaders headers,
			@ApiParam(value = "Post Stix Doc", required = true) @Valid @RequestBody String stixDoc) {
		return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

	}

}
