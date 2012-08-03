package org.jclouds.azure.servicemanagement.domain.role;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name = "PersistentVMRole")
@XmlSeeAlso({ NetworkConfigurationSet.class,
		LinuxProvisioningConfigurationSet.class })
public class Role {

	/**
	 * Specifies the name for the virtual machine. The name must be unique
	 * within Windows Azure.
	 */
	@XmlElement(required = true, name = "RoleName")
	private String roleName;

	/**
	 * The type of the role for the virtual machine. The only supported value is
	 * PersistentVMRole.
	 */
	@XmlElement(required = true, name = "RoleType")
	private String roleType;

	/**
	 * Specifies the name of an availability set to which to add the virtual
	 * machine. This value controls the virtual machine allocation in the
	 * Windows Azure environment. Virtual machines specified in the same
	 * availability set are allocated to different nodes to maximize
	 * availability.
	 */
	@XmlElement(name = "AvailabilitySetName")
	private String availabilitySetName;

	/**
	 * The size of the virtual machine to allocate. The default value is Small.
	 */
	@XmlElement(name = "RoleSize")
	private RoleSize roleSize;

	@XmlElement(name = "OSVirtualHardDisk")
	private OSVirtualHardDisk osVirtualHardDisk;

	/**
	 * Required. You must specifye either a WindowsProvisioningConfigurationSet
	 * or LinuxProvisioningConfigurationSet configuration set.
	 * 
	 * Optional. You can specify a NetworkConfigurationSet which contains the
	 * metadata required to create the virtual network configuration for a
	 * virtual machine.
	 */
	@XmlElementWrapper(name = "ConfigurationSets")
	@XmlElement(name = "ConfigurationSet")
	private List<? extends ConfigurationSet> configurationSets = new ArrayList<ConfigurationSet>(
			0);

	@XmlElementWrapper(name = "DataVirtualHardDisks")
	@XmlElement(name = "DataVirtualHardDisk")
	private List<DataVirtualHardDisk> dataVirtualHardDisks = new ArrayList<DataVirtualHardDisk>(
			0);

	public Role() {
		super();
	}

	public String getRoleName() {
		return roleName;
	}

	public String getRoleType() {
		return roleType;
	}

	public String getAvailabilitySetName() {
		return availabilitySetName;
	}

	public RoleSize getRoleSize() {
		return roleSize;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public void setAvailabilitySetName(String availabilitySetName) {
		this.availabilitySetName = availabilitySetName;
	}

	public void setRoleSize(RoleSize roleSize) {
		this.roleSize = roleSize;
	}

	public void setOsVirtualHardDisk(OSVirtualHardDisk osVirtualHardDisk) {
		this.osVirtualHardDisk = osVirtualHardDisk;
	}

	public OSVirtualHardDisk getOsVirtualHardDisk() {
		return osVirtualHardDisk;
	}

	public List<? extends ConfigurationSet> getConfigurationSets() {
		return configurationSets;
	}

	public void setConfigurationSets(List<ConfigurationSet> configurationSets) {
		this.configurationSets = configurationSets;
	}

	public List<DataVirtualHardDisk> getDataVirtualHardDisks() {
		return dataVirtualHardDisks;
	}

	public void setDataVirtualHardDisks(
			List<DataVirtualHardDisk> dataVirtualHardDisks) {
		this.dataVirtualHardDisks = dataVirtualHardDisks;
	}

	@Override
	public String toString() {
		return "Role [roleName=" + roleName + ", roleType=" + roleType
				+ ", availabilitySetName=" + availabilitySetName
				+ ", roleSize=" + roleSize + ", osVirtualHardDisk="
				+ osVirtualHardDisk + ", configurationSets="
				+ configurationSets + ", dataVirtualHardDisks="
				+ dataVirtualHardDisks + "]";
	}

}
