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
import static org.jclouds.opsource.servers.OpSourceNameSpaces.SERVER;

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
@XmlRootElement(namespace = SERVER, name = "DeployedServers")
public class DeployedServersList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

	  Set<DeployedServer> deployedServers = Sets.newLinkedHashSet();;
	  
      public Builder deployedServers(Set<DeployedServer> deployedServers) {
    	  this.deployedServers = Sets.newLinkedHashSet(checkNotNull(deployedServers, "deployedServers"));
    	  return this;
      }
      
      public Builder deployedServer(DeployedServer deployedServer) {
	      this.deployedServers.add(checkNotNull(deployedServer, "deployedServer"));
	      return this;
	  }
      
      public DeployedServersList build() {
         return new DeployedServersList(deployedServers);
      }

   }

   private DeployedServersList() {
      // For JAXB and builder use
   }
   
   @XmlElement(name = "DeployedServer", namespace = SERVER)
   private Set<DeployedServer> deployedServers = Sets.newLinkedHashSet();

   private DeployedServersList(Set<DeployedServer> deployedServers) {
	   this.deployedServers = ImmutableSet.copyOf(deployedServers);
   }

	public Set<DeployedServer> getDeployedServers() {
		return deployedServers;
	}

	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DeployedServersList that = DeployedServersList.class.cast(o);
		return super.equals(that) && equal(deployedServers, that.deployedServers);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(deployedServers);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("deployedServers", deployedServers).toString();
	}

}
