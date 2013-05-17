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
package org.jclouds.compute.domain.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Provider;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
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
import com.google.common.collect.Ordering;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "TemplateBuilderImplTest")
public class TemplateBuilderImplTest {

   public void testMultiMax() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3", "3");
      assertEquals(TemplateBuilderImpl.multiMax(Ordering.natural(), values), ImmutableList.of("3", "3"));
   }

   public void testMultiMax1() {
      Iterable<String> values = ImmutableList.of("1", "2", "2", "3");
      assertEquals(TemplateBuilderImpl.multiMax(Ordering.natural(), values), ImmutableList.of("3"));
   }
   
   protected Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2").description("aws-ec2").build();

   protected Location region = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1")
            .description("us-east-1").parent(provider).build();

   protected Location region2 = new LocationBuilder().scope(LocationScope.REGION).id("us-east-2")
            .description("us-east-2").parent(provider).build();

   protected OperatingSystem os = OperatingSystem.builder().name("osName").version("osVersion")
            .description("osDescription").arch("X86_32").build();

   protected Image image = new ImageBuilder().id("us-east-1/imageId").providerId("imageId").name("imageName")
            .description("imageDescription").version("imageVersion").operatingSystem(os).status(Image.Status.AVAILABLE)
            .location(region).build();

   protected Image image2 = ImageBuilder.fromImage(image).operatingSystem(os.toBuilder().arch("X86_64").build()).build();
   
   @SuppressWarnings("unchecked")
   public void testLocationPredicateWhenComputeMetadataIsNotLocationBound() {

      Hardware hardware = new HardwareBuilder().id("hardwareId").build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);


      replay(defaultTemplate, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);
      assert template.locationPredicate.apply(hardware);

      verify(defaultTemplate, optionsProvider, templateBuilderProvider);
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void testResolveImages() {


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

      replay(defaultTemplate, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      assertEquals(template.resolveImage(hardware, images.get()), image2);

      verify(defaultTemplate, optionsProvider, templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testArchWins() {

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

      replay(defaultTemplate, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      assertEquals(template.smallest().osArchMatches("X86_32").build().getImage(), image);

      verify(defaultTemplate, optionsProvider, templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHardwareWithImageIdPredicateOnlyAcceptsImage() {

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

      replay(defaultTemplate);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      template.imageId("us-east-1/imageId").build();

      verify(defaultTemplate);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHardwareWithImageIdPredicateOnlyAcceptsImageWhenLocationNull() {

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

      replay(defaultTemplate, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      template.imageId("us-east-1/imageId").build();

      verify(defaultTemplate, optionsProvider, templateBuilderProvider);

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHardwareWithImageIdPredicateOnlyDoesntImage() {

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
      
      replay(defaultTemplate, optionsProvider, templateBuilderProvider);
      
      TemplateBuilderImpl template = createTemplateBuilder(image, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);
      try {
         template.imageId("us-east-1/imageId").build();
         fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {
         // make sure message is succinct
         assertEquals(e.getMessage(), "no hardware profiles support images matching params: idEquals(differentImageId)");
         verify(defaultTemplate, optionsProvider, templateBuilderProvider);
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

      Hardware hardware = new HardwareBuilder().id("hardwareId").supportsImage(ImagePredicates.idEquals(image.getId())).build();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(provider, region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
               .<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(hardware));

      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);
      expect(optionsProvider.get()).andReturn(from).atLeastOnce();

      replay(defaultOptions, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      assertEquals(template.imageId(image.getId()).locationId(provider.getId()).build().getLocation(), region);

      verify(defaultOptions, optionsProvider, templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedLocationWithNoOptions() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      replay(defaultOptions);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("foo").locationId("location").build();
         fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {

      }

      verify(defaultOptions);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedLocationAndOptions() {
      TemplateOptions from = provideTemplateOptions();

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from).atLeastOnce();

      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("foo").options(provideTemplateOptions()).locationId("location").build();
         fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {

      }

      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   public void testImagesMustBePresentWhenQuerying() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(createMock(Hardware.class)));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      try {
         template.os64Bit(true).build();
         fail("Expected IllegalStateException");
      } catch (IllegalStateException e) {
         assertEquals(e.getMessage(), "no images present!");
      }

      verify(defaultOptions, optionsProvider, templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   public void testHardwareProfilesMustBePresentWhenQuerying() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of());
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      try {
         template.os64Bit(true).build();
         fail("Expected IllegalStateException");
      } catch (IllegalStateException e) {
         assertEquals(e.getMessage(), "no hardware profiles present!");
      }

      verify(defaultOptions, optionsProvider, templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultLocationWithNoOptionsNoSuchElement() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(createMock(Hardware.class)));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("region/imageId2").build();
         fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {
         // make sure big data is not in the exception message
         assertEquals(e.getMessage(), "imageId(region/imageId2) not found");
      }

      verify(defaultOptions, optionsProvider, templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultLocationWithUnmatchedPredicateExceptionMessageAndLocationNotCalled() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(region));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(createMock(Hardware.class)));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, region,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageDescriptionMatches("notDescription").build();
         fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {
         // make sure big data is not in the exception message
         assertEquals(e.getMessage(), "no image matched predicate: And(nullEqualToIsParentOrIsGrandparentOfCurrentLocation(),imageDescription(notDescription))");
      }

      verify(defaultOptions, optionsProvider, templateBuilderProvider);
   }

   protected TemplateOptions provideTemplateOptions() {
      return new TemplateOptions();
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultLocationWithOptions() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(image));
      Supplier<Set<? extends Hardware>> hardwares = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(createMock(Hardware.class)));
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      TemplateOptions from = provideTemplateOptions();
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from);

      replay(defaultLocation, optionsProvider, templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, hardwares, defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("region/ami").options(provideTemplateOptions()).build();
         fail("Expected NoSuchElementException");
      } catch (NoSuchElementException e) {

      }

      verify(defaultLocation, optionsProvider, templateBuilderProvider);
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
                        .status(Status.AVAILABLE)
                        .operatingSystem(
                              OperatingSystem.builder().name("Ubuntu 11.04 x64").description("Ubuntu 11.04 x64")
                                    .is64Bit(true).version("11.04").family(OsFamily.UBUNTU).build()).build(),
                  new ImageBuilder()
                        .ids("Ubuntu 11.04 64-bit")
                        .name("Ubuntu 11.04 64-bit")
                        .description("Ubuntu 11.04 64-bit")
                        .location(region)
                        .status(Status.AVAILABLE)
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

      assertEquals(templateBuilder.toString(), "{minRam=512, minRam=512, osFamily=ubuntu, osVersion=1[10].[10][04], os64Bit=true, hypervisor=OpenVZ}");

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
                        .status(Status.AVAILABLE)
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

      assertEquals(templateBuilder.toString(), "{imageId=us-east-2/ami-ffff, hardwareId=m1.small}");

      Template template = templateBuilder.build();
      assertEquals(template.getLocation().getId(), "us-east-2");

   }
   


   @Test
   public void testFromSpecWithLoginUser() {

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
                        .status(Status.AVAILABLE)
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

      TemplateBuilder templateBuilder = templateBuilderProvider.get().from("hardwareId=m1.small,imageId=us-east-2/ami-ffff,loginUser=user:Password01,authenticateSudo=true");

      assertEquals(templateBuilder.toString(), "{imageId=us-east-2/ami-ffff, hardwareId=m1.small}");

      Template template = templateBuilder.build();
      assertEquals(template.getLocation().getId(), "us-east-2");
      assertEquals(template.getOptions().getLoginUser(), "user");
      assertEquals(template.getOptions().getLoginPassword(), "Password01");
      assertEquals(template.getOptions().getLoginPrivateKey(), null);
      assertEquals(template.getOptions().shouldAuthenticateSudo(), Boolean.TRUE);
   }
}
