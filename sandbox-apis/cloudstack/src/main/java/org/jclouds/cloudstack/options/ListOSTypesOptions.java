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

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what OSType information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api/user/listOsTypes.html"
 *      />
 * @author Adrian Cole
 */
public class ListOSTypesOptions extends BaseHttpRequestOptions {

   public static final ListOSTypesOptions NONE = new ListOSTypesOptions();

   /**
    * @param id
    *           list by Os type Id
    */
   public ListOSTypesOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param OSCategoryId
    *           list by Os Category id
    */
   public ListOSTypesOptions OSCategoryId(long OSCategoryId) {
      this.queryParameters.replaceValues("oscategoryid", ImmutableSet.of(OSCategoryId + ""));
      return this;
   }

   public static class Builder {
      /**
       * @see ListOSTypesOptions#id
       */
      public static ListOSTypesOptions id(long id) {
         ListOSTypesOptions options = new ListOSTypesOptions();
         return options.id(id);
      }

      /**
       * @see ListOSTypesOptions#OSCategoryId
       */
      public static ListOSTypesOptions OSCategoryId(long id) {
         ListOSTypesOptions options = new ListOSTypesOptions();
         return options.OSCategoryId(id);
      }

   }
}
