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
package org.jclouds.ec2.compute.internal;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.util.Throwables2;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * 
 * @author Adrian Cole
 */
public class EC2TemplateBuilderImpl extends TemplateBuilderImpl {

   private final Supplier<LoadingCache<RegionAndName, ? extends Image>> lazyImageCache;

   @Inject
   protected EC2TemplateBuilderImpl(@Memoized Supplier<Set<? extends Location>> locations,
         @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
         Supplier<Location> defaultLocation, @Named("DEFAULT") Provider<TemplateOptions> optionsProvider,
         @Named("DEFAULT") Provider<TemplateBuilder> defaultTemplateProvider, Supplier<LoadingCache<RegionAndName, ? extends Image>> imageMap) {
      super(locations, images, sizes, defaultLocation, optionsProvider, defaultTemplateProvider);
      this.lazyImageCache = imageMap;
   }

   final Provider<Image> lazyImageProvider = new Provider<Image>() {

      @Override
      public Image get() {
         if (imageId != null) {
            String[] regionName = imageId.split("/");
            checkArgument(regionName.length == 2,
                  "amazon image ids must include the region ( ex. us-east-1/ami-7ea24a17 ) you specified: " + imageId);
            RegionAndName key = new RegionAndName(regionName[0], regionName[1]);
            try {
               return lazyImageCache.get().get(key);
            } catch (ExecutionException e) {
               throw new NoSuchElementException(String.format("could not get imageId(%s/%s)", key.getRegion(), key.getName()));
            } catch (UncheckedExecutionException e) {
               // Primarily for testing: if cache is backed by a map, can get IllegalArgumentException instead of NPE
               IllegalArgumentException e2 = Throwables2.getFirstThrowableOfType(e, IllegalArgumentException.class);
               if (e2 != null && e2.getMessage() != null && e2.getMessage().contains("not present in")) {
                  throw new NoSuchElementException(String.format("imageId(%s/%s) not found", key.getRegion(), key.getName()));
               }
               throw new NoSuchElementException(String.format("could not get imageId(%s/%s)", key.getRegion(), key.getName()));
            } catch (CacheLoader.InvalidCacheLoadException nex) {
               throw new NoSuchElementException(String.format("imageId(%s/%s) not found", key.getRegion(), key.getName()));
            }
         }
         return null;
      }

   };

   /**
    * @throws NoSuchElementException
    *            if the image is not found
    */
   @Override
   protected Image resolveImage(Hardware size, Iterable<? extends Image> supportedImages) {
      try {
         return super.resolveImage(size, supportedImages);
      } catch (NoSuchElementException e) {
         Image returnVal = lazyImageProvider.get();
         if (returnVal != null)
            return returnVal;
         throw e;
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   protected Set<? extends Image> getImages() {
      if (imageId != null) {
         Image image = lazyImageProvider.get();
         return ImmutableSet.of(image);
      } else {
         return (Set<Image>) this.images.get();
      }
   }
}
