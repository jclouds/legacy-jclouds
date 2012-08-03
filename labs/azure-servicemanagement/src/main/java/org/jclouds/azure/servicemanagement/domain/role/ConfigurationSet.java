package org.jclouds.azure.servicemanagement.domain.role;

import javax.xml.bind.annotation.XmlElement;

public class ConfigurationSet {

	/**
	 *  Specifies the configuration set type. 
	 */
	@XmlElement(required = true,name = "ConfigurationSetType")
	protected String configurationSetType;
	
	public ConfigurationSet(){
	}
	
	public String getConfigurationSetType() {
		return configurationSetType;
	}

	public void setConfigurationSetType(String configurationSetType) {
		this.configurationSetType = configurationSetType;
	}


	@Override
	public String toString() {
		return "ConfigurationSet [configurationSetType=" + configurationSetType+ "]";
	}
	
	
}
