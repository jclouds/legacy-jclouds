/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Provider;

import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.compute.options.EC2TemplateOptions;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.HardwareImpl;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.domain.internal.TemplateBuilderImplTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true)
public class EC2TemplateBuilderImplTest extends TemplateBuilderImplTest {

   @Override
   protected TemplateOptions provideTemplateOptions() {
      return new EC2TemplateOptions();
   }

   @Override
   protected EC2TemplateBuilderImpl createTemplateBuilder(final Image knownImage,
            Supplier<Set<? extends Location>> locations, Supplier<Set<? extends Image>> images,
            Supplier<Set<? extends Hardware>> sizes, Location defaultLocation,
            Provider<TemplateOptions> optionsProvider, Provider<TemplateBuilder> templateBuilderProvider) {
      final RegionAndName knownRegionAndName = new RegionAndName("region", "ami");

      ConcurrentMap<RegionAndName, Image> imageMap = new MapMaker()
               .makeComputingMap(new Function<RegionAndName, Image>() {
                  @Override
                  public Image apply(RegionAndName from) {
                     return from.equals(knownRegionAndName) ? knownImage : null;
                  }

               });

      return new EC2TemplateBuilderImpl(locations, images, sizes, Suppliers.ofInstance(defaultLocation),
               optionsProvider, templateBuilderProvider, imageMap);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testParseOnDemand() {
      Location location = new LocationImpl(LocationScope.REGION, "region", "region", null);

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(location));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(Sets
               .<Image> newLinkedHashSet());
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(new HardwareImpl("1", "1", "region/1", location, null,
                        ImmutableMap.<String, String> of(), ImmutableList.of(new Processor(1, 1.0)), 1, ImmutableList
                                 .<Volume> of(), ImagePredicates.any())));

      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      Image knownImage = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      expect(knownImage.getId()).andReturn("region/ami").atLeastOnce();
      expect(knownImage.getLocation()).andReturn(location).atLeastOnce();
      expect(knownImage.getName()).andReturn(null).atLeastOnce();
      expect(knownImage.getDescription()).andReturn(null).atLeastOnce();
      expect(knownImage.getVersion()).andReturn(null).atLeastOnce();

      expect(knownImage.getOperatingSystem()).andReturn(os).atLeastOnce();

      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn(null).atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();

      replay(knownImage);
      replay(os);
      replay(defaultOptions);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(knownImage, locations, images, sizes, location,
               optionsProvider, templateBuilderProvider);

      assertEquals(template.imageId("region/ami").build().getImage(), knownImage);

      verify(knownImage);
      verify(os);
      verify(defaultOptions);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testParseOnDemandWithoutRegionEncodedIntoId() {
      Location location = new LocationImpl(LocationScope.REGION, "region", "region", null);

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(location));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(new HardwareImpl("1", "1", "region/1", location, null,
                        ImmutableMap.<String, String> of(), ImmutableList.of(new Processor(1, 1.0)), 1, ImmutableList
                                 .<Volume> of(), ImagePredicates.any())));

      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      Image knownImage = createMock(Image.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(knownImage);
      replay(defaultOptions);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(knownImage, locations, images, sizes, location,
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
      Location location = new LocationImpl(LocationScope.REGION, "region", "region", null);

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(location));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(new HardwareImpl("1", "1", "region/1", location, null,
                        ImmutableMap.<String, String> of(), ImmutableList.of(new Processor(1, 1.0)), 1, ImmutableList
                                 .<Volume> of(), ImagePredicates.any())));

      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      Image knownImage = createMock(Image.class);

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
