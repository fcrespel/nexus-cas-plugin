package org.sonatype.nexus.plugins.cas.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.guice.bean.locators.BeanLocator;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.plugins.cas.config.model.v1_0_0.Configuration;
import org.sonatype.nexus.plugins.cas.config.model.v1_0_0.io.xpp3.CasPluginConfigurationXpp3Reader;
import org.sonatype.sisu.goodies.eventbus.internal.DefaultEventBus;
import org.sonatype.sisu.goodies.eventbus.internal.guava.EventBus;

/**
 * CAS plugin configuration implementation using an XML file.
 * @author Fabien Crespel <fabien@crespel.net>
 */
@Component(role = CasPluginConfiguration.class, hint = "default")
public class DefaultCasPluginConfiguration extends DefaultEventBus implements CasPluginConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DefaultCasPluginConfiguration.class);
	
	@Requirement
	private NexusConfiguration nexusConfiguration;
	
    private File configurationFile;
	private Configuration configuration;
    private ReentrantLock lock = new ReentrantLock();

	@Inject
	public DefaultCasPluginConfiguration(EventBus eventBus, BeanLocator beanLocator) {
		super(eventBus, beanLocator);
	}
    
    public File getConfigurationFile() {
    	if (configurationFile == null) {
    		configurationFile = new File(nexusConfiguration.getConfigurationDirectory(), "cas-plugin.xml");
    	}
    	return configurationFile;
    }
    
    public Configuration getConfiguration() {
        if (configuration == null) {
	        lock.lock();
	        
	        FileInputStream is = null;
	        try {
	            is = new FileInputStream(getConfigurationFile());
	            CasPluginConfigurationXpp3Reader reader = new CasPluginConfigurationXpp3Reader();
	            configuration = reader.read(is);
	            
	        } catch (FileNotFoundException e) {
	            logger.error("CAS configuration file does not exist: " + getConfigurationFile().getAbsolutePath());
	            
	        } catch (IOException e) {
	        	logger.error("IOException while retrieving configuration file", e);
	        	
	        } catch (XmlPullParserException e) {
	        	logger.error("Invalid XML Configuration", e);
	        	
	        } finally {
	            if (is != null) {
	                try {
	                    is.close();
	                } catch (IOException ignored) {
	                }
	            }
	            lock.unlock();
	        }
        }
        return configuration;
    }
}
