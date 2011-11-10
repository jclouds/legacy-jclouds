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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for the Iso registerIso method.
 *
 * @see org.jclouds.cloudstack.features.IsoClient#registerIso
 * @see org.jclouds.cloudstack.features.IsoAsyncClient#registerIso
 * @author Richard Downer
 */
public class RegisterIsoOptions extends AccountInDomainOptions {

   public static final RegisterIsoOptions NONE = new RegisterIsoOptions(); 

   /**
    * @param bootable true if this ISO is bootable
    */
   public RegisterIsoOptions bootable(boolean bootable) {
      this.queryParameters.replaceValues("bootable", ImmutableSet.of(bootable + ""));
      return this;
   }

   /**
    * @param isExtractable true if the iso or its derivatives are extractable; default is false
    */
   public RegisterIsoOptions isExtractable(boolean isExtractable) {
      this.queryParameters.replaceValues("isextractable", ImmutableSet.of(isExtractable + ""));
      return this;
   }

   /**
    * @param isFeatured true if you want this ISO to be featured
    */
   public RegisterIsoOptions isFeatured(boolean isFeatured) {
      this.queryParameters.replaceValues("isfeatured", ImmutableSet.of(isFeatured + ""));
      return this;
   }

   /**
    * @param isPublic true if you want to register the ISO to be publicly available to all users, false otherwise.
    */
   public RegisterIsoOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * @param osTypeId the ID of the OS Type that best represents the OS of this ISO
    */
   public RegisterIsoOptions osTypeId(long osTypeId) {
      this.queryParameters.replaceValues("ostypeid", ImmutableSet.of(osTypeId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account an optional account name. Must be used with domainId.
       */
      public static RegisterIsoOptions accountInDomain(String account, long domainId) {
         return (RegisterIsoOptions) new RegisterIsoOptions().accountInDomain(account, domainId);
      }

      /**
       * @param bootable true if this ISO is bootable
       */
      public static RegisterIsoOptions bootable(boolean bootable) {
         return new RegisterIsoOptions().bootable(bootable);
      }

      /**
       * @param domainId an optional domainId. If the account parameter is used, domainId must also be used.
       */
      public static RegisterIsoOptions domainId(long domainId) {
         return (RegisterIsoOptions) new RegisterIsoOptions().domainId(domainId);
      }

      /**
       * @param isExtractable true if the iso or its derivatives are extractable; default is false
       */
      public static RegisterIsoOptions isExtractable(boolean isExtractable) {
         return new RegisterIsoOptions().isExtractable(isExtractable);
      }

      /**
       * @param isFeatured true if you want this ISO to be featured
       */
      public static RegisterIsoOptions isFeatured(boolean isFeatured) {
         return new RegisterIsoOptions().isFeatured(isFeatured);
      }

      /**
       * @param isPublic true if you want to register the ISO to be publicly available to all users, false otherwise.
       */
      public static RegisterIsoOptions isPublic(boolean isPublic) {
         return new RegisterIsoOptions().isPublic(isPublic);
      }

      /**
       * @param osTypeId the ID of the OS Type that best represents the OS of this ISO
       */
      public static RegisterIsoOptions osTypeId(long osTypeId) {
         return new RegisterIsoOptions().osTypeId(osTypeId);
      }
   }

}
