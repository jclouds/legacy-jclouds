/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.config;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.config.LicenseDto;

/**
 * Adds high level functionality to {@link LicenseDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@EnterpriseEdition
public class License extends DomainWrapper<LicenseDto> {
   /**
    * Constructor to be used only by the builder.
    */
   protected License(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final LicenseDto target) {
      super(context, target);
   }

   // Domain operations

   public void remove() {
      context.getApi().getConfigApi().removeLicense(target);
      target = null;
   }

   public void add() {
      target = context.getApi().getConfigApi().addLicense(target);
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final String code) {
      return new Builder(context, code);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private String code;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final String code) {
         super();
         this.context = context;
         this.code = code;
      }

      public Builder code(final String code) {
         this.code = code;
         return this;
      }

      public License build() {
         LicenseDto dto = new LicenseDto();
         dto.setCode(code);

         License license = new License(context, dto);
         return license;
      }

      public static Builder fromLicense(final License in) {
         return License.builder(in.context, in.getCode());
      }
   }

   // Delegate methods

   public String getCode() {
      return target.getCode();
   }

   public String getCustomerId() {
      return target.getCustomerid();
   }

   public String getEnabledIp() {
      return target.getEnabledip();
   }

   public String getExpiration() {
      return target.getExpiration();
   }

   public Integer getId() {
      return target.getId();
   }

   public Integer getNumCores() {
      return target.getNumcores();
   }

   @Override
   public String toString() {
      return "License [id=" + getId() + ", code=" + getCode() + ", customerId=" + getCustomerId() + ", enabledIp="
            + getEnabledIp() + ", expiration=" + getExpiration() + ", numCores=" + getNumCores() + "]";
   }

}
