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
 * Represents set of OS Server Images from which servers may be deployed
 * @author Kedar Dave
 */
@XmlRootElement(namespace = SERVER, name = "ServerImages")
public class ServerImagesList {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

	  Set<ServerImage> serverImages = Sets.newLinkedHashSet();;
	  
      public Builder dataCenters(Set<ServerImage> serverImages) {
    	  this.serverImages = Sets.newLinkedHashSet(checkNotNull(serverImages, "serverImages"));
    	  return this;
      }
      
      public Builder serverImage(ServerImage serverImage) {
	      this.serverImages.add(checkNotNull(serverImage, "serverImage"));
	      return this;
	  }
      
      public ServerImagesList build() {
         return new ServerImagesList(serverImages);
      }

   }

   private ServerImagesList() {
      // For JAXB and builder use
   }
   
   @XmlElement(name = "ServerImage", namespace = SERVER)
   private Set<ServerImage> serverImages = Sets.newLinkedHashSet();

   private ServerImagesList(Set<ServerImage> serverImages) {
	   this.serverImages = ImmutableSet.copyOf(serverImages);
   }

	public Set<ServerImage> getServerImages() {
		return serverImages;
	}

	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ServerImagesList that = ServerImagesList.class.cast(o);
		return super.equals(that) && equal(serverImages, that.serverImages);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(serverImages);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("serverImages", serverImages).toString();
	}

}
