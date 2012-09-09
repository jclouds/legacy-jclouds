package org.jclouds.azure.management.domain;

import org.jclouds.azure.management.domain.role.Protocol;

public class InputEndpoint {

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Integer localPort;
		private Integer externalPort;
		private String name;
		private Protocol protocol;

		public Builder localPort(Integer localPort) {
			this.localPort = localPort;
			return this;
		}

		public Builder externalPort(Integer externalPort) {
			this.externalPort = externalPort;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder protocol(Protocol protocol) {
			this.protocol = protocol;
			return this;
		}
		
		public InputEndpoint build(){
			return new InputEndpoint(localPort, externalPort, name, protocol);
		}

	}

	private final Integer localPort;
	private final Integer externalPort;
	private final String name;
	private final Protocol protocol;

	public InputEndpoint(Integer localPort, Integer externalPort, String name,
			Protocol protocol) {
		super();
		this.localPort = localPort;
		this.externalPort = externalPort;
		this.name = name;
		this.protocol = protocol;
	}

	public Integer getLocalPort() {
		return localPort;
	}

	public Integer getExternalPort() {
		return externalPort;
	}

	public String getName() {
		return name;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((externalPort == null) ? 0 : externalPort.hashCode());
		result = prime * result
				+ ((localPort == null) ? 0 : localPort.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
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
		InputEndpoint other = (InputEndpoint) obj;
		if (externalPort == null) {
			if (other.externalPort != null)
				return false;
		} else if (!externalPort.equals(other.externalPort))
			return false;
		if (localPort == null) {
			if (other.localPort != null)
				return false;
		} else if (!localPort.equals(other.localPort))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (protocol != other.protocol)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InputEndPoint [localPort=" + localPort + ", externalPort="
				+ externalPort + ", name=" + name + ", protocol=" + protocol
				+ "]";
	}

}
