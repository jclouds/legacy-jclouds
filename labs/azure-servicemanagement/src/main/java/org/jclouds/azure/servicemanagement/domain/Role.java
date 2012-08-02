package org.jclouds.azure.servicemanagement.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PersistentVMRole")
public class Role {

	@XmlElement(name = "RoleName")
	private String roleName;
	@XmlElement(name = "RoleType")
	private String roleType;
	@XmlElement(name = "AvailabilitySetName")
	private String availabilitySetName;
	@XmlElement(name = "RoleSize")
	private RoleSize roleSize;

	public Role() {
		super();
	}

	public Role(String roleName, String roleType, String availabilitySetName,
			RoleSize roleSize) {
		super();
		this.roleName = roleName;
		this.roleType = roleType;
		this.availabilitySetName = availabilitySetName;
		this.roleSize = roleSize;
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

	@Override
	public String toString() {
		return "Role [roleName=" + roleName + ", roleType=" + roleType
				+ ", availabilitySetName=" + availabilitySetName
				+ ", roleSize=" + roleSize + "]";
	}
	
}
