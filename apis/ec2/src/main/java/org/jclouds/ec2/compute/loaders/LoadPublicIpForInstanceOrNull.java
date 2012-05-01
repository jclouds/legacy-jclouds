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
package org.jclouds.ec2.compute.loaders;

import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;

/**
 * @author Adrian Cole
 */
@Singleton
public class LoadPublicIpForInstanceOrNull extends CacheLoader<RegionAndName, String> {
   private final EC2Client client;

   @Inject
   public LoadPublicIpForInstanceOrNull(EC2Client client) {
      this.client = client;
   }

   @Override
   public String load(final RegionAndName key) throws Exception {
      try {
         return Iterables.find(client.getElasticIPAddressServices().describeAddressesInRegion(key.getRegion()),
                  new Predicate<PublicIpInstanceIdPair>() {

                     @Override
                     public boolean apply(PublicIpInstanceIdPair input) {
                        return key.getName().equals(input.getInstanceId());
                     }

                  }).getPublicIp();
      } catch (NoSuchElementException e) {
         return null;
      }
   }
}