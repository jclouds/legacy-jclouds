package org.jclouds.azure.servicemanagement.domain.virtualmachine.conf;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class LinuxProvisioningConfigurationSetAdapter extends XmlAdapter<LinuxProvisioningConfiguration, ConfigurationSet> {

	@Override
	public ConfigurationSet unmarshal(LinuxProvisioningConfiguration networkConfigurationSet)
			throws Exception {
		return networkConfigurationSet;
	}

	@Override
	public LinuxProvisioningConfiguration marshal(ConfigurationSet configurationSet) throws Exception {
		if (configurationSet instanceof NetworkConfiguration){
			return (LinuxProvisioningConfiguration)configurationSet;
		}
		
		if (LinuxProvisioningConfiguration.ID.equals(configurationSet.getConfigurationSetType())){
			return new LinuxProvisioningConfiguration();
		}
		return null;
	}

}