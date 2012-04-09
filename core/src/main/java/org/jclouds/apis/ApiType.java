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
package org.jclouds.apis;

import static org.jclouds.util.Preconditions2.checkNotEmpty;

import com.google.common.base.CaseFormat;

/**
 * 
 * @author Adrian Cole
 */
public enum ApiType {

   BLOBSTORE, COMPUTE, LOADBALANCER, TABLE, QUEUE, MONITOR, UNRECOGNIZED;

   @Override
   public String toString() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
   }

   public static ApiType fromValue(String type) {
      checkNotEmpty(type, "type must be defined");
      try {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, type));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }
}