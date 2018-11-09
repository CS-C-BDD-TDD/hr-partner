package gov.dhs.devsecops.hrpartner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class HrPartnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrPartnerApplication.class, args);
	}
}
