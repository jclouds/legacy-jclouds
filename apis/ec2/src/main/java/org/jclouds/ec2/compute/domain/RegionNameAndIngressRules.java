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
package org.jclouds.ec2.compute.domain;

/**
 * 
 * @author Adrian Cole
 */
public class RegionNameAndIngressRules extends RegionAndName {
   private final int[] ports;
   private final boolean authorizeSelf;

   public RegionNameAndIngressRules(String region, String tag, int[] ports, boolean authorizeSelf) {
      super(region, tag);
      this.ports = ports;
      this.authorizeSelf = authorizeSelf;
   }

   // intentionally not overriding equals or hash-code so that we can search only by region/tag in a
   // map

   public int[] getPorts() {
      return ports;
   }

   public boolean shouldAuthorizeSelf() {
      return authorizeSelf;
   }

}
