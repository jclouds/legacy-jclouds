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

import static org.jclouds.opsource.servers.OpSourceNameSpaces.SERVER;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * 
 * @author Kedar Dave
 */
@XmlRootElement(namespace = SERVER, name = "PendingDeployServer")
public class PendingDeployServer extends BaseServer{
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromPendingDeployServer(this);
   }

   public static class Builder {

	  private String id;
	  private String name;
	  private String description;
	  private MachineSpecification machineSpecification;
	  private String sourceImageId;
	  private String networkId;
	  private String privateIpAddress;
	  private Status status;
	  
      public Builder id(String id) {
    	  this.id = id;
    	  return this;
      }

      public Builder name(String name) {
    	  this.name = name;
    	  return this;
      }

      public Builder description(String description) {
    	  this.description = description;
    	  return this;
      }
      
      public Builder machineSpecification(MachineSpecification machineSpecification) {
    	  this.machineSpecification = machineSpecification;
    	  return this;
      }
      
      public Builder sourceImageId(String sourceImageId) {
    	  this.sourceImageId = sourceImageId;
    	  return this;
      }
      
      public Builder networkId(String networkId) {
    	  this.networkId = networkId;
    	  return this;
      }
      
      public Builder privateIpAddress(String privateIpAddress) {
    	  this.privateIpAddress = privateIpAddress;
    	  return this;
      }

      public Builder status(Status status) {
    	  this.status = status;
    	  return this;
      }

      public PendingDeployServer build() {
         return new PendingDeployServer(id, name, description, machineSpecification, sourceImageId, networkId,
        		 privateIpAddress, status);
      }

      public Builder fromPendingDeployServer(PendingDeployServer in) {
         return new Builder().id(id).name(name).description(description).machineSpecification(machineSpecification)
         	.sourceImageId(sourceImageId).networkId(networkId).privateIpAddress(privateIpAddress).status(status);
      }
   }

   private PendingDeployServer() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = SERVER, name="id")
   private String id;
   @XmlElement(namespace = SERVER, name="name")
   private String name;
   @XmlElement(namespace = SERVER, name="description")
   private String description;
   @XmlElement(namespace = SERVER, name="machineSpecification")
   private MachineSpecification machineSpecification;
   @XmlElement(namespace = SERVER, name="sourceImageId")
   private String sourceImageId;
   @XmlElement(namespace = SERVER, name="networkId")
   private String networkId;
   @XmlElement(namespace = SERVER, name="privateIpAddress")
   private String privateIpAddress;
   @XmlElement(namespace = SERVER, name="status")
   private Status status;

   private PendingDeployServer(String id, String name, String description, MachineSpecification machineSpecification, String sourceImageId,
		   String networkId, String privateIpAddress, Status status) {
	  super(id, name, description);
      this.machineSpecification = machineSpecification;
      this.sourceImageId = sourceImageId;
      this.networkId = networkId;
      this.privateIpAddress = privateIpAddress;
      this.status = status;
   }

	public MachineSpecification getMachineSpecification() {
		return machineSpecification;
	}
	
	public String getSourceImageId() {
		return sourceImageId;
	}
	
	public String getNetworkId() {
		return networkId;
	}
	
	public String getPrivateIpAddress() {
		return privateIpAddress;
	}
	
	public Status getStatus() {
		return status;
	}

	@Override
   	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
      	return super.hashCode() + Objects.hashCode(id, name, description, machineSpecification, sourceImageId, networkId, 
      			privateIpAddress, status);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("id", id).add("name", name).add("description", description).
			add("machineSpecification", machineSpecification).add("sourceImageId", sourceImageId).add("networkId", networkId).
			add("privateIpAddress", privateIpAddress).add("status", status).toString();
	}

}
