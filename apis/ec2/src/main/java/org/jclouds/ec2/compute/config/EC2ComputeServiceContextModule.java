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
package org.jclouds.ec2.compute.config;

import static com.google.common.collect.Iterables.toArray;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.concurrent.RetryOnTimeOutExceptionSupplier;
import org.jclouds.ec2.compute.EC2ComputeService;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.loaders.RegionAndIdToImage;
import org.jclouds.ec2.compute.suppliers.RegionAndNameToImageSupplier;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.SetAndThrowAuthorizationExceptionSupplier;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * Configures the {@link ComputeServiceContext}; requires {@link EC2ComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class EC2ComputeServiceContextModule extends BaseComputeServiceContextModule {
   @Override
   protected void configure() {
      installDependencies();
      install(new EC2BindComputeStrategiesByClass());
      install(new EC2BindComputeSuppliersByClass());
      super.configure();
   }
   
   protected void installDependencies(){
      install(new EC2ComputeServiceDependenciesModule());
   }
   
   @Override
   protected boolean shouldEagerlyParseImages(Injector injector) {
      // If no owners to query, then will never lookup all images
      String[] amiOwners = injector.getInstance(Key.get(String[].class, Names.named(PROPERTY_EC2_AMI_OWNERS)));
      return (amiOwners.length > 0);
   }

   @Override
   protected Supplier<Set<? extends Image>> supplyNonParsingImageCache(
            AtomicReference<AuthorizationException> authException, @Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final Supplier<Set<? extends Image>> imageSupplier, Injector injector) {
      final Supplier<LoadingCache<RegionAndName, ? extends Image>> cache = injector.getInstance(Key.get(new TypeLiteral<Supplier<LoadingCache<RegionAndName, ? extends Image>>>() {}));
      return new Supplier<Set<? extends Image>>() {
         @Override
         public Set<? extends Image> get() {
            return ImmutableSet.copyOf(cache.get().asMap().values());
         }
      };
   }

   @Provides
   @Singleton
   protected Supplier<LoadingCache<RegionAndName, ? extends Image>> provideRegionAndNameToImageSupplierCache(
            final RegionAndNameToImageSupplier supplier) {
      return supplier;
   }

   @Provides
   @Singleton
   protected Supplier<CacheLoader<RegionAndName, Image>> provideRegionAndNameToImageSupplierCacheLoader(
            final RegionAndIdToImage delegate) {
      return Suppliers.<CacheLoader<RegionAndName, Image>>ofInstance(new CacheLoader<RegionAndName, Image>() {
         private final AtomicReference<AuthorizationException> authException = new AtomicReference<AuthorizationException>();

         @Override
         public Image load(final RegionAndName key) throws Exception {
            // raw lookup of an image
            Supplier<Image> rawSupplier = new Supplier<Image>() {
               @Override public Image get() {
                  try {
                     return delegate.load(key);
                  } catch (ExecutionException e) {
                     throw Throwables.propagate(e);
                  }
               }
            };
            
            // wrap in retry logic
            Supplier<Image> retryingSupplier = new RetryOnTimeOutExceptionSupplier<Image>(
                  new SetAndThrowAuthorizationExceptionSupplier<Image>(rawSupplier, authException));
            
            return retryingSupplier.get();
         }
         
      });
   }

   @Provides
   @Singleton
   @Named(PROPERTY_EC2_AMI_OWNERS)
   String[] amiOwners(@Named(PROPERTY_EC2_AMI_OWNERS) String amiOwners) {
      if (amiOwners.trim().equals(""))
         return new String[] {};
      return toArray(Splitter.on(',').split(amiOwners), String.class);
   }
   
   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.of(i.getInstance(ImageExtension.class));
   }

}

