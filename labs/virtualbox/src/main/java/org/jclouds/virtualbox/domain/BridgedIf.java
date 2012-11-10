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

import com.google.common.base.Objects;

/**
 * Name: en1: Wi-Fi (AirPort) GUID: 00316e65-0000-4000-8000-28cfdaf2917a Dhcp:
 * Disabled IPAddress: 192.168.57.1 NetworkMask: 255.255.255.0 IPV6Address:
 * IPV6NetworkMaskPrefixLength: 0 HardwareAddress: 28:cf:da:f2:91:7a MediumType:
 * Ethernet Status: Up VBoxNetworkName: HostInterfaceNetworking-en1: Wi-Fi
 * (AirPort)
 * 
 * @author Andrea Turli
 * 
 */
public class BridgedIf {

	private final String name;
	private final String guid;
	private final String dhcp;
	private final String ipAddress;
	private final String networkMask;
	private final String ipv6Address;
	private final String ipv6NetworkMask;
	private final String mediumType;
	private final String status;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String name;
		private String guid;
		private String dhcp;
		private String ipAddress;
		private String networkMask;
		private String ipv6Address;
		private String iv6NetworkMask;
		private String mediumType;
		private String status;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder guid(String guid) {
			this.guid = guid;
			return this;
		}

		public Builder dhcp(String dhcp) {
			this.dhcp = dhcp;
			return this;
		}

		public Builder ip(String ipAddress) {
			this.ipAddress = ipAddress;
			return this;
		}

		public Builder networkMask(String networkMask) {
			this.networkMask = networkMask;
			return this;
		}

		public Builder ipv6(String ipv6Address) {
			this.ipv6Address = ipv6Address;
			return this;
		}

		public Builder ipv6networkMask(String iv6NetworkMask) {
			this.iv6NetworkMask = iv6NetworkMask;
			return this;
		}

		public Builder mediumType(String mediumType) {
			this.mediumType = mediumType;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		public BridgedIf build() {
			return new BridgedIf(name, guid, dhcp, ipAddress, networkMask,
					ipv6Address, iv6NetworkMask, mediumType, status);
		}
	}

	public BridgedIf(String name, String guid, String dhcp, String ipAddress,
			String networkMask, String ipv6Address, String iv6NetworkMask,
			String mediumType, String status) {
		this.name = checkNotNull(name, "bridgedIf name can't be null");
		this.guid = guid;
		this.dhcp = dhcp;
		this.ipAddress = checkNotNull(ipAddress, "bridgedIf ipAddress can't be null");
		this.networkMask = networkMask;
		this.ipv6Address = ipv6Address;
		this.ipv6NetworkMask = iv6NetworkMask;
		this.mediumType = mediumType;
		this.status = checkNotNull(status, "bridgedIf status can't be null");
	}

	public String getName() {
		return name;
	}

	public String getGuid() {
		return guid;
	}

	public String getDhcp() {
		return dhcp;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getNetworkMask() {
		return networkMask;
	}

	public String getIpv6Address() {
		return ipv6Address;
	}

	public String getIpv6NetworkMask() {
		return ipv6NetworkMask;
	}

	public String getMediumType() {
		return mediumType;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof BridgedIf) {
			BridgedIf other = (BridgedIf) o;
			return Objects.equal(name, other.name)
					&& Objects.equal(dhcp, other.dhcp)
					&& Objects.equal(ipAddress, other.ipAddress)
					&& Objects.equal(networkMask, other.networkMask)
					&& Objects.equal(mediumType, other.mediumType)
					&& Objects.equal(status, other.status);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name, guid, dhcp, ipAddress, networkMask, ipv6Address, ipv6NetworkMask, mediumType, status);
	}

	@Override
	public String toString() {
		return "BridgedIf{" +
				"name=" + name + 
				", dhcp=" + dhcp + 
				", ipAddress=" + ipAddress + 
				", networkMask=" + networkMask + 
				", ipv6Address=" + ipv6Address + 
				", ipv6NetworkMask=" + ipv6NetworkMask + 
				", mediumType=" + mediumType + 				
				", status=" + status + 
				'}';
	}

}
