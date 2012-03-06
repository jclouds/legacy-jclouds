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
 * Specifies router information.
 *
 * @author danikov
 */
@XmlRootElement(name = "RouterInfo")
public class RouterInfo {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromRouterInfo(this);
   }

   public static class Builder {

      private String externalIp;

      /**
       * @see RouterInfo#getExternalIp()
       */
      public Builder externalIp(String externalIp) {
         this.externalIp = externalIp;
         return this;
      }

      public RouterInfo build() {
         return new RouterInfo(externalIp);
      }

      public Builder fromRouterInfo(RouterInfo in) {
         return externalIp(in.getExternalIp());
      }
   }

   private RouterInfo() {
      // for JAXB
   }

   private RouterInfo(String externalIp) {
      this.externalIp = externalIp;
   }


   @XmlElement(name = "ExternalIp")
   private String externalIp;

   /**
    * @return the external IP of the router. Applicable for NAT Routed / Fenced networks only.
    */
   public String getExternalIp() {
      return externalIp;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      RouterInfo that = RouterInfo.class.cast(o);
      return equal(externalIp, that.externalIp);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(externalIp, externalIp);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("externalIp", externalIp).toString();
   }

}
