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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CredentialsForInstance extends CacheLoader<RunningInstance, Credentials> {
   private final ConcurrentMap<RegionAndName, KeyPair> credentialsMap;
   private final Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap;

   @Inject
   CredentialsForInstance(ConcurrentMap<RegionAndName, KeyPair> credentialsMap,
         Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap) {
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.imageMap = imageMap;
   }

   @Override
   public Credentials load(RunningInstance instance) throws ExecutionException {
      Credentials credentials = null;// default if no keypair exists
      if (instance.getKeyName() != null) {
         credentials = new Credentials(getLoginAccountFor(instance), getPrivateKeyOrNull(instance));
      }
      return credentials;
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance) throws ExecutionException {
      KeyPair keyPair = credentialsMap.get(new RegionAndName(instance.getRegion(), instance.getKeyName()));
      return keyPair != null ? keyPair.getKeyMaterial() : null;
   }

   @VisibleForTesting
   String getLoginAccountFor(RunningInstance from) throws ExecutionException {
      return imageMap.get().get(new RegionAndName(from.getRegion(), from.getImageId())).getDefaultCredentials().identity;
   }
}
