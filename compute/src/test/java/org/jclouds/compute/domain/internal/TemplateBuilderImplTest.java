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
package org.jclouds.compute.domain.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Provider;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class TemplateBuilderImplTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2").description("aws-ec2").build();

   protected Location region = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1").description("us-east-1")
         .parent(provider).build();
   
   protected Location region2 = new LocationBuilder().scope(LocationScope.REGION).id("us-east-2").description("us-east-2")
         .parent(provider).build();
   
   @SuppressWarnings("unchecked")
   public void testLocationPredicateWhenComputeMetadataIsNotLocationBound() {
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      Hardware hardware = new HardwareBuilder().id("hardwareId").build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(
               image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(image.getLocation()).andReturn(region).anyTimes();
      expect(image.getProviderId()).andReturn("imageId").anyTimes();

      replay(image);
      replay(os);
      replay(defaultTemplate);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);
      assert template.locationPredicate.apply(hardware);

      verify(image);
      verify(os);
      verify(defaultTemplate);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testResolveImages() {
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);
      Image image2 = createMock(Image.class);
      OperatingSystem os2 = createMock(OperatingSystem.class);

      Hardware hardware = new HardwareBuilder().id("hardwareId").build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(
               image, image2));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(image.getName()).andReturn("imageName");
      expect(image2.getName()).andReturn("imageName");
      expect(image.getDescription()).andReturn("imageDescription");
      expect(image2.getDescription()).andReturn("imageDescription");
      expect(image.getVersion()).andReturn("imageVersion");
      expect(image2.getVersion()).andReturn("imageVersion");
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image2.getOperatingSystem()).andReturn(os2).atLeastOnce();
      expect(image.getLocation()).andReturn(region).anyTimes();
      expect(image2.getLocation()).andReturn(region).anyTimes();
      expect(image.getProviderId()).andReturn("imageId").anyTimes();
      expect(image2.getProviderId()).andReturn("imageId2").anyTimes();
      expect(os.getName()).andReturn("osName");
      expect(os2.getName()).andReturn("osName");
      expect(os.getVersion()).andReturn("osVersion");
      expect(os2.getVersion()).andReturn("osVersion");
      expect(os.getDescription()).andReturn("osDescription");
      expect(os2.getDescription()).andReturn("osDescription");
      expect(os.getArch()).andReturn("X86_64").atLeastOnce();
      expect(os2.getArch()).andReturn("X86_64").atLeastOnce();
      
      replay(image);
      replay(image2);
      replay(os);
      replay(os2);
      replay(defaultTemplate);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      assertEquals(template.resolveImage(hardware, images.get()), image2);

      verify(image);
      verify(image2);
      verify(os);
      verify(os2);
      verify(defaultTemplate);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testArchWins() {
      Image image = createMock(Image.class);
      Image image2 = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);
      OperatingSystem os2 = createMock(OperatingSystem.class);

      Hardware hardware = new HardwareBuilder().id("hardwareId").build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(
               image, image2));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());

      
      expect(image.getLocation()).andReturn(region).atLeastOnce();
      expect(image2.getLocation()).andReturn(region).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image2.getOperatingSystem()).andReturn(os2).atLeastOnce();
      expect(image.getId()).andReturn("us-east-1/1").atLeastOnce();
      expect(image.getProviderId()).andReturn("1").anyTimes();
      expect(image2.getProviderId()).andReturn("2").anyTimes();

      expect(os.getArch()).andReturn("X86_32").atLeastOnce();
      expect(os2.getArch()).andReturn("X86_64").atLeastOnce();

      replay(image);
      replay(image2);
      replay(os);
      replay(os2);
      replay(defaultTemplate);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      assertEquals(template.smallest().osArchMatches("X86_32").build().getImage(), image);

      verify(image);
      verify(image2);
      verify(os);
      verify(os2);
      verify(defaultTemplate);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHardwareWithImageIdPredicateOnlyAcceptsImage() {
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      Hardware hardware = new HardwareBuilder().id("hardwareId").supportsImage(ImagePredicates.idEquals("us-east-1/imageId"))
               .build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
               .<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());
      expect(image.getId()).andReturn("us-east-1/imageId").atLeastOnce();
      expect(image.getLocation()).andReturn(region).atLeastOnce();
      expect(image.getName()).andReturn(null).atLeastOnce();
      expect(image.getDescription()).andReturn(null).atLeastOnce();
      expect(image.getVersion()).andReturn(null).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image.getProviderId()).andReturn("imageId").anyTimes();
      

      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn(null).atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();


      replay(image);
      replay(os);
      replay(defaultTemplate);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      template.imageId("us-east-1/imageId").build();

      verify(image);
      verify(os);
      verify(defaultTemplate);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHardwareWithImageIdPredicateOnlyAcceptsImageWhenLocationNull() {
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      Hardware hardware = new HardwareBuilder().id("hardwareId").supportsImage(ImagePredicates.idEquals("us-east-1/imageId"))
               .build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
               .<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());
      expect(image.getId()).andReturn("us-east-1/imageId").atLeastOnce();
      expect(image.getLocation()).andReturn(null).atLeastOnce();
      expect(image.getName()).andReturn(null).atLeastOnce();
      expect(image.getDescription()).andReturn(null).atLeastOnce();
      expect(image.getVersion()).andReturn(null).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image.getProviderId()).andReturn("imageId").anyTimes();
      
      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn(null).atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();

      replay(image, os, defaultTemplate, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      template.imageId("us-east-1/imageId").build();

      verify(image, os, defaultTemplate, optionsProvider, templateBuilderProvider);

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHardwareWithImageIdPredicateOnlyDoesntImage() {
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      Hardware hardware = new HardwareBuilder().id("hardwareId").supportsImage(ImagePredicates.idEquals("differentImageId"))
               .build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
               .<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());
      expect(image.getId()).andReturn("us-east-1/imageId").atLeastOnce();
      expect(image.getLocation()).andReturn(region).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image.getName()).andReturn(null).atLeastOnce();
      expect(image.getDescription()).andReturn(null).atLeastOnce();
      expect(image.getVersion()).andReturn(null).atLeastOnce();
      expect(image.getProviderId()).andReturn("imageId").anyTimes();
      

      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn(null).atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();

      replay(image);
      replay(os);
      replay(defaultTemplate);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(image, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);
      try {
         template.imageId("us-east-1/imageId").build();
         assert false;
      } catch (NoSuchElementException e) {
         // make sure big data is not in the exception message
         assertEquals(
                  e.getMessage(),
                  "no hardware profiles support images matching params: [biggest=false, fastest=false, imageName=null, imageDescription=null, imageId=us-east-1/imageId, imagePredicate=null, imageVersion=null, location=[id=us-east-1, scope=REGION, description=us-east-1, parent=aws-ec2, iso3166Codes=[], metadata={}], minCores=0.0, minRam=0, osFamily=null, osName=null, osDescription=null, osVersion=null, osArch=null, os64Bit=false, hardwareId=null, hypervisor=null]");
         verify(image);
         verify(os);
         verify(defaultTemplate);
         verify(optionsProvider);
         verify(templateBuilderProvider);
      }
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testOptionsUsesDefaultTemplateBuilder() {
      TemplateOptions options = provideTemplateOptions();
      TemplateOptions from = provideTemplateOptions();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(templateBuilderProvider.get()).andReturn(defaultTemplate);
      expect(defaultTemplate.options(from)).andReturn(defaultTemplate);
      expect(defaultTemplate.build()).andReturn(null);
      expect(optionsProvider.get()).andReturn(from).atLeastOnce();

      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      template.options(options).build();

      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testNothingUsesDefaultTemplateBuilder() {

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());

      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(templateBuilderProvider.get()).andReturn(defaultTemplate);
      expect(defaultTemplate.build()).andReturn(null);

      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      template.build();

      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   protected TemplateBuilderImpl createTemplateBuilder(Image knownImage, Supplier<Set<? extends Location>> locations,
            Supplier<Set<? extends Image>> images, Supplier<Set<? extends Hardware>> hardwares,
            Location defaultLocation, Provider<TemplateOptions> optionsProvider,
            Provider<TemplateBuilder> templateBuilderProvider) {
      TemplateBuilderImpl template = new TemplateBuilderImpl(locations, images, hardwares, Suppliers
               .ofInstance(defaultLocation), optionsProvider, templateBuilderProvider);
      return template;
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedImageLocationWiderThanDefault() {
      TemplateOptions from = provideTemplateOptions();

      Image image = createMock(Image.class);

      Hardware hardware = new HardwareBuilder().id("hardwareId").supportsImage(ImagePredicates.idEquals("us-east-1/foo")).build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
               .<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Location imageLocation = createMock(Location.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      expect(optionsProvider.get()).andReturn(from).atLeastOnce();


      expect(image.getId()).andReturn("us-east-1/foo").atLeastOnce();
      expect(image.getLocation()).andReturn(region).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image.getName()).andReturn(null).atLeastOnce();
      expect(image.getDescription()).andReturn(null).atLeastOnce();
      expect(image.getVersion()).andReturn(null).atLeastOnce();
      expect(image.getProviderId()).andReturn("foo").anyTimes();
      
      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn(null).atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();


      replay(defaultOptions);
      replay(imageLocation);
      replay(image);
      replay(os);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      assertEquals(template.imageId("us-east-1/foo").locationId(region.getId()).build().getLocation(), region);

      verify(defaultOptions);
      verify(imageLocation);
      verify(image);
      verify(os);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedLocationWithNoOptions() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("foo").locationId("location").build();
         assert false;
      } catch (NoSuchElementException e) {

      }

      verify(defaultOptions);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedLocationAndOptions() {
      TemplateOptions from = provideTemplateOptions();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from).atLeastOnce();

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("foo").options(provideTemplateOptions()).locationId("location").build();
         assert false;
      } catch (NoSuchElementException e) {

      }

      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultLocationWithNoOptionsNoSuchElement() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("region/imageId").build();
         assert false;
      } catch (NoSuchElementException e) {
         // make sure big data is not in the exception message
         assertEquals(e.getMessage(), "imageId(region/imageId) not found");
      }

      verify(defaultOptions);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultLocationWithUnmatchedPredicateExceptionMessageAndLocationNotCalled() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageDescriptionMatches("description").build();
         assert false;
      } catch (NoSuchElementException e) {
         // make sure big data is not in the exception message
         assertEquals(e.getMessage(), "no image matched predicate: And(nullEqualToIsParentOrIsGrandparentOfCurrentLocation(),imageDescription(description))");
      }

      verify(defaultOptions);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   protected TemplateOptions provideTemplateOptions() {
      return new TemplateOptions();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultLocationWithOptions() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      TemplateOptions from = provideTemplateOptions();
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from);

      expect(from.getInboundPorts()).andReturn(new int[] { 22 });

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("region/ami").options(provideTemplateOptions()).build();
         assert false;
      } catch (NoSuchElementException e) {

      }

      verify(defaultLocation);
      // verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testImageIdNullsEverythingElse() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      template.imageDescriptionMatches("imageDescriptionMatches");
      template.imageNameMatches("imageNameMatches");
      template.imageVersionMatches("imageVersionMatches");
      template.osDescriptionMatches("osDescriptionMatches");
      template.osFamily(OsFamily.CENTOS);
      template.osArchMatches("osArchMatches");

      assertEquals(template.osArch, "osArchMatches");
      assertEquals(template.imageDescription, "imageDescriptionMatches");
      assertEquals(template.imageName, "imageNameMatches");
      assertEquals(template.imageVersion, "imageVersionMatches");
      assertEquals(template.osDescription, "osDescriptionMatches");
      assertEquals(template.osFamily, OsFamily.CENTOS);
      assertEquals(template.imageId, null);

      template.imageId("myid");
      assertEquals(template.osArch, null);
      assertEquals(template.imageDescription, null);
      assertEquals(template.imageName, null);
      assertEquals(template.imageVersion, null);
      assertEquals(template.osDescription, null);
      assertEquals(template.osFamily, null);
      assertEquals(template.imageId, "myid");

      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testHardwareIdNullsHypervisor() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);


      template.hypervisorMatches("OpenVZ");

      assertEquals(template.hardwareId, null);
      assertEquals(template.hypervisor, "OpenVZ");

      template.hardwareId("myid");
      assertEquals(template.hardwareId, "myid");
      assertEquals(template.hypervisor, null);


      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @Test
   public void testMatchesHardwareWithIdPredicate() {

      final Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(region));
      final Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
            .<Image> of(
                  new ImageBuilder()
                        .ids("Ubuntu 11.04 x64")
                        .name("Ubuntu 11.04 x64")
                        .description("Ubuntu 11.04 x64")
                        .location(region)
                        .operatingSystem(
                              OperatingSystem.builder().name("Ubuntu 11.04 x64").description("Ubuntu 11.04 x64")
                                    .is64Bit(true).version("11.04").family(OsFamily.UBUNTU).build()).build(),
                  new ImageBuilder()
                        .ids("Ubuntu 11.04 64-bit")
                        .name("Ubuntu 11.04 64-bit")
                        .description("Ubuntu 11.04 64-bit")
                        .location(region)
                        .operatingSystem(
                              OperatingSystem.builder().name("Ubuntu 11.04 64-bit").description("Ubuntu 11.04 64-bit")
                                    .is64Bit(true).version("11.04").family(OsFamily.UBUNTU).build()).build()));

      final Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(
                  new HardwareBuilder()
                        .ids(String.format("datacenter(%s)platform(%s)cpuCores(%d)memorySizeMB(%d)diskSizeGB(%d)",
                              "Falkenberg", "Xen", 1, 512, 5)).ram(512)
                        .processors(ImmutableList.of(new Processor(1, 1.0)))
                        .volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 5, true, true))).hypervisor("Xen")
                        .location(region)
                        .supportsImage(ImagePredicates.idIn(ImmutableSet.of("Ubuntu 11.04 x64"))).build(),
                  new HardwareBuilder()
                        .ids(String.format("datacenter(%s)platform(%s)cpuCores(%d)memorySizeMB(%d)diskSizeGB(%d)",
                              "Falkenberg", "OpenVZ", 1, 512, 5)).ram(512)
                        .processors(ImmutableList.of(new Processor(1, 1.0)))
                        .volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 5, true, true))).hypervisor("OpenVZ")
                        .location(region)
                        .supportsImage(ImagePredicates.idIn(ImmutableSet.of("Ubuntu 11.04 64-bit"))).build()));

      final Provider<TemplateOptions> optionsProvider = new Provider<TemplateOptions>() {

         @Override
         public TemplateOptions get() {
            return new TemplateOptions();
         }

      };
      Provider<TemplateBuilder> templateBuilderProvider = new Provider<TemplateBuilder>() {

         @Override
         public TemplateBuilder get() {
            return createTemplateBuilder(null, locations, images, hardwares, region, optionsProvider, this);
         }

      };

      TemplateBuilder templateBuilder = templateBuilderProvider.get().minRam(512).osFamily(OsFamily.UBUNTU)
            .hypervisorMatches("OpenVZ").osVersionMatches("1[10].[10][04]").os64Bit(true);

      Template template = templateBuilder.build();
      assertEquals(template.getHardware().getHypervisor(), "OpenVZ");
      assertEquals(template.getImage().getId(), "Ubuntu 11.04 64-bit");

   }
   

   @Test
   public void testImageLocationNonDefault() {

      final Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(region));
      final Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
            .<Image> of(
                  new ImageBuilder()
                        .id("us-east-2/ami-ffff")
                        .providerId("ami-ffff")
                        .name("Ubuntu 11.04 x64")
                        .description("Ubuntu 11.04 x64")
                        .location(region2)
                        .operatingSystem(
                              OperatingSystem.builder().name("Ubuntu 11.04 x64").description("Ubuntu 11.04 x64")
                                    .is64Bit(true).version("11.04").family(OsFamily.UBUNTU).build()).build()));

      final Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(
                  new HardwareBuilder()
                        .ids("m1.small").ram(512)
                        .processors(ImmutableList.of(new Processor(1, 1.0)))
                        .volumes(ImmutableList.<Volume> of(new VolumeImpl((float) 5, true, true))).build()));

      final Provider<TemplateOptions> optionsProvider = new Provider<TemplateOptions>() {

         @Override
         public TemplateOptions get() {
            return new TemplateOptions();
         }

      };
      Provider<TemplateBuilder> templateBuilderProvider = new Provider<TemplateBuilder>() {

         @Override
         public TemplateBuilder get() {
            return createTemplateBuilder(null, locations, images, hardwares, region, optionsProvider, this);
         }

      };

      TemplateBuilder templateBuilder = templateBuilderProvider.get().hardwareId("m1.small").imageId("us-east-2/ami-ffff");
      
      Template template = templateBuilder.build();
      assertEquals(template.getLocation().getId(), "us-east-2");

   }
}
