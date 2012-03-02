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

import com.google.common.base.Objects;

public class NetworkInterfaceCard {

	private final long slot;
	private final NetworkAdapter networkAdapter;
	private final String hostInterfaceName;

	
	public NetworkInterfaceCard(long slot, NetworkAdapter networkAdapter, String hostInterfaceName) {
		this.slot = slot;
		this.networkAdapter = networkAdapter;
		this.hostInterfaceName = hostInterfaceName;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private long slot;
		private NetworkAdapter networkAdapter;
		private String hostInterfaceName;
		
		public Builder slot(long slot) {
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
		
		public NetworkInterfaceCard build() {
			return new NetworkInterfaceCard(slot, networkAdapter, hostInterfaceName);
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
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof NetworkInterfaceCard) {
			NetworkInterfaceCard other = (NetworkInterfaceCard) o;
			return Objects.equal(slot,
					other.slot) &&
					Objects.equal(networkAdapter, other.networkAdapter);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(slot, networkAdapter);
	}

	@Override
	public String toString() {
		return "NetworkInterfaceCard{" + "slot= "+ 
				slot + 
				"networkAdapter= " + networkAdapter +
				'}';
	}

}
