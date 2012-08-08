package org.jclouds.azure.servicemanagement.domain.virtualmachine.conf;

import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "KeyPair")
public class KeyPair extends SSHKey {

	public KeyPair(){
	}
	
	@Override
	public String toString() {
		return "KeyPair [fingerPrint=" + fingerPrint + ", path=" + path + "]";
	}
	
	
}
