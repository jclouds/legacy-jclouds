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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.c1_medium;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Provider;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.domain.internal.TemplateBuilderImplTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.ImagesToRegionAndIdMap;
import org.jclouds.ec2.compute.options.EC2TemplateOptions;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class EC2TemplateBuilderImplTest extends TemplateBuilderImplTest {

   @Override
   protected TemplateOptions provideTemplateOptions() {
      return new EC2TemplateOptions();
   }

   @Override
   protected EC2TemplateBuilderImpl createTemplateBuilder(final Image knownImage,
         @Memoized Supplier<Set<? extends Location>> locations, final @Memoized Supplier<Set<? extends Image>> images,
         @Memoized Supplier<Set<? extends Hardware>> sizes, Location defaultLocation,
         Provider<TemplateOptions> optionsProvider, Provider<TemplateBuilder> templateBuilderProvider) {

      LoadingCache<RegionAndName, ? extends Image> imageMap;
      if (knownImage != null) {
         final RegionAndName knownRegionAndName = new RegionAndName(knownImage.getLocation().getId(), knownImage.getProviderId());

         imageMap = CacheBuilder.newBuilder().build(new CacheLoader<RegionAndName, Image>() {
            @Override
            public Image load(RegionAndName from) {
               return from.equals(knownRegionAndName) ? knownImage : null;
            }

         });
         
      } else {
         imageMap = CacheBuilder.newBuilder().build(CacheLoader.from(Functions.forMap(
                  ImagesToRegionAndIdMap.imagesToMap(images.get()))));
      }

      return new EC2TemplateBuilderImpl(locations, images, sizes, Suppliers.ofInstance(defaultLocation),
            optionsProvider, templateBuilderProvider, Suppliers.<LoadingCache<RegionAndName, ? extends Image>>ofInstance(imageMap));
   }
   

   @Override
   @Test
   public void testHardwareWithImageIdPredicateOnlyAcceptsImageWhenLocationNull() {
      // not possible to have null region in ec2
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testParseOnDemand() {

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(Sets
            .<Image> newLinkedHashSet());
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(c1_medium().build()));

      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      Image knownImage = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      expect(knownImage.getId()).andReturn("us-east-1/ami").atLeastOnce();
      expect(knownImage.getLocation()).andReturn(region).atLeastOnce();
      expect(knownImage.getName()).andReturn(null).atLeastOnce();
      expect(knownImage.getDescription()).andReturn(null).atLeastOnce();
      expect(knownImage.getVersion()).andReturn(null).atLeastOnce();
      expect(knownImage.getProviderId()).andReturn("ami").atLeastOnce();

      expect(knownImage.getOperatingSystem()).andReturn(os).atLeastOnce();

      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn("paravirtual").atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();

      replay(knownImage);
      replay(os);
      replay(defaultOptions);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(knownImage, locations, images, sizes, region,
            optionsProvider, templateBuilderProvider);

      assertEquals(template.imageId("us-east-1/ami").build().getImage(), knownImage);

      verify(knownImage);
      verify(os);
      verify(defaultOptions);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testParseOnDemandWithoutRegionEncodedIntoId() {

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(c1_medium().build()));

      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      Image knownImage = createMock(Image.class);
      expect(knownImage.getId()).andReturn("region/ami").anyTimes();
      expect(knownImage.getProviderId()).andReturn("ami").anyTimes();
      expect(knownImage.getLocation()).andReturn(region).anyTimes();

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(knownImage);
      replay(defaultOptions);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(knownImage, locations, images, sizes, region,
            optionsProvider, templateBuilderProvider);
      try {
         template.imageId("ami").build();
         assert false;
      } catch (IllegalArgumentException e) {

      }
      verify(knownImage);
      verify(defaultOptions);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = NoSuchElementException.class)
   public void testParseOnDemandNotFound() {

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(c1_medium().build()));

      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      Image knownImage = createMock(Image.class);
      expect(knownImage.getId()).andReturn("region/ami").anyTimes();
      expect(knownImage.getProviderId()).andReturn("ami").anyTimes();
      expect(knownImage.getLocation()).andReturn(region).anyTimes();

      expect(defaultLocation.getId()).andReturn("region");
      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(knownImage);
      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(knownImage, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);

      assertEquals(template.imageId("region/bad").build().getImage(), knownImage);

      verify(knownImage);
      verify(defaultOptions);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

}
