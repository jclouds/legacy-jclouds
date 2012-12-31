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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.virtualbox_4_2.NATProtocol;
import org.virtualbox_4_2.NetworkAttachmentType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents a network adapter in VirtualBox.
 * <p/>
 * redirectRules are the redirect rules that are applied to the network adapter.
 */
public class NetworkAdapter {

	private final NetworkAttachmentType networkAttachmentType;
	private final String macAddress;
	private final Set<RedirectRule> redirectRules;
	private final String staticIp;

	public NetworkAdapter(NetworkAttachmentType networkAttachmentType,
			String macAddress, Set<RedirectRule> redirectRules,
			String staticIp) {
		this.networkAttachmentType = checkNotNull(networkAttachmentType,
				"networkAttachmentType can't be null");
		this.macAddress = macAddress;
		this.redirectRules = ImmutableSet.<RedirectRule>copyOf(redirectRules);
		this.staticIp = staticIp;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private NetworkAttachmentType networkAttachmentType;
		private String macAddress;
		private Set<RedirectRule> redirectRules = Sets.newLinkedHashSet();
		private String staticIp;

		/**
		 * 
		 * @param networkAttachmentType
		 * @return
		 */
		public Builder networkAttachmentType(
				NetworkAttachmentType networkAttachmentType) {
			this.networkAttachmentType = networkAttachmentType;
			return this;
		}

		/**
		 * 
		 * @param macAddress
		 * @return
		 */
		public Builder macAddress(String macAddress) {
			this.macAddress = macAddress;
			return this;
		}

		/**
		 * @param host
		 *            incoming address
		 * @param hostPort
		 * @param guest
		 *            guest address or empty string for all addresses
		 * @param guestPort
		 * @return
		 */
		public Builder tcpRedirectRule(String host, int hostPort, String guest,
				int guestPort) {
			redirectRules.add(new RedirectRule(NATProtocol.TCP, host, hostPort,
					guest, guestPort));
			return this;
		}

		/**
		 * @param host
		 *            incoming address
		 * @param hostPort
		 * @param guest
		 *            guest address or empty string for all addresses
		 * @param guestPort
		 * @return
		 */
		public Builder udpRedirectRule(String host, int hostPort, String guest,
				int guestPort) {
			redirectRules.add(new RedirectRule(NATProtocol.UDP, host, hostPort,
					guest, guestPort));
			return this;
		}
		
		public Builder staticIp(@Nullable String staticIp) {
		   this.staticIp = staticIp;
		   return this;
		}

		public NetworkAdapter build() {
			return new NetworkAdapter(networkAttachmentType, macAddress,
					redirectRules,staticIp);
		}
	}

	public NetworkAttachmentType getNetworkAttachmentType() {
		return networkAttachmentType;
	}

	public Set<RedirectRule> getRedirectRules() {
		return Collections.unmodifiableSet(redirectRules);
	}

	public String getMacAddress() {
		return macAddress;
	}
	
	public String getStaticIp() {
         return staticIp;
      }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof NetworkAdapter) {
			NetworkAdapter other = (NetworkAdapter) o;
			return Objects.equal(networkAttachmentType,
					other.networkAttachmentType) &&
					Objects.equal(macAddress, other.macAddress) &&		
					Objects.equal(redirectRules, other.redirectRules);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(networkAttachmentType, macAddress, redirectRules);
	}

	@Override
	public String toString() {
		return "NetworkAdapter{networkAttachmentType="+ 
				networkAttachmentType + 
				", macAddress=" + macAddress +
				", redirectRules=" + redirectRules +
				'}';
	}
}
