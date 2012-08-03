package org.jclouds.azure.servicemanagement.domain.role;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class NetworkConfigurationSetAdapter extends XmlAdapter<NetworkConfigurationSet, ConfigurationSet> {

	@Override
	public ConfigurationSet unmarshal(NetworkConfigurationSet networkConfigurationSet)
			throws Exception {
		return networkConfigurationSet;
	}

	@Override
	public NetworkConfigurationSet marshal(ConfigurationSet configurationSet) throws Exception {
		if (configurationSet instanceof NetworkConfigurationSet){
			return (NetworkConfigurationSet)configurationSet;
		}
		
		if (NetworkConfigurationSet.ID.equals(configurationSet.getConfigurationSetType())){
			return new NetworkConfigurationSet();
		}
		return null;
	}

}