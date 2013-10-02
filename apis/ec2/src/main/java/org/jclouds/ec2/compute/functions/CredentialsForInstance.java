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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.RunningInstance;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CredentialsForInstance extends CacheLoader<RunningInstance, Optional<LoginCredentials>> {

   private final ConcurrentMap<RegionAndName, KeyPair> credentialsMap;
   private final Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap;
   private final Function<RunningInstance, LoginCredentials> passwordCredentialsFromWindowsInstance;

   @Inject
   CredentialsForInstance(ConcurrentMap<RegionAndName, KeyPair> credentialsMap,
            Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap, Function<RunningInstance, LoginCredentials> passwordCredentialsFromWindowsInstance) {
      this.credentialsMap = checkNotNull(credentialsMap, "credentialsMap");
      this.imageMap = checkNotNull(imageMap, "imageMap");
      this.passwordCredentialsFromWindowsInstance = checkNotNull(passwordCredentialsFromWindowsInstance, "passwordCredentialsFromWindowsInstance");
   }

   @Override
   public Optional<LoginCredentials> load(final RunningInstance instance) throws ExecutionException {
      if ("windows".equals(instance.getPlatform())) {
         return Optional.of(passwordCredentialsFromWindowsInstance.apply(instance));
      } else  if (instance.getKeyName() != null) {
         return Optional.of(LoginCredentials.builder().user(getLoginAccountFor(instance)).privateKey(getPrivateKeyOrNull(instance)).build());
      }
      return Optional.absent();
   }

   @VisibleForTesting
   String getPrivateKeyOrNull(RunningInstance instance) {
      KeyPair keyPair = credentialsMap.get(new RegionAndName(instance.getRegion(), instance.getKeyName()));
      return keyPair != null ? keyPair.getKeyMaterial() : null;
   }

   @VisibleForTesting
   String getLoginAccountFor(RunningInstance from) throws ExecutionException {
      return imageMap.get().get(new RegionAndName(from.getRegion(), from.getImageId())).getDefaultCredentials().identity;
   }
}
