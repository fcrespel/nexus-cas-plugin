package org.sonatype.nexus.plugins.cas.client;

import java.net.URI;

import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * CAS REST API client interface.
 * @author Fabien Crespel <fabien@crespel.net>
 */
public interface CasRestClient {

	public RestTemplate getRestTemplate();
	public void setRestTemplate(RestTemplate restTemplate);
	
	public void setConnectTimeout(int connectTimeout);
	public void setReadTimeout(int readTimeout);

	public String getCasRestTicketUrl();
	public void setCasRestTicketUrl(String casRestTicketUrl);

	public TicketValidator getTicketValidator();
	public void setTicketValidator(TicketValidator ticketValidator);

	public URI createTicketGrantingTicket(String username, String password) throws RestClientException;
	public String grantServiceTicket(URI tgtUri, String service) throws RestClientException;
	public Assertion validateServiceTicket(String serviceTicket, String service) throws TicketValidationException;
	public void destroyTicketGrantingTicket(URI tgtUri) throws RestClientException;

}
