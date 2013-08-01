/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the ISO registerISO method.
 *
 * @see org.jclouds.cloudstack.features.ISOApi#registerISO
 * @see org.jclouds.cloudstack.features.ISOApi#registerISO
 * @author Richard Downer
 */
public class RegisterISOOptions extends AccountInDomainOptions {

   public static final RegisterISOOptions NONE = new RegisterISOOptions();

   /**
    * @param bootable true if this ISO is bootable
    */
   public RegisterISOOptions bootable(boolean bootable) {
      this.queryParameters.replaceValues("bootable", ImmutableSet.of(bootable + ""));
      return this;
   }

   /**
    * @param isExtractable true if the iso or its derivatives are extractable; default is false
    */
   public RegisterISOOptions isExtractable(boolean isExtractable) {
      this.queryParameters.replaceValues("isextractable", ImmutableSet.of(isExtractable + ""));
      return this;
   }

   /**
    * @param isFeatured true if you want this ISO to be featured
    */
   public RegisterISOOptions isFeatured(boolean isFeatured) {
      this.queryParameters.replaceValues("isfeatured", ImmutableSet.of(isFeatured + ""));
      return this;
   }

   /**
    * @param isPublic true if you want to register the ISO to be publicly available to all users, false otherwise.
    */
   public RegisterISOOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * @param osTypeId the ID of the OS Type that best represents the OS of this ISO
    */
   public RegisterISOOptions osTypeId(String osTypeId) {
      this.queryParameters.replaceValues("ostypeid", ImmutableSet.of(osTypeId + ""));
      return this;
   }

   /**
    * @param projectId the project this ISO will be in.
    */
   public RegisterISOOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account an optional account name. Must be used with domainId.
       */
      public static RegisterISOOptions accountInDomain(String account, String domainId) {
         return (RegisterISOOptions) new RegisterISOOptions().accountInDomain(account, domainId);
      }

      /**
       * @param bootable true if this ISO is bootable
       */
      public static RegisterISOOptions bootable(boolean bootable) {
         return new RegisterISOOptions().bootable(bootable);
      }

      /**
       * @param domainId an optional domainId. If the account parameter is used, domainId must also be used.
       */
      public static RegisterISOOptions domainId(String domainId) {
         return (RegisterISOOptions) new RegisterISOOptions().domainId(domainId);
      }

      /**
       * @param isExtractable true if the iso or its derivatives are extractable; default is false
       */
      public static RegisterISOOptions isExtractable(boolean isExtractable) {
         return new RegisterISOOptions().isExtractable(isExtractable);
      }

      /**
       * @param isFeatured true if you want this ISO to be featured
       */
      public static RegisterISOOptions isFeatured(boolean isFeatured) {
         return new RegisterISOOptions().isFeatured(isFeatured);
      }

      /**
       * @param isPublic true if you want to register the ISO to be publicly available to all users, false otherwise.
       */
      public static RegisterISOOptions isPublic(boolean isPublic) {
         return new RegisterISOOptions().isPublic(isPublic);
      }

      /**
       * @param osTypeId the ID of the OS Type that best represents the OS of this ISO
       */
      public static RegisterISOOptions osTypeId(String osTypeId) {
         return new RegisterISOOptions().osTypeId(osTypeId);
      }

      /**
       * @param projectId the project this ISO will be in.
       */
      public static RegisterISOOptions projectId(String projectId) {
         return new RegisterISOOptions().projectId(projectId);
      }
   }

}
