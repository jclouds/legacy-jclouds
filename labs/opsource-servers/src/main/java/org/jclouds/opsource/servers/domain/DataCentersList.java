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
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.opsource.servers.OpSourceNameSpaces.DATACENTER;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Represents list of data centers for an account
 * @author Kedar Dave
 */
@XmlRootElement(namespace = DATACENTER, name = "DatacentersWithLimits")
public class DataCentersList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

	  Set<DataCenter> dataCenters = Sets.newLinkedHashSet();;
	  
      public Builder dataCenters(Set<DataCenter> dataCenters) {
    	  this.dataCenters = Sets.newLinkedHashSet(checkNotNull(dataCenters, "dataCenters"));
    	  return this;
      }
      
      public Builder dataCenter(DataCenter dataCenter) {
	      this.dataCenters.add(checkNotNull(dataCenter, "dataCenter"));
	      return this;
	  }
      
      public DataCentersList build() {
         return new DataCentersList(dataCenters);
      }

   }

   private DataCentersList() {
      // For JAXB and builder use
   }
   
   @XmlElement(name = "datacenterWithLimits", namespace = DATACENTER)
   private Set<DataCenter> dataCenters = Sets.newLinkedHashSet();

   private DataCentersList(Set<DataCenter> dataCenters) {
	   this.dataCenters = ImmutableSet.copyOf(dataCenters);
   }

	public Set<DataCenter> getDataCenters() {
		return dataCenters;
	}

	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DataCentersList that = DataCentersList.class.cast(o);
		return super.equals(that) && equal(dataCenters, that.dataCenters);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dataCenters);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("dataCenters", dataCenters).toString();
	}

}
