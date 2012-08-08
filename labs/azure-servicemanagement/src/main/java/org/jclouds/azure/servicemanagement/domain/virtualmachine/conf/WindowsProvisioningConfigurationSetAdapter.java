package org.jclouds.azure.servicemanagement.domain.virtualmachine.conf;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class WindowsProvisioningConfigurationSetAdapter extends XmlAdapter<WindowsProvisioningConfiguration, ConfigurationSet> {

	@Override
	public ConfigurationSet unmarshal(WindowsProvisioningConfiguration networkConfigurationSet)
			throws Exception {
		return networkConfigurationSet;
	}

	@Override
	public WindowsProvisioningConfiguration marshal(ConfigurationSet configurationSet) throws Exception {
		if (configurationSet instanceof NetworkConfiguration){
			return (WindowsProvisioningConfiguration)configurationSet;
		}
		
		if (WindowsProvisioningConfiguration.ID.equals(configurationSet.getConfigurationSetType())){
			return new WindowsProvisioningConfiguration();
		}
		return null;
	}

}