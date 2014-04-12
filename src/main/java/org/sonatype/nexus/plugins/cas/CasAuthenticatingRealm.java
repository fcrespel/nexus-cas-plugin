package org.sonatype.nexus.plugins.cas;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.sisu.Description;
import org.sonatype.nexus.plugins.cas.client.CasRestClient;
import org.sonatype.nexus.plugins.cas.config.CasPluginConfiguration;
import org.sonatype.nexus.plugins.cas.config.model.v1_0_0.Configuration;

/**
 * CAS Authentication Realm using CAS REST API.
 * @author Fabien Crespel <fabien@crespel.net>
 */
@Singleton
@Named(CasAuthenticatingRealm.ROLE)
@Description("CAS Authentication Realm")
public class CasAuthenticatingRealm extends CasRealm implements Initializable {

	public static final String ROLE = "CasAuthenticatingRealm";
	private static final Logger log = LoggerFactory.getLogger(CasAuthenticatingRealm.class);

	private final CasPluginConfiguration casPluginConfiguration;
	private final CasRestClient casRestClient;

	private boolean isConfigured = false;

	@Inject
	public CasAuthenticatingRealm(final CasPluginConfiguration casPluginConfiguration, final CasRestClient casRestClient) {
		super();
		this.casPluginConfiguration = casPluginConfiguration;
		this.casRestClient = casRestClient;
		setAuthenticationTokenClass(UsernamePasswordToken.class);
		setCredentialsMatcher(new AllowAllCredentialsMatcher());
	}
	
	@Override
	public String getName() {
		return ROLE;
	}

	@Override
	public void initialize() throws InitializationException {
		configure(casPluginConfiguration.getConfiguration());
	}

	protected void configure(Configuration config) {
		if (config != null) {
			setCasServerUrlPrefix(config.getCasServerUrl());
			setCasService(config.getCasService());
			setValidationProtocol(config.getValidationProtocol());
			setRoleAttributeNames(config.getRoleAttributeNames());
			casRestClient.setCasRestTicketUrl(config.getCasRestTicketUrl());
			casRestClient.setTicketValidator(ensureTicketValidator());
			casRestClient.setConnectTimeout(config.getConnectTimeout());
			casRestClient.setReadTimeout(config.getReadTimeout());
			log.info("CAS plugin configured for use with server " + config.getCasServerUrl());
			isConfigured = true;
		}
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		if (!isConfigured || casRestClient == null || token == null) {
			return null;
		}

		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		URI tgt = null;
		try {
			log.debug("Authenticating user '" + upToken.getUsername() + "' ...");
			tgt = casRestClient.createTicketGrantingTicket(upToken.getUsername(), new String(upToken.getPassword()));
			String st = casRestClient.grantServiceTicket(tgt, getCasService());
			Assertion assertion = casRestClient.validateServiceTicket(st, getCasService());

			return createAuthenticationInfo(st, assertion);

		} catch (TicketValidationException e) {
			log.warn("Error validating remote CAS REST Ticket for user '" + upToken.getUsername() + "'", e);
			throw new AuthenticationException(e);

		} catch (Exception e) {
			log.error("Error calling remote CAS REST Ticket API for user '" + upToken.getUsername() + "'", e);
			throw new AuthenticationException(e);

		} finally {
			if (tgt != null) {
				try {
					casRestClient.destroyTicketGrantingTicket(tgt);
				} catch (Throwable e) {
					// Ignored
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (!isConfigured || principals.fromRealm(getName()).size() == 0) {
			return null;
		} else {
			SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
			Map<String, Object> attributes = (Map<String, Object>) principals.asList().get(1);

			simpleAuthorizationInfo.addRoles(split(getDefaultRoles()));
			for (String attributeName : split(getRoleAttributeNames())) {
				simpleAuthorizationInfo.addRoles(collectAttributeValues(attributes, attributeName));
			}

			simpleAuthorizationInfo.addStringPermissions(split(getDefaultPermissions()));
			for (String attributeName : split(getPermissionAttributeNames())) {
				simpleAuthorizationInfo.addStringPermissions(collectAttributeValues(attributes, attributeName));
			}

			return simpleAuthorizationInfo;
		}
	}

	protected AuthenticationInfo createAuthenticationInfo(String serviceTicket, Assertion assertion) {
		List<Object> principals = CollectionUtils.asList(assertion.getPrincipal().getName(), assertion.getPrincipal().getAttributes());
		PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, getName());
		return new SimpleAuthenticationInfo(principalCollection, serviceTicket);
	}

	@SuppressWarnings("unchecked")
	protected List<String> split(String s) {
		if (s != null) {
			return Arrays.asList(StringUtils.split(s, ","));
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@SuppressWarnings("rawtypes")
	protected List<String> collectAttributeValues(Map<String, Object> attributes, String attributeName) {
		List<String> values = new ArrayList<String>();
		Object attribute = attributes.get(attributeName);
		if (attribute instanceof String) {
			values.add((String) attribute);
		} else if (attribute instanceof Collection) {
			for (Object value : ((Collection) attribute)) {
				if (value instanceof String) {
					values.add((String) value);
				}
			}
		}
		return values;
	}

}
