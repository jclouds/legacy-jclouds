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

import org.jclouds.cloudstack.domain.Template;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control how a template should be updated.
 *
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.8/api/user/updateTemplate.html"
 *      />
 * @author Richard Downer
 */
public class UpdateTemplateOptions extends BaseHttpRequestOptions {

   /**
    * true if image is bootable, false otherwise
    */
   public UpdateTemplateOptions bootable(boolean bootable) {
      this.queryParameters.replaceValues("bootable", ImmutableSet.of(bootable + ""));
      return this;
   }

   /**
    * the display text of the image
    */
   public UpdateTemplateOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.of(displayText));
      return this;
   }

   /**
    * the format for the image
    */
   public UpdateTemplateOptions format(Template.Format format) {
      this.queryParameters.replaceValues("format", ImmutableSet.of(format + ""));
      return this;
   }

   /**
    * the name of the image file
    */
   public UpdateTemplateOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * the ID of the OS type that best represents the OS of this image.
    */
   public UpdateTemplateOptions osTypeId(String osTypeId) {
      this.queryParameters.replaceValues("ostypeid", ImmutableSet.of(osTypeId + ""));
      return this;
   }

   /**
    * true if the image supports the password reset feature; default is false
    */
   public UpdateTemplateOptions passwordEnabled(boolean passwordEnabled) {
      this.queryParameters.replaceValues("passwordenabled", ImmutableSet.of(passwordEnabled + ""));
      return this;
   }

   public static class Builder {

      public static UpdateTemplateOptions bootable(boolean bootable) {
         UpdateTemplateOptions options = new UpdateTemplateOptions();
         return options.bootable(bootable);
      }

      public static UpdateTemplateOptions displayText(String displayText) {
         UpdateTemplateOptions options = new UpdateTemplateOptions();
         return options.displayText(displayText);
      }

      public static UpdateTemplateOptions format(Template.Format format) {
         UpdateTemplateOptions options = new UpdateTemplateOptions();
         return options.format(format);
      }

      public static UpdateTemplateOptions name(String name) {
         UpdateTemplateOptions options = new UpdateTemplateOptions();
         return options.name(name);
      }

      public static UpdateTemplateOptions osTypeId(String osTypeId) {
         UpdateTemplateOptions options = new UpdateTemplateOptions();
         return options.osTypeId(osTypeId);
      }

      public static UpdateTemplateOptions passwordEnabled(boolean passwordEnabled) {
         UpdateTemplateOptions options = new UpdateTemplateOptions();
         return options.passwordEnabled(passwordEnabled);
      }
   }
   
}
