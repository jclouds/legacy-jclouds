package org.jclouds.azure.servicemanagement.domain.role;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class LinuxProvisioningConfigurationSetAdapter extends XmlAdapter<LinuxProvisioningConfigurationSet, ConfigurationSet> {

	@Override
	public ConfigurationSet unmarshal(LinuxProvisioningConfigurationSet networkConfigurationSet)
			throws Exception {
		return networkConfigurationSet;
	}

	@Override
	public LinuxProvisioningConfigurationSet marshal(ConfigurationSet configurationSet) throws Exception {
		if (configurationSet instanceof NetworkConfigurationSet){
			return (LinuxProvisioningConfigurationSet)configurationSet;
		}
		
		if (LinuxProvisioningConfigurationSet.ID.equals(configurationSet.getConfigurationSetType())){
			return new LinuxProvisioningConfigurationSet();
		}
		return null;
	}

}