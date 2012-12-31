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
package org.jclouds.virtualbox.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class NetworkInterfaceCard {

	private final long slot;
	private final NetworkAdapter networkAdapter;
	private final String hostInterfaceName;
	private final boolean enabled;
	
	public NetworkInterfaceCard(long slot, NetworkAdapter networkAdapter, String hostInterfaceName, boolean enabled) {
		this.slot = checkNotNull(slot, "slot can't be null");
		this.networkAdapter = checkNotNull(networkAdapter, "networkAdapter can't be null");
		this.hostInterfaceName = hostInterfaceName;
		this.enabled = enabled;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private long slot = 0L;
		private NetworkAdapter networkAdapter;
		private String hostInterfaceName;
		private boolean enabled = true;
		
		public Builder slot(long slot) {
		      checkArgument(slot >= 0 && slot < 4, "must be 0, 1, 2, 3: %s", slot);
			this.slot = slot;
			return this;
		}
		
		public Builder addNetworkAdapter(
				NetworkAdapter networkAdapter) {
			this.networkAdapter = networkAdapter;
			return this;
		}
		
		public Builder addHostInterfaceName(
				String hostInterfaceName) {
			this.hostInterfaceName = hostInterfaceName;
			return this;
		}	
		
	    public Builder enabled(
	          boolean enabled) {
	         this.enabled = enabled;
	         return this;
	      }  
		
		public NetworkInterfaceCard build() {
			return new NetworkInterfaceCard(slot, networkAdapter, hostInterfaceName, enabled);
		}
	}

	public long getSlot() {
		return slot;
	}
	
	public NetworkAdapter getNetworkAdapter() {
		return networkAdapter;
	}
	
	public String getHostInterfaceName() {
		return hostInterfaceName;
	}
	
	public boolean isEnabled() {
      return enabled;
   }
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof NetworkInterfaceCard) {
			NetworkInterfaceCard other = (NetworkInterfaceCard) o;
			return Objects.equal(slot,
					other.slot) &&
					Objects.equal(networkAdapter, other.networkAdapter)
					&& Objects.equal(enabled, other.enabled);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(slot, networkAdapter, enabled);
	}

	@Override
	public String toString() {
		return "NetworkInterfaceCard{slot="+ 
				slot + 
				", networkAdapter=" + networkAdapter +
				", enabled=" + enabled +
				'}';
	}

}
