package org.sonatype.nexus.plugins.cas.config;

import org.sonatype.nexus.plugins.cas.config.model.v1_0_0.Configuration;
import org.sonatype.sisu.goodies.eventbus.EventBus;

/**
 * CAS plugin configuration interface.
 * @author Fabien Crespel <fabien@crespel.net>
 */
public interface CasPluginConfiguration extends EventBus {

	Configuration getConfiguration();

}
