package org.sonatype.nexus.plugins.cas;

import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.security.authorization.AbstractReadOnlyAuthorizationManager;
import org.sonatype.security.authorization.AuthorizationManager;
import org.sonatype.security.authorization.NoSuchPrivilegeException;
import org.sonatype.security.authorization.NoSuchRoleException;
import org.sonatype.security.authorization.Privilege;
import org.sonatype.security.authorization.Role;

@Component(role=AuthorizationManager.class, hint=CasAuthorizationManager.SOURCE_NAME)
public class CasAuthorizationManager extends AbstractReadOnlyAuthorizationManager {
	
	public static final String SOURCE_NAME = "CAS";

	@Override
	public String getSource() {
		return SOURCE_NAME;
	}

	@Override
	public Set<Role> listRoles() {
		return null;
	}

	@Override
	public Role getRole(String roleId) throws NoSuchRoleException {
		Role role = new Role();
		role.setName(roleId);
		role.setRoleId(roleId);
		role.setSource(getSource());
		return role;
	}

	@Override
	public Set<Privilege> listPrivileges() {
		return null;
	}

	@Override
	public Privilege getPrivilege(String privilegeId) throws NoSuchPrivilegeException {
		return null;
	}

}
