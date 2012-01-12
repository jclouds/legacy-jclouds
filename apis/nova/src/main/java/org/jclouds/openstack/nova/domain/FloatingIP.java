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
package org.jclouds.openstack.nova.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Check <a href="http://wiki.openstack.org/os_api_floating_ip">Floating IP Wiki page</a>
 * 
 * @author chamerling
 *
 */
public class FloatingIP extends Resource {
	
	private int id;
	
	private String ip;
	
	@SerializedName(value="fixed_ip")
	private String fixedIP;
	
	@SerializedName(value = "instance_id")
	private int instanceID;

	@SuppressWarnings("unused")
	private FloatingIP() {
	}

	public FloatingIP(int id, String ip, String fixedIP, int instanceID) {
		this.id = id;
		this.ip = ip;
		this.fixedIP = fixedIP;
		this.instanceID = instanceID;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the fixedIP
	 */
	public String getFixedIP() {
		return fixedIP;
	}

	/**
	 * @param fixedIP the fixedIP to set
	 */
	public void setFixedIP(String fixedIP) {
		this.fixedIP = fixedIP;
	}

	/**
	 * @return the instanceID
	 */
	public int getInstanceID() {
		return instanceID;
	}

	/**
	 * @param instanceID the instanceID to set
	 */
	public void setInstanceID(int instanceID) {
		this.instanceID = instanceID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FloatingIP [id=");
		builder.append(id);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", fixedIP=");
		builder.append(fixedIP);
		builder.append(", instanceID=");
		builder.append(instanceID);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fixedIP == null) ? 0 : fixedIP.hashCode());
		result = prime * result + id;
		result = prime * result + instanceID;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloatingIP other = (FloatingIP) obj;
		if (fixedIP == null) {
			if (other.fixedIP != null)
				return false;
		} else if (!fixedIP.equals(other.fixedIP))
			return false;
		if (id != other.id)
			return false;
		if (instanceID != other.instanceID)
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}

}
