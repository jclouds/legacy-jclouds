/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.savvis.vpdc.domain;

import java.net.URI;

/**
 * API returns a firewall rule in a firewall service
 * 
 * @author Kedar Dave
 */
public class FirewallRule extends Resource {

	public static Builder builder() {
      return new Builder();
	}

	public static class Builder extends Resource.Builder {
		
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
	public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      FirewallRule other = (FirewallRule) obj;
      if (firewallType == null) {
          if (other.firewallType != null)
             return false;
       } else if (!firewallType.equals(other.firewallType))
          return false;
      if (source == null) {
         if (other.source != null)
            return false;
      } else if (!source.equals(other.source))
         return false;
      if (destination == null) {
         if (other.destination != null)
            return false;
      } else if (!destination.equals(other.destination))
         return false;
      if (port == null) {
          if (other.port != null)
             return false;
       } else if (!port.equals(other.port))
          return false;
      if (policy == null) {
          if (other.policy != null)
             return false;
       } else if (!policy.equals(other.policy))
          return false;
      if (protocol == null) {
          if (other.protocol != null)
             return false;
       } else if (!protocol.equals(other.protocol))
          return false;
      return true;
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
      return "[firewallType=" + firewallType + ", description=" + description + ", source=" + source + ", destination=" + destination
      + ", port=" + port + ", protocol=" + protocol + ", policy=" + policy + ", isLogged=" + isLogged;
	}

}