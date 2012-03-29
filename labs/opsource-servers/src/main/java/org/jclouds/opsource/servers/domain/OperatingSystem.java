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
 * Holds operating system information for {@link ServerImage}
 * @author Kedar Dave
 */
@XmlRootElement(name = "operatingSystem", namespace = SERVER)
public class OperatingSystem {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromOperatingSystem(this);
   }

   public static class Builder {

      private String osType;
      private String displayName;

      public Builder osType(String osType) {
         this.osType = osType;
         return this;
      }
      
      public Builder displayName(String displayName) {
          this.displayName = displayName;
          return this;
       }

      public OperatingSystem build() {
         return new OperatingSystem(osType, displayName);
      }

      public Builder fromOperatingSystem(OperatingSystem in) {
         return new Builder().osType(in.getOsType()).displayName(in.getDisplayName());
      }
   }

   private OperatingSystem() {
      // For JAXB and builder use
   }

   @XmlElement(namespace = SERVER, name="type")
   private String osType;
   @XmlElement(namespace = SERVER, name="displayName")
   private String displayName;

   private OperatingSystem(String osType, String displayName) {
      this.osType = osType;
      this.displayName = displayName;
   }

   public String getOsType() {
      return osType;
   }
   
   public String getDisplayName() {
	  return displayName;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      OperatingSystem that = OperatingSystem.class.cast(o);
      return equal(osType, that.osType) && equal(displayName, that.displayName);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(osType, displayName);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("osType", osType).add("displayName", displayName).toString();
   }

}
