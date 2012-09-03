package org.jclouds.azure.management.domain;

import java.net.URI;

public class Deployment {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String deploymentName;
		private DeploymentSlot deploymentSlot;
		private DeploymentStatus deploymentStatus;
		private String deploymentLabel;
		private URI deploymentURL;
		private String roleName;
		private String instanceName;
		private InstanceStatus instanceStatus;
		private String instanceStateDetails;
		private String instanceErrorCode;
		private RoleSize instanceSize;
		private String privateIpAddress;
		private String publicIpAddress;

		public Builder deploymentName(final String deploymentName) {
			this.deploymentName = deploymentName;
			return this;
		}

		public Builder deploymentSlot(final DeploymentSlot deploymentSlot) {
			this.deploymentSlot = deploymentSlot;
			return this;
		}

		public Builder deploymentStatus(final DeploymentStatus deploymentStatus) {
			this.deploymentStatus = deploymentStatus;
			return this;
		}

		public Builder deploymentLabel(final String deploymentLabel) {
			this.deploymentLabel = deploymentLabel;
			return this;
		}

		public Builder deploymentURL(final URI deploymentURL) {
			this.deploymentURL = deploymentURL;
			return this;
		}

		public Builder instanceSize(final RoleSize instanceSize) {
			this.instanceSize = instanceSize;
			return this;
		}

		public Builder instanceName(final String instanceName) {
			this.instanceName = instanceName;
			return this;
		}

		public Builder instanceStatus(final InstanceStatus instanceStatus) {
			this.instanceStatus = instanceStatus;
			return this;
		}

		public Builder instanceStateDetails(final String instanceStateDetails) {
			this.instanceStateDetails = instanceStateDetails;
			return this;
		}

		public Builder instanceErrorCode(final String instanceErrorCode) {
			this.instanceErrorCode = instanceErrorCode;
			return this;
		}

		public Builder privateIpAddress(final String privateIpAddress) {
			this.privateIpAddress = privateIpAddress;
			return this;
		}

		public Builder publicIpAddress(final String publicIpAddress) {
			this.publicIpAddress = publicIpAddress;
			return this;
		}

		public Builder roleName(final String roleName) {
			this.roleName = roleName;
			return this;
		}

		public Deployment build() {
			return new Deployment(deploymentName, deploymentSlot,
					deploymentStatus, deploymentLabel, deploymentURL, roleName,
					instanceName, instanceStatus,instanceStateDetails,instanceErrorCode, instanceSize,
					privateIpAddress, publicIpAddress);
		}

	}

	/**
	 * The user-supplied name for this deployment.
	 */
	private final String deploymentName;
	/**
	 * The environment to which the hosted service is deployed, either staging
	 * or production.
	 */
	private final DeploymentSlot deploymentSlot;
	/**
	 * The status of the deployment.
	 */
	private final DeploymentStatus deploymentStatus;
	/**
	 * The user-supplied name of the deployment returned as a base-64 encoded
	 * string. This name can be used identify the deployment for your tracking
	 * purposes.
	 */
	private final String deploymentLabel;
	/**
	 * The URL used to access the hosted service. For example, if the service
	 * name is MyService you could access the access the service by calling:
	 * http://MyService.cloudapp.net
	 */
	private final URI deploymentURL;

	/**
	 * Specifies the name for the virtual machine. The name must be unique
	 * within Windows Azure.
	 */
	private final String roleName;

	/**
	 * The name of the specific role instance (if any).
	 */
	private final String instanceName;
	/**
	 * The current status of this instance.
	 */
	private final InstanceStatus instanceStatus;
	/**
	 * The instance state is returned as an English human-readable string that,
	 * when present, provides a snapshot of the state of the virtual machine at
	 * the time the operation was called.
	 * 
	 * For example, when the instance is first being initialized a
	 * "Preparing Windows for first use." could be returned.
	 */
	private final String instanceStateDetails;
	/**
	 * Error code of the latest role or VM start
	 * 
	 * For VMRoles the error codes are:
	 * 
	 * WaitTimeout - The virtual machine did not communicate back to Azure
	 * infrastructure within 25 minutes. Typically this indicates that the
	 * virtual machine did not start or that the guest agent is not installed.
	 * 
	 * VhdTooLarge - The VHD image selected was too large for the virtual
	 * machine hosting the role.
	 * 
	 * AzureInternalError – An internal error has occurred that has caused to
	 * virtual machine to fail to start. Contact support for additional
	 * assistance.
	 * 
	 * For web and worker roles this field returns an error code that can be provided to Windows Azure support to assist in resolution of errors. Typically this field will be empty.
	 */
	private final String instanceErrorCode;

	/**
	 * The size of the role instance
	 */
	private final RoleSize instanceSize;
	private final String privateIpAddress;
	private final String publicIpAddress;

	public Deployment(String deploymentName, DeploymentSlot deploymentSlot,
			DeploymentStatus deploymentStatus, String deploymentLabel,
			URI deploymentURL, String roleName, String instanceName,
			InstanceStatus instanceStatus,String instanceStateDetails, String instanceErrorCode, RoleSize instanceSize,
			String privateIpAddress, String publicIpAddress) {
		super();
		this.deploymentName = deploymentName;
		this.deploymentSlot = deploymentSlot;
		this.deploymentStatus = deploymentStatus;
		this.deploymentLabel = deploymentLabel;
		this.deploymentURL = deploymentURL;
		this.roleName = roleName;
		this.instanceName = instanceName;
		this.instanceStatus = instanceStatus;
		this.instanceStateDetails = instanceStateDetails;
		this.instanceErrorCode = instanceErrorCode;
		this.instanceSize = instanceSize;
		this.privateIpAddress = privateIpAddress;
		this.publicIpAddress = publicIpAddress;
	}

	public String getDeploymentName() {
		return deploymentName;
	}

	public DeploymentSlot getDeploymentSlot() {
		return deploymentSlot;
	}

	public DeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public String getDeploymentLabel() {
		return deploymentLabel;
	}

	public URI getDeploymentURL() {
		return deploymentURL;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public InstanceStatus getInstanceStatus() {
		return instanceStatus;
	}
	
	public String getInstanceStateDetails() {
		return instanceStateDetails;
	}
	
	public String getInstanceErrorCode() {
		return instanceErrorCode;
	}
	
	public RoleSize getInstanceSize() {
		return instanceSize;
	}

	public String getPrivateIpAddress() {
		return privateIpAddress;
	}

	public String getPublicIpAddress() {
		return publicIpAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deploymentName == null) ? 0 : deploymentName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Deployment other = (Deployment) obj;
		if (deploymentName == null) {
			if (other.deploymentName != null)
				return false;
		} else if (!deploymentName.equals(other.deploymentName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Deployment [deploymentName=" + deploymentName
				+ ", deploymentSlot=" + deploymentSlot + ", deploymentStatus="
				+ deploymentStatus + ", deploymentLabel=" + deploymentLabel
				+ ", deploymentURL=" + deploymentURL + ", roleName=" + roleName
				+ ", instanceName=" + instanceName + ", instanceStatus="
				+ instanceStatus + ", instanceStateDetails="
				+ instanceStateDetails + ", instanceErrorCode="
				+ instanceErrorCode + ", instanceSize=" + instanceSize
				+ ", privateIpAddress=" + privateIpAddress
				+ ", publicIpAddress=" + publicIpAddress + "]";
	}


}
