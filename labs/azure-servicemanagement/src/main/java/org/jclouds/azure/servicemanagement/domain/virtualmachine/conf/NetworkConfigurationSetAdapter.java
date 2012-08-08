package org.jclouds.azure.servicemanagement.domain.virtualmachine.conf;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class NetworkConfigurationSetAdapter extends XmlAdapter<NetworkConfiguration, ConfigurationSet> {

	@Override
	public ConfigurationSet unmarshal(NetworkConfiguration networkConfigurationSet)
			throws Exception {
		return networkConfigurationSet;
	}

	@Override
	public NetworkConfiguration marshal(ConfigurationSet configurationSet) throws Exception {
		if (configurationSet instanceof NetworkConfiguration){
			return (NetworkConfiguration)configurationSet;
		}
		
		if (NetworkConfiguration.ID.equals(configurationSet.getConfigurationSetType())){
			return new NetworkConfiguration();
		}
		return null;
	}

}