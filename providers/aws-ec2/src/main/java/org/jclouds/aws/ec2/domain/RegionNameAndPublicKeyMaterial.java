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
package org.jclouds.aws.ec2.domain;

import org.jclouds.ec2.compute.domain.RegionAndName;

/**
 * 
 * @author Adrian Cole
 */
public class RegionNameAndPublicKeyMaterial extends RegionAndName {
   private final String publicKeyMaterial;

   public RegionNameAndPublicKeyMaterial(String region, String tag, String publicKeyMaterial) {
      super(region, tag);
      this.publicKeyMaterial = publicKeyMaterial;
   }

   // intentionally not overriding equals or hash-code so that we can search only by region/tag in a
   // map

   public String getPublicKeyMaterial() {
      return publicKeyMaterial;
   }

}
