/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.compute.config;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.config.VCloudComputeServiceContextModule;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.compute.TerremarkVCloudComputeService;
import org.jclouds.vcloud.terremark.domain.ComputeOptions;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Injector;

/**
 * Configures the {@link TerremarkVCloudComputeServiceContext}; requires
 * {@link TerremarkVCloudComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudComputeServiceContextModule extends VCloudComputeServiceContextModule {

   @Override
   protected void configure() {
      super.configure();
   }

   @Override
   protected ComputeService provideComputeService(Injector injector) {
      return injector.getInstance(TerremarkVCloudComputeService.class);
   }

   private static final ComputeOptionsToSize sizeConverter = new ComputeOptionsToSize();

   private static class ComputeOptionsToSize implements Function<ComputeOptions, Size> {
      @Override
      public Size apply(ComputeOptions from) {
         return new SizeImpl(from.toString(), from.toString(), null, null, ImmutableMap
                  .<String, String> of(), from.getProcessorCount(), from.getMemory(), 10,
                  ImmutableSet.<Architecture> of(Architecture.X86_32, Architecture.X86_64));
      }
   }

   @Override
   protected Map<String, ? extends Size> provideSizes(Function<ComputeMetadata, String> indexer,
            VCloudClient client, Map<String, ? extends Image> images, LogHolder holder,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor)
            throws InterruptedException, TimeoutException, ExecutionException {
      Image anyImage = Iterables.get(images.values(), 0);
      holder.logger.debug(">> providing sizes");
      SortedSet<Size> sizes = Sets.newTreeSet(Iterables.transform(TerremarkVCloudClient.class.cast(
               client).getComputeOptionsOfCatalogItem(anyImage.getId()), sizeConverter));
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return Maps.uniqueIndex(sizes, indexer);
   }

}
