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
 * Options for the ISO updateISO method.
 *
 * @see org.jclouds.cloudstack.features.ISOClient#updateISO
 * @see org.jclouds.cloudstack.features.ISOAsyncClient#updateISO
 * @author Richard Downer
 */
public class UpdateISOOptions extends BaseHttpRequestOptions {

   public static final UpdateISOOptions NONE = new UpdateISOOptions();

   /**
    * @param bootable true if image is bootable, false otherwise
    */
   public UpdateISOOptions bootable(boolean bootable) {
      this.queryParameters.replaceValues("bootable", ImmutableSet.of(bootable + ""));
      return this;
   }

   /**
    * @param displayText the display text of the image
    */
   public UpdateISOOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.of(displayText + ""));
      return this;
   }

   /**
    * @param format the format for the image
    */
   public UpdateISOOptions format(String format) {
      this.queryParameters.replaceValues("format", ImmutableSet.of(format + ""));
      return this;
   }

   /**
    * @param name the name of the image file
    */
   public UpdateISOOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name + ""));
      return this;
   }

   /**
    * @param osTypeId the ID of the OS type that best represents the OS of this image.
    */
   public UpdateISOOptions osTypeId(String osTypeId) {
      this.queryParameters.replaceValues("ostypeid", ImmutableSet.of(osTypeId + ""));
      return this;
   }

   /**
    * @param passwordEnabled true if the image supports the password reset feature; default is false
    */
   public UpdateISOOptions passwordEnabled(boolean passwordEnabled) {
      this.queryParameters.replaceValues("passwordenabled", ImmutableSet.of(passwordEnabled + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param bootable true if image is bootable, false otherwise
       */
      public static UpdateISOOptions bootable(boolean bootable) {
         return new UpdateISOOptions().bootable(bootable);
      }

      /**
       * @param displayText the display text of the image
       */
      public static UpdateISOOptions displayText(String displayText) {
         return new UpdateISOOptions().displayText(displayText);
      }

      /**
       * @param format the format for the image
       */
      public static UpdateISOOptions format(String format) {
         return new UpdateISOOptions().format(format);
      }

      /**
       * @param name the name of the image file
       */
      public static UpdateISOOptions name(String name) {
         return new UpdateISOOptions().name(name);
      }

      /**
       * @param osTypeId the ID of the OS type that best represents the OS of this image.
       */
      public static UpdateISOOptions osTypeId(String osTypeId) {
         return new UpdateISOOptions().osTypeId(osTypeId);
      }

      /**
       * @param passwordEnabled true if the image supports the password reset feature; default is false
       */
      public static UpdateISOOptions passwordEnabled(boolean passwordEnabled) {
         return new UpdateISOOptions().passwordEnabled(passwordEnabled);
      }
   }

}
