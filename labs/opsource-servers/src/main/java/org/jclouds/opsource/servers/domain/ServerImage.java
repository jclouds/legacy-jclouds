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
import static org.jclouds.opsource.servers.OpSourceNameSpaces.SERVER;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * 
 * @author Kedar Dave
 */
@XmlRootElement(namespace = SERVER, name = "ServerImage")
public class ServerImage {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromServerImage(this);
   }

   public static class Builder {

	  private String id;
	  private String resourcePath;
	  private String name;
	  private String description;
	  private OperatingSystem operatingSystem;
	  private String location;
	  private int cpuCount;
	  private long memory;
	  private long osStorage;
	  private long additionalLocalStorage;
	  
      public Builder id(String id) {
    	  this.id = id;
    	  return this;
      }

      public Builder resourcePath(String resourcePath) {
    	  this.resourcePath = resourcePath;
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

      public Builder operatingSystem(OperatingSystem operatingSystem) {
    	  this.operatingSystem = operatingSystem;
    	  return this;
      }
      
      public Builder location(String location) {
    	  this.location = location;
    	  return this;
      }
      
      public Builder cpuCount(int cpuCount) {
    	  this.cpuCount = cpuCount;
    	  return this;
      }

      public Builder memory(long memory) {
    	  this.memory = memory;
    	  return this;
      }
      
      public Builder osStorage(long osStorage) {
    	  this.osStorage = osStorage;
    	  return this;
      }
      
      public Builder additionalLocalStorage(long additionalLocalStorage) {
    	  this.additionalLocalStorage = additionalLocalStorage;
    	  return this;
      }

      public ServerImage build() {
         return new ServerImage(id, resourcePath, name, description, operatingSystem, location, cpuCount, 
        		 memory, osStorage, additionalLocalStorage);
      }

      public Builder fromServerImage(ServerImage in) {
         return new Builder().id(id).resourcePath(resourcePath).name(name).description(description)
         	.operatingSystem(operatingSystem).location(location).cpuCount(cpuCount).memory(memory)
         	.osStorage(osStorage).additionalLocalStorage(additionalLocalStorage);
      }
   }

   private ServerImage() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = SERVER, name="id")
   private String id;
   @XmlElement(namespace = SERVER, name="resourcePath")
   private String resourcePath;
   @XmlElement(namespace = SERVER, name="name")
   private String name;
   @XmlElement(namespace = SERVER, name="description")
   private String description;
   @XmlElement(namespace = SERVER, name="operatingSystem")
   private OperatingSystem operatingSystem;
   @XmlElement(namespace = SERVER, name="location")
   private String location;
   @XmlElement(namespace = SERVER, name="cpuCount")
   private long cpuCount;
   @XmlElement(namespace = SERVER, name="memory")
   private long memory;
   @XmlElement(namespace = SERVER, name="osStorage")
   private long osStorage;
   @XmlElement(namespace = SERVER, name="additionalLocalStorage")
   private long additionalLocalStorage;

   private ServerImage(String id, String resourcePath, String name, String description, OperatingSystem operatingSystem,
		   String location, long cpuCount, long memory, long osStorage, long additionalLocalStorage) {
      this.id = id;
      this.resourcePath = resourcePath;
      this.name = name;
      this.description = description;
      this.operatingSystem = operatingSystem;
      this.location = location;
      this.cpuCount = cpuCount;
      this.memory = memory;
      this.osStorage = osStorage;
      this.additionalLocalStorage = additionalLocalStorage;
   }

   	public String getId() {
   		return id;
   	}

	public String getResourcePath() {
		return resourcePath;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}
	
	public String getLocation() {
		return location;
	}
	
	public long getCpuCount() {
		return cpuCount;
	}
	
	public long getMemory() {
		return memory;
	}
	
	public long getOsStorage() {
		return osStorage;
	}
	
	public long getAdditionalLocalStorage() {
		return additionalLocalStorage;
	}

	@Override
   	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ServerImage that = ServerImage.class.cast(o);
		return equal(id, that.id);
	}

	@Override
	public int hashCode() {
      	return Objects.hashCode(id, resourcePath, name, description, operatingSystem, location, 
      			cpuCount, memory, osStorage, additionalLocalStorage);
	}

	@Override
   	public String toString() {
		return Objects.toStringHelper("").add("id", id).add("resourcePath", resourcePath).add("name", name).
			add("description", description).add("operatingSystem", operatingSystem).add("location", location).
			add("cpuCount", cpuCount).add("memory", memory).add("osStorage", osStorage).
			add("additionalLocalStorage", additionalLocalStorage).toString();
	}

}
