package org.sonatype.nexus.plugins.cas;

import java.util.Set;
import java.util.TreeSet;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.security.usermanagement.AbstractReadOnlyUserManager;
import org.sonatype.security.usermanagement.DefaultUser;
import org.sonatype.security.usermanagement.User;
import org.sonatype.security.usermanagement.UserNotFoundException;
import org.sonatype.security.usermanagement.UserSearchCriteria;
import org.sonatype.security.usermanagement.UserStatus;

/**
 * CAS User Manager that accepts all users.
 * @author Fabien Crespel <fabien@crespel.net>
 */
@Singleton
@Named(CasUserManager.SOURCE_NAME)
public class CasUserManager extends AbstractReadOnlyUserManager {
	
	public static final String SOURCE_NAME = "CAS";

	@Override
	public String getSource() {
		return SOURCE_NAME;
	}

	@Override
	public String getAuthenticationRealmName() {
		return CasAuthenticatingRealm.ROLE;
	}

	@Override
	public Set<User> listUsers() {
		return null;
	}

	@Override
	public Set<String> listUserIds() {
		return null;
	}

	@Override
	public Set<User> searchUsers(UserSearchCriteria criteria) {
		Set<User> users = new TreeSet<User>();
		if (criteria.getUserId() != null && criteria.getUserId().length() != 0) {
			users.add(buildFakeUser(criteria.getUserId()));
		}
		return users;
	}

	@Override
	public User getUser(String userId) throws UserNotFoundException {
		return buildFakeUser(userId);
	}
	
	protected User buildFakeUser(String userId) {
		DefaultUser user = new DefaultUser();
		user.setName(userId);
		user.setUserId(userId);
		user.setSource(getSource());
		user.setStatus(UserStatus.active);
		return user;
	}

}
