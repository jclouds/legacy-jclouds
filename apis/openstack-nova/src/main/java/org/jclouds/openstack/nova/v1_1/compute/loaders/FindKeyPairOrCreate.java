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
package org.jclouds.openstack.nova.v1_1.compute.loaders;

import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;
import org.jclouds.openstack.nova.v1_1.domain.KeyPair;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndName;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Adam Lowe
 */
public class FindKeyPairOrCreate extends CacheLoader<ZoneAndName, KeyPair> {
   protected final Function<ZoneAndName, KeyPair> keypairCreator;

   @Inject
   public FindKeyPairOrCreate(
         Function<ZoneAndName, KeyPair> keypairCreator) {
      this.keypairCreator = checkNotNull(keypairCreator, "keypairCreator");
   }

   @Override
   public KeyPair load(ZoneAndName in) {
      // not retrieving KeyPairs from the server as the private key isn't returned
      return keypairCreator.apply(in);
   }

   @Override
   public String toString() {
      return "returnExistingKeyPairInZoneOrCreateAsNeeded()";
   }

}
