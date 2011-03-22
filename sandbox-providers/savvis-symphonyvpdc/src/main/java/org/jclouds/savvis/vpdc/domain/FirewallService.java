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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * API returns the firewall service containing firewall rules for a given VDC
 * 
 * @author Kedar Dave
 */
public class FirewallService extends Resource {

	public static Builder builder() {
      return new Builder();
	}

	public static class Builder extends Resource.Builder {
		private boolean isEnabled;
		private Set<FirewallRule> firewallRules = Sets.newLinkedHashSet();
		
		@Override
		public FirewallService build() {
			return new FirewallService(id, name, type, href, isEnabled, firewallRules);
		}

		public Builder isEnabled(boolean isEnabled) {
	         this.isEnabled = isEnabled;
	         return this;
		}
		
		public Builder firewallRule(FirewallRule in) {
	         this.firewallRules.add(checkNotNull(in, "firewallRule"));
	         return this;
		}
		
		public Builder firewallRules(Set<FirewallRule> firewallRules) {
	         this.firewallRules.addAll(checkNotNull(firewallRules, "firewallRules"));
	         return this;
		}
		
		public static Builder fromFirewallService(FirewallService in) {
	        return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref())
	        	.isEnabled(in.isEnabled()).firewallRules(in.getFirewallRules());
	    }
		
		public Set<FirewallRule> getFirewallRules() {
			return firewallRules;
		}

		public void setFirewallRules(Set<FirewallRule> firewallRules) {
			this.firewallRules = firewallRules;
		}
		
		public boolean isEnabled() {
			return isEnabled;
		}

		public void setEnabled(boolean isEnabled) {
			this.isEnabled = isEnabled;
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
	
	private boolean isEnabled;
	private Set<FirewallRule> firewallRules;
	
	public FirewallService(String id, String name, String type, URI href, boolean isEnabled, Set<FirewallRule> firewallRules) {
      super(id, name, type, href);
      this.isEnabled = isEnabled;
      this.firewallRules = ImmutableSet.copyOf(checkNotNull(firewallRules, "firewallRules"));
	}

	@Override
    public Builder toBuilder() {
		return Builder.fromFirewallService(this);
    }
	
	public Set<FirewallRule> getFirewallRules() {
		return firewallRules;
	}

	public void setFirewallRules(Set<FirewallRule> firewallRules) {
		this.firewallRules = firewallRules;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

}