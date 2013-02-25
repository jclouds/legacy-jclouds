/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.savvis.vpdc.domain;

import java.net.URI;

import com.google.common.base.Objects;

/**
 * API returns a firewall rule in a firewall service
 * 
 * @author Kedar Dave
 */
public class FirewallRule extends ResourceImpl {

	public static Builder builder() {
      return new Builder();
	}

	public static class Builder extends ResourceImpl.Builder {
		
		private String firewallType;
		private boolean isEnabled;
		private String source;
		private String destination;
		private String port;
		private String policy;
		private String description;
		private boolean isLogged;
		private String protocol;
		
		@Override
		public FirewallRule build() {
			return new FirewallRule(id, name, type, href, firewallType, isEnabled, source, destination, port,
					policy, description, isLogged, protocol);
		}

		public Builder firewallType(String firewallType) {
			this.firewallType = firewallType;
			return this;
		}
		
		public Builder isEnabled(boolean isEnabled) {
			this.isEnabled = isEnabled;
			return this;
		}

		public Builder source(String source) {
			this.source = source;
			return this;
		}
		
		public Builder destination(String destination) {
			this.destination = destination;
			return this;
		}
		
		public Builder port(String port) {
			this.port = port;
			return this;
		}
		
		public Builder policy(String policy) {
			this.policy = policy;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder isLogged(boolean isLogged) {
			this.isLogged = isLogged;
			return this;
		}
		
		public Builder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}
		
		public static Builder fromFirewallRule(FirewallRule in) {
	        return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref())
	        	.firewallType(in.getFirewallType()).isEnabled(in.isEnabled()).source(in.getSource())
	        	.destination(in.getDestination()).port(in.getPort()).policy(in.getPolicy()).description(in.getDescription())
	        	.isLogged(in.isLogged()).protocol(in.getProtocol());
	     }
		
		@Override
		public Builder id(String id) {
			return Builder.class.cast(super.id(id));
		}

		@Override
		public Builder name(String name) {
			return Builder.class.cast(super.name(name));
		}

		@Override
		public Builder type(String type) {
			return Builder.class.cast(super.type(type));
		}

		@Override
		public Builder href(URI href) {
			return Builder.class.cast(super.href(href));
		}
		
	}
	
	private final String firewallType;
	private final boolean isEnabled;
	private final String source;
	private final String destination;
	private final String port;
	private final String policy;
	private final String description;
	private final boolean isLogged;
	private final String protocol;
	
	public FirewallRule(String id, String name, String type, URI href, String firewallType, boolean isEnabled, 
			String source, String destination, String port, String policy, String description, boolean isLogged, String protocol) {
      super(id, name, type, href);
      this.firewallType = firewallType;
      this.isEnabled = isEnabled;
      this.source = source;
      this.destination = destination;
      this.port = port;
      this.policy = policy;
      this.description = description;
      this.isLogged = isLogged;
      this.protocol = protocol;
	}

	@Override
    public Builder toBuilder() {
		return Builder.fromFirewallRule(this);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(firewallType, isEnabled, source, destination,
         port, policy, description, isLogged, protocol);
    }

	@Override
	public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!(obj instanceof FirewallRule))
         return false;
      FirewallRule other = (FirewallRule) obj;
      return Objects.equal(firewallType, other.firewallType) &&
	     isEnabled == other.isEnabled &&
	     Objects.equal(source, other.source) &&
	     Objects.equal(destination, other.destination) &&
	     Objects.equal(port, other.port) &&
	     Objects.equal(policy, other.policy) &&
	     Objects.equal(description, other.description) &&
	     isLogged == other.isLogged &&
	     Objects.equal(protocol, other.protocol);
    }
	
	public boolean isEnabled() {
		return isEnabled;
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public String getFirewallType() {
		return firewallType;
	}

	public String getPort() {
		return port;
	}

	public String getPolicy() {
		return policy;
	}

	public String getDescription() {
		return description;
	}

	public boolean isLogged() {
		return isLogged;
	}

	public String getProtocol() {
		return protocol;
	}

	@Override
	public String toString() {
      return "[firewallType=" + firewallType + ", isEnabled=" + isEnabled + ", description=" + description + ", source=" + source + ", destination=" + destination
      + ", port=" + port + ", protocol=" + protocol + ", policy=" + policy + ", isLogged=" + isLogged;
	}

}
