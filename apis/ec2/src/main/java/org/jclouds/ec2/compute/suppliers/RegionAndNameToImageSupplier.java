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
package org.jclouds.ec2.compute.suppliers;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.compute.domain.RegionAndName;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class RegionAndNameToImageSupplier implements Supplier<LoadingCache<RegionAndName, ? extends Image>> {
   private final LoadingCache<RegionAndName, Image> cache;

   @Inject
   protected RegionAndNameToImageSupplier(CacheLoader<RegionAndName, Image> regionAndIdToImage, 
            @Named(PROPERTY_SESSION_INTERVAL) long expirationSecs) {
      cache = CacheBuilder.newBuilder().expireAfterWrite(expirationSecs,  TimeUnit.SECONDS).build(regionAndIdToImage);
   }

   @Override
   public LoadingCache<RegionAndName, ? extends Image> get() {
      return cache;
   }
}
