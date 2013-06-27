package org.sonatype.nexus.plugins.cas.client;

import java.net.URI;

import org.codehaus.plexus.component.annotations.Component;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.jasig.cas.client.validation.TicketValidator;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * CAS REST API client implementation using Spring.
 * @author Fabien Crespel <fabien@crespel.net>
 */
@Component(role = CasRestClient.class, hint = "default")
public class DefaultCasRestClient implements CasRestClient {

	private RestTemplate restTemplate;
	private String casRestTicketUrl;
	private TicketValidator ticketValidator;

	@Override
	public RestTemplate getRestTemplate() {
		if (restTemplate == null) {
			restTemplate = new RestTemplate();
		}
		return restTemplate;
	}

	@Override
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public String getCasRestTicketUrl() {
		return casRestTicketUrl;
	}

	@Override
	public void setCasRestTicketUrl(String casRestTicketUrl) {
		this.casRestTicketUrl = casRestTicketUrl;
	}

	@Override
	public TicketValidator getTicketValidator() {
		return ticketValidator;
	}

	@Override
	public void setTicketValidator(TicketValidator ticketValidator) {
		this.ticketValidator = ticketValidator;
	}

	@Override
	public URI createTicketGrantingTicket(String username, String password) throws RestClientException {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("username", username);
		params.add("password", password);
		return getRestTemplate().postForLocation(getCasRestTicketUrl(), params);
	}

	@Override
	public String grantServiceTicket(URI tgtUri, String service) throws RestClientException {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("service", service);
		return getRestTemplate().postForObject(tgtUri, params, String.class);
	}

	@Override
	public Assertion validateServiceTicket(String serviceTicket, String service) throws TicketValidationException {
		return getTicketValidator().validate(serviceTicket, service);
	}

	@Override
	public void destroyTicketGrantingTicket(URI tgtUri) throws RestClientException {
		getRestTemplate().delete(tgtUri);
	}

}
