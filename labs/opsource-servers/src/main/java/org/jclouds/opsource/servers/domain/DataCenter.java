/*
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
package org.jclouds.opsource.servers.domain;

import static com.google.common.base.Objects.equal;
import static org.jclouds.opsource.servers.OpSourceNameSpaces.DATACENTER;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * 
 * @author Kedar Dave
 */
@XmlRootElement(namespace = DATACENTER, name = "datacenterWithLimits")
public class DataCenter {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromDataCenter(this);
   }

   public static class Builder {

	  private String location;
	  private String displayName;
	  private String city;
	  private String state;
	  private String country;
	  private String vpnUrl;
	  private boolean isDefault;
	  private int maxCpu;
	  private long maxRamMb;
	  
      /**
       * @see DataCenter#getOrgId()
       */
      public Builder location(String location) {
    	  this.location = location;
    	  return this;
      }

      public Builder displayName(String displayName) {
    	  this.displayName = displayName;
    	  return this;
      }

      public Builder city(String city) {
    	  this.city = city;
    	  return this;
      }

      public Builder state(String state) {
    	  this.state = state;
    	  return this;
      }

      public Builder country(String country) {
    	  this.country = country;
    	  return this;
      }

      public Builder vpnUrl(String vpnUrl) {
    	  this.vpnUrl = vpnUrl;
    	  return this;
      }

      public Builder isDefault(boolean isDefault) {
    	  this.isDefault = isDefault;
    	  return this;
      }

      public Builder maxCpu(int maxCpu) {
    	  this.maxCpu = maxCpu;
    	  return this;
      }

      public Builder maxRamMb(long maxRamMb) {
    	  this.maxRamMb = maxRamMb;
    	  return this;
      }

      public DataCenter build() {
         return new DataCenter(location, displayName, city, state, country, vpnUrl, isDefault, maxCpu, maxRamMb);
      }

      public Builder fromDataCenter(DataCenter in) {
         return new Builder().location(location).displayName(displayName).city(city).state(state)
         	.country(country).vpnUrl(vpnUrl).isDefault(isDefault).maxCpu(maxCpu).maxRamMb(maxRamMb);
      }
   }

   private DataCenter() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = DATACENTER, name="location")
   private String location;
   @XmlElement(namespace = DATACENTER, name="displayName")
   private String displayName;
   @XmlElement(namespace = DATACENTER, name="city")
   private String city;
   @XmlElement(namespace = DATACENTER, name="state")
   private String state;
   @XmlElement(namespace = DATACENTER, name="country")
   private String country;
   @XmlElement(namespace = DATACENTER, name="vpnUrl")
   private String vpnUrl;
   @XmlElement(namespace = DATACENTER, name="isDefault")
   private boolean isDefault;
   @XmlElement(namespace = DATACENTER, name="maxCpu")
   private long maxCpu;
   @XmlElement(namespace = DATACENTER, name="maxRamMb")
   private long maxRamMb;

   private DataCenter(String location, String displayName, String city, String state, String country,
		   String vpnUrl, boolean isDefault, long maxCpu, long maxRamMb) {
      this.location = location;
      this.displayName = displayName;
      this.city = city;
      this.state = state;
      this.country = country;
      this.vpnUrl = vpnUrl;
      this.isDefault = isDefault;
      this.maxCpu = maxCpu;
      this.maxRamMb = maxRamMb;
   }

   	public String getLocation() {
   		return location;
   	}

	public String getDisplayName() {
		return displayName;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getState() {
		return state;
	}
	
	public String getCountry() {
		return country;
	}
	
	/**
	 * VPN URL corresponding to this data center's location
	 * @return
	 */
	public String getVpnUrl() {
		return vpnUrl;
	}
	
	public boolean isDefault() {
		return isDefault;
	}
	
	/**
	 * defines the upper ceiling for the
	 * number of CPUs permitted respectively for Customer Servers deployed at this data
	 * center.
	 * @return
	 */
	public long getMaxCpu() {
		return maxCpu;
	}
	
	/**
	 * defines the upper ceiling for the
	 * quantity of RAM permitted respectively for Customer Servers deployed at this data
	 * center.
	 * @return
	 */
	public long getMaxRamMb() {
		return maxRamMb;
	}

	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DataCenter that = DataCenter.class.cast(o);
		return equal(location, that.location);
	}

	@Override
	public int hashCode() {
      	return Objects.hashCode(location);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("location", location).add("displayName", displayName).add("city", city).
			add("state", state).add("country", country).add("vpnUrl", vpnUrl).add("isDefault", isDefault).
			add("maxCpu", maxCpu).add("maxRamMb", maxRamMb).toString();
	}

}
