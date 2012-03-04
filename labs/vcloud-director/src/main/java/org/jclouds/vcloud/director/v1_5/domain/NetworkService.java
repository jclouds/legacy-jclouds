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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * Represents a network service
 *
 * @author danikov
 */
@XmlRootElement(name = "NetworkService")
public class NetworkService {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromNetworkService(this);
   }

   public static class Builder {

      private boolean isEnabled;

      /**
       * @see NetworkService#isEnabled()
       */
      public Builder enabled(boolean isEnabled) {
         this.isEnabled = isEnabled;
         return this;
      }

      public NetworkService build() {
         return new NetworkService(isEnabled);
      }

      public Builder fromNetworkService(NetworkService in) {
         return enabled(in.isEnabled());
      }
   }

   private NetworkService(boolean enabled) {
      isEnabled = enabled;
   }

   private NetworkService() {
      // For JAXB and builder use
   }

   @XmlElement(name = "IsEnabled")
   private boolean isEnabled;

   /**
    * @return Enable or disable the service using this flag
    */
   public boolean isEnabled() {
      return isEnabled;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      NetworkService that = NetworkService.class.cast(o);
      return equal(isEnabled, that.isEnabled);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(isEnabled);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("isEnabled", isEnabled).toString();
   }
}
