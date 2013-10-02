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

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control how a template is created.
 *
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.8/api/user/createTemplate.html"
 *      />
 * @author Richard Downer
 */
public class CreateTemplateOptions extends BaseHttpRequestOptions {

   public static final CreateTemplateOptions NONE = new CreateTemplateOptions();

   /**
    * 32 or 64 bit
    */
   public CreateTemplateOptions bits(int bits) {
      this.queryParameters.replaceValues("bits", ImmutableSet.of(bits + ""));
      return this;
   }

   /**
    * true if this template is a featured template, false otherwise
    */
   public CreateTemplateOptions isFeatured(boolean isFeatured) {
      this.queryParameters.replaceValues("isfeatured", ImmutableSet.of(isFeatured + ""));
      return this;
   }

   /**
    * true if this template is a public template, false otherwise
    */
   public CreateTemplateOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * true if the template supports the password reset feature; default is false
    */
   public CreateTemplateOptions passwordEnabled(boolean passwordEnabled) {
      this.queryParameters.replaceValues("passwordenabled", ImmutableSet.of(passwordEnabled + ""));
      return this;
   }

   /**
    * true if the template requires HVM, false otherwise
    */
   public CreateTemplateOptions requiresHVM(boolean requiresHVM) {
      this.queryParameters.replaceValues("requireshvm", ImmutableSet.of(requiresHVM + ""));
      return this;
   }

   /**
    * the ID of the snapshot the template is being created from. Either this parameter, or volumeId has to be passed in
    */
   public CreateTemplateOptions snapshotId(String snapshotId) {
      this.queryParameters.replaceValues("snapshotid", ImmutableSet.of(snapshotId + ""));
      return this;
   }

   /**
    * the ID of the disk volume the template is being created from. Either this parameter, or snapshotId has to be passed in
    */
   public CreateTemplateOptions volumeId(String volumeId) {
      this.queryParameters.replaceValues("volumeid", ImmutableSet.of(volumeId + ""));
      return this;
   }
   
   public static class Builder {

      public static CreateTemplateOptions bits(int bits) {
         CreateTemplateOptions options = new CreateTemplateOptions();
         return options.bits(bits);
      }

      public static CreateTemplateOptions isFeatured(boolean isFeatured) {
         CreateTemplateOptions options = new CreateTemplateOptions();
         return options.isFeatured(isFeatured);
      }

      public static CreateTemplateOptions isPublic(boolean isPublic) {
         CreateTemplateOptions options = new CreateTemplateOptions();
         return options.isPublic(isPublic);
      }

      public static CreateTemplateOptions passwordEnabled(boolean passwordEnabled) {
         CreateTemplateOptions options = new CreateTemplateOptions();
         return options.passwordEnabled(passwordEnabled);
      }

      public static CreateTemplateOptions requiresHVM(boolean requiresHVM) {
         CreateTemplateOptions options = new CreateTemplateOptions();
         return options.requiresHVM(requiresHVM);
      }

      public static CreateTemplateOptions snapshotId(String snapshotId) {
         CreateTemplateOptions options = new CreateTemplateOptions();
         return options.snapshotId(snapshotId);
      }

      public static CreateTemplateOptions volumeId(String volumeId) {
         CreateTemplateOptions options = new CreateTemplateOptions();
         return options.volumeId(volumeId);
      }
   }
}
