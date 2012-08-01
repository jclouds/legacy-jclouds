package org.jclouds.azure.management.domain.role;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains the parameters Windows Azure uses to create the operating system
 * disk for the virtual machine.
 * 
 * @author gpereira
 * 
 */
@XmlRootElement(name = "OSVirtualHardDisk")
public class OSVirtualHardDisk extends VirtualHardDisk {

	/**
	 * Specifies the name of the disk image to use to create the virtual
	 * machine.
	 */
	@XmlElement(name = "SourceImageName")
	private String sourceImageName;

	@XmlElement(name = "OS")
	private String os;

	public OSVirtualHardDisk() {

	}

	public String getSourceImageName() {
		return sourceImageName;
	}

	public void setSourceImageName(String sourceImageName) {
		this.sourceImageName = sourceImageName;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getOs() {
		return os;
	}

	@Override
	public String toString() {
		return "OSVirtualHardDisk [hostCaching=" + hostCaching + ", diskLabel="
				+ diskLabel + ", diskName=" + diskName + ", mediaLink="
				+ mediaLink + ", sourceImageName=" + sourceImageName + "]";
	}

}
