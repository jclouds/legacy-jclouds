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
 * Options used to control updates to VMGroups
 *
 * @author Richard Downer
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api/user/updateInstanceGroup.html"
 *      />
 */
public class UpdateVMGroupOptions extends BaseHttpRequestOptions {

   public static final UpdateVMGroupOptions NONE = new UpdateVMGroupOptions();

   /**
    * @param name new name of the VMGroup
    */
   public UpdateVMGroupOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   public static class Builder {
      /**
       * @see org.jclouds.cloudstack.options.UpdateVMGroupOptions#name
       */
      public static UpdateVMGroupOptions name(String name) {
         UpdateVMGroupOptions options = new UpdateVMGroupOptions();
         return options.name(name);
      }
   }

}
