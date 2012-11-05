package org.jclouds.azure.management.domain;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Lists;

/**
 * 
 * To create a new deployment/role
 * 
 * Warning : the OSType must be the one of the source image used to create the VM
 * 
 * @author Gérald Pereira
 * 
 */
public class DeploymentParams {

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {
		return builder().fromLinuxDeploymentParams(this);
	}

	public static class Builder {

		protected String name;
		protected String sourceImageName;
		protected String username;
		protected String password;
		protected String storageAccount;
		protected OSType osType;
		protected RoleSize size = RoleSize.SMALL;
		protected List<InputEndpoint> endpoints = Lists.newArrayList();

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder sourceImageName(String sourceImageName) {
			this.sourceImageName = sourceImageName;
			return this;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder storageAccount(String storageAccount) {
			this.storageAccount = storageAccount;
			return this;
		}

		public Builder size(RoleSize size) {
			this.size = size;
			return this;
		}
		
		public Builder osType(OSType osType) {
			this.osType = osType;
			return this;
		}
		
		public Builder endpoint(InputEndpoint endpoint) {
			endpoints.add(endpoint);
			return this;
		}

		public DeploymentParams build() {
			return new DeploymentParams(name, sourceImageName,
					username, password, storageAccount, size,osType,endpoints);
		}

		public Builder fromLinuxDeploymentParams(DeploymentParams in) {
			// TODO Since the roleName should be unique, is it a good idea to
			// copy it ?
			return this.name(in.getName())
					.sourceImageName(in.getSourceImageName())
					.username(in.getUsername()).password(in.getPassword())
					.size(in.getSize());
		}
	}

	protected final String name;
	protected final String sourceImageName;
	protected final String username;
	protected final String password;
	protected final String storageAccount;
	protected final RoleSize size;
	protected final OSType osType;
	protected final List<InputEndpoint> endpoints;
	
	
	public DeploymentParams(String name, String sourceImageName,
			String username, String password, String storageAccount,
			RoleSize size,OSType osType,final List<InputEndpoint> endpoints) {
		super();
		this.name = name;
		this.sourceImageName = sourceImageName;
		this.username = username;
		this.password = password;
		this.storageAccount = storageAccount;
		this.size = size;
		this.osType = osType;
		this.endpoints = endpoints;
	}

	/**
	 * Specifies the name for the deployment and its virtual machine. The name must be unique
	 * within Windows Azure.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Specifies the name of an operating system image in the image repository.
	 */
	public String getSourceImageName() {
		return sourceImageName;
	}

	/**
	 * Specifies the name of a user to be created in the sudoer group of the
	 * virtual machine. User names are ASCII character strings 1 to 32
	 * characters in length.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Specifies the associated password for the user name.
	 * PasswoazureManagement are ASCII character strings 6 to 72 characters in
	 * length.
	 */
	public String getPassword() {
		return password;
	}

	public String getStorageAccount() {
		return storageAccount;
	}

	/**
	 * The size of the virtual machine to allocate. The default value is Small.
	 */
	public RoleSize getSize() {
		return size;
	}
	
	/**
	 * Os type of the given sourceImage
	 */
	public OSType getOsType() {
		return osType;
	}
	
	public List<InputEndpoint> getEndpoints() {
		return endpoints;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeploymentParams other = (DeploymentParams) obj;
		return Objects.equal(this.name, other.name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return string().toString();
	}

	protected ToStringHelper string() {
		return Objects.toStringHelper(this).add("name", name)
				.add("sourceImageName", sourceImageName).add("size", size);
	}
}
