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
@XmlRootElement(namespace = SERVER, name = "PendingDeployServers")
public class PendingDeployServersList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

	  Set<PendingDeployServer> pendingDeployServers = Sets.newLinkedHashSet();;
	  
      public Builder pendingDeployServers(Set<PendingDeployServer> pendingDeployServers) {
    	  this.pendingDeployServers = Sets.newLinkedHashSet(checkNotNull(pendingDeployServers, "pendingDeployServers"));
    	  return this;
      }
      
      public Builder pendingDeployServer(PendingDeployServer pendingDeployServer) {
	      this.pendingDeployServers.add(checkNotNull(pendingDeployServer, "pendingDeployServer"));
	      return this;
	  }
      
      public PendingDeployServersList build() {
         return new PendingDeployServersList(pendingDeployServers);
      }

   }

   private PendingDeployServersList() {
      // For JAXB and builder use
   }
   
   @XmlElement(name = "PendingDeployServer", namespace = SERVER)
   private Set<PendingDeployServer> pendingDeployServers = Sets.newLinkedHashSet();

   private PendingDeployServersList(Set<PendingDeployServer> pendingDeployServers) {
	   this.pendingDeployServers = ImmutableSet.copyOf(pendingDeployServers);
   }

	public Set<PendingDeployServer> getPendingDeployServers() {
		return pendingDeployServers;
	}

	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PendingDeployServersList that = PendingDeployServersList.class.cast(o);
		return super.equals(that) && equal(pendingDeployServers, that.pendingDeployServers);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pendingDeployServers);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("pendingDeployServers", pendingDeployServers).toString();
	}

}
