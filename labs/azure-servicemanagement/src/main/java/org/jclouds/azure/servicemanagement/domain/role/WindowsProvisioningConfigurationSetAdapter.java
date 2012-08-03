package org.jclouds.azure.servicemanagement.domain.role;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class WindowsProvisioningConfigurationSetAdapter extends XmlAdapter<WindowsProvisioningConfigurationSet, ConfigurationSet> {

	@Override
	public ConfigurationSet unmarshal(WindowsProvisioningConfigurationSet networkConfigurationSet)
			throws Exception {
		return networkConfigurationSet;
	}

	@Override
	public WindowsProvisioningConfigurationSet marshal(ConfigurationSet configurationSet) throws Exception {
		if (configurationSet instanceof NetworkConfigurationSet){
			return (WindowsProvisioningConfigurationSet)configurationSet;
		}
		
		if (WindowsProvisioningConfigurationSet.ID.equals(configurationSet.getConfigurationSetType())){
			return new WindowsProvisioningConfigurationSet();
		}
		return null;
	}

}