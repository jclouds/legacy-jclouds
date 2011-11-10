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
 * Options for the Iso updateIso method.
 *
 * @see org.jclouds.cloudstack.features.IsoClient#updateIso
 * @see org.jclouds.cloudstack.features.IsoAsyncClient#updateIso
 * @author Richard Downer
 */
public class UpdateIsoOptions extends BaseHttpRequestOptions {

   public static final UpdateIsoOptions NONE = new UpdateIsoOptions(); 

   /**
    * @param bootable true if image is bootable, false otherwise
    */
   public UpdateIsoOptions bootable(boolean bootable) {
      this.queryParameters.replaceValues("bootable", ImmutableSet.of(bootable + ""));
      return this;
   }

   /**
    * @param displayText the display text of the image
    */
   public UpdateIsoOptions displayText(String displayText) {
      this.queryParameters.replaceValues("displaytext", ImmutableSet.of(displayText + ""));
      return this;
   }

   /**
    * @param format the format for the image
    */
   public UpdateIsoOptions format(String format) {
      this.queryParameters.replaceValues("format", ImmutableSet.of(format + ""));
      return this;
   }

   /**
    * @param name the name of the image file
    */
   public UpdateIsoOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name + ""));
      return this;
   }

   /**
    * @param osTypeId the ID of the OS type that best represents the OS of this image.
    */
   public UpdateIsoOptions osTypeId(long osTypeId) {
      this.queryParameters.replaceValues("ostypeid", ImmutableSet.of(osTypeId + ""));
      return this;
   }

   /**
    * @param passwordEnabled true if the image supports the password reset feature; default is false
    */
   public UpdateIsoOptions passwordEnabled(boolean passwordEnabled) {
      this.queryParameters.replaceValues("passwordenabled", ImmutableSet.of(passwordEnabled + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param bootable true if image is bootable, false otherwise
       */
      public static UpdateIsoOptions bootable(boolean bootable) {
         return new UpdateIsoOptions().bootable(bootable);
      }

      /**
       * @param displayText the display text of the image
       */
      public static UpdateIsoOptions displayText(String displayText) {
         return new UpdateIsoOptions().displayText(displayText);
      }

      /**
       * @param format the format for the image
       */
      public static UpdateIsoOptions format(String format) {
         return new UpdateIsoOptions().format(format);
      }

      /**
       * @param name the name of the image file
       */
      public static UpdateIsoOptions name(String name) {
         return new UpdateIsoOptions().name(name);
      }

      /**
       * @param osTypeId the ID of the OS type that best represents the OS of this image.
       */
      public static UpdateIsoOptions osTypeId(long osTypeId) {
         return new UpdateIsoOptions().osTypeId(osTypeId);
      }

      /**
       * @param passwordEnabled true if the image supports the password reset feature; default is false
       */
      public static UpdateIsoOptions passwordEnabled(boolean passwordEnabled) {
         return new UpdateIsoOptions().passwordEnabled(passwordEnabled);
      }
   }

}
