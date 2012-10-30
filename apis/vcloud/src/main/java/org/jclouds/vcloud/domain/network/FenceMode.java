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
package org.jclouds.vcloud.domain.network;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.CaseFormat;

/**
 * 
 * The FenceMode element contains one of the following strings that specify how a network is
 * connected to its parent network.
 * 
 * @author Adrian Cole
 */
public enum FenceMode {
   /**
    * The two networks are bridged.
    * <p/>
    * Note that in vcloud 0.8 this was called ALLOW_IN_OUT, and so our implementation automatically
    * converts this for you. Use bridged instead of allowInOut.
    * 
    * @since vcloud api 0.9
    */
   BRIDGED,
   /**
    * The two networks are not connected.
    * 
    * @since vcloud api 0.8
    */
   ISOLATED,
   /**
    * The two networks are connected as specified in their NatService elements.
    * 
    * @since vcloud api 0.8
    */
   NAT_ROUTED, UNRECOGNIZED;

   public String value() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
   }

   @Override
   public String toString() {
      return value();
   }

   public static FenceMode fromValue(String fenceMode) {
      try {
         return valueOf(CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(fenceMode, "fenceMode")));
      } catch (IllegalArgumentException e) {
         return UNRECOGNIZED;
      }
   }

}
