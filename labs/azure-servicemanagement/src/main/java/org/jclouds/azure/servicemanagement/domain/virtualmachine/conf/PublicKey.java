package org.jclouds.azure.servicemanagement.domain.virtualmachine.conf;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "PublicKey")
public class PublicKey extends SSHKey {

	public PublicKey(){
	}
	
	@Override
	public String toString() {
		return "PublicKey [fingerPrint=" + fingerPrint + ", path=" + path + "]";
	}
	
	
}
