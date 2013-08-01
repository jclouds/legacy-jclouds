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
 * Options used to control how a template is registered.
 *
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.8/api/user/registerTemplate.html"
 *      />
 * @author Richard Downer
 */
public class RegisterTemplateOptions extends AccountInDomainOptions {

   public static final RegisterTemplateOptions NONE = new RegisterTemplateOptions();

   /**
    * 32 or 64 bits support. 64 by default
    */
   public RegisterTemplateOptions bits(int bits) {
      this.queryParameters.replaceValues("bits", ImmutableSet.of(bits + ""));
      return this;
   }

   /**
    * the MD5 checksum value of this template
    */
   public RegisterTemplateOptions checksum(String checksum) {
      this.queryParameters.replaceValues("checksum", ImmutableSet.of(checksum));
      return this;
   }

   /**
    * the project for this template.
    */
   public RegisterTemplateOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   /**
    * true if the template or its derivatives are extractable; default is true
    */
   public RegisterTemplateOptions isExtractable(boolean isExtractable) {
      this.queryParameters.replaceValues("isextractable", ImmutableSet.of(isExtractable + ""));
      return this;
   }

   /**
    * true if this template is a featured template, false otherwise
    */
   public RegisterTemplateOptions isFeatured(boolean isFeatured) {
      this.queryParameters.replaceValues("isfeatured", ImmutableSet.of(isFeatured + ""));
      return this;
   }

   /**
    * true if the template is available to all accounts; default is true
    */
   public RegisterTemplateOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * true if the template supports the password reset feature; default is false
    */
   public RegisterTemplateOptions passwordEnabled(boolean passwordEnabled) {
      this.queryParameters.replaceValues("passwordenabled", ImmutableSet.of(passwordEnabled + ""));
      return this;
   }

   /**
    * true if this template requires HVM
    */
   public RegisterTemplateOptions requiresHVM(boolean requiresHVM) {
      this.queryParameters.replaceValues("requireshvm", ImmutableSet.of(requiresHVM + ""));
      return this;
   }

   public static class Builder {

      public static RegisterTemplateOptions bits(int bits) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.bits(bits);
      }

      public static RegisterTemplateOptions checksum(String checksum) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.checksum(checksum);
      }

      public static RegisterTemplateOptions projectId(String projectId) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.projectId(projectId);
      }

      public static RegisterTemplateOptions isExtractable(boolean isExtractable) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.isExtractable(isExtractable);
      }

      public static RegisterTemplateOptions isFeatured(boolean isFeatured) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.isFeatured(isFeatured);
      }

      public static RegisterTemplateOptions isPublic(boolean isPublic) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.isPublic(isPublic);
      }

      public static RegisterTemplateOptions passwordEnabled(boolean passwordEnabled) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.passwordEnabled(passwordEnabled);
      }

      public static RegisterTemplateOptions requiresHVM(boolean requiresHVM) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return options.requiresHVM(requiresHVM);
      }

      /**
       * @see AccountInDomainOptions#accountInDomain
       */
      public static RegisterTemplateOptions accountInDomain(String account, String domain) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return (RegisterTemplateOptions)options.accountInDomain(account, domain);
      }

      /**
       * @see AccountInDomainOptions#domainId
       */
      public static RegisterTemplateOptions domainId(String domainId) {
         RegisterTemplateOptions options = new RegisterTemplateOptions();
         return (RegisterTemplateOptions)options.domainId(domainId);
      }
   }
   
}
