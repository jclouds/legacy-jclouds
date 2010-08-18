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

package org.jclouds.compute.domain.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Provider;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TemplateBuilderImplTest {

   @SuppressWarnings("unchecked")
   @Test
   public void tesResolveImages() {
      Location defaultLocation = createMock(Location.class);
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);
      Image image2 = createMock(Image.class);
      OperatingSystem os2 = createMock(OperatingSystem.class);

      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(),
            1.0, 0, 0, ImagePredicates.any());

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(defaultLocation));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(
            image, image2));
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(image.getName()).andReturn("imageName");
      expect(image2.getName()).andReturn("imageName");
      expect(image.getVersion()).andReturn("imageVersion");
      expect(image2.getVersion()).andReturn("imageVersion");
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image2.getOperatingSystem()).andReturn(os2).atLeastOnce();
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
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);

      assertEquals(template.resolveImage(size, images.get()), image2);

      verify(image);
      verify(image2);
      verify(os);
      verify(os2);
      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testArchWins() {
      Location defaultLocation = createMock(Location.class);
      Image image = createMock(Image.class);
      Image image2 = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);
      OperatingSystem os2 = createMock(OperatingSystem.class);

      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(),
            1.0, 0, 0, ImagePredicates.any());

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(defaultLocation));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(
            image, image2));
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());

      expect(image.getLocation()).andReturn(defaultLocation).atLeastOnce();
      expect(image.getArchitecture()).andReturn(Architecture.X86_32).atLeastOnce();

      expect(image2.getLocation()).andReturn(defaultLocation).atLeastOnce();
      expect(image2.getArchitecture()).andReturn(Architecture.X86_64).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image2.getOperatingSystem()).andReturn(os2).atLeastOnce();

      replay(image);
      replay(image2);
      replay(os);
      replay(os2);
      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);

      assertEquals(template.smallest().architecture(Architecture.X86_32).build().getImage(), image);

      verify(image);
      verify(image2);
      verify(os);
      verify(os2);
      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSizeWithImageIdPredicateOnlyAcceptsImage() {
      Location defaultLocation = createMock(Location.class);
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(), 0,
            0, 0, ImagePredicates.idEquals("imageId"));

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(defaultLocation));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
            .<Image> of(image));
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());
      expect(image.getId()).andReturn("imageId").atLeastOnce();
      expect(image.getLocation()).andReturn(defaultLocation).atLeastOnce();
      expect(image.getName()).andReturn(null).atLeastOnce();
      expect(image.getDescription()).andReturn(null).atLeastOnce();
      expect(image.getVersion()).andReturn(null).atLeastOnce();
      expect(image.getArchitecture()).andReturn(null).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();

      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn(null).atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();

      replay(image);
      replay(os);
      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);

      template.imageId("imageId").build();

      verify(image);
      verify(os);
      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSizeWithImageIdPredicateOnlyDoesntImage() {
      Location defaultLocation = createMock(Location.class);
      Image image = createMock(Image.class);
      OperatingSystem os = createMock(OperatingSystem.class);

      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(), 0,
            0, 0, ImagePredicates.idEquals("imageId"));

      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(defaultLocation));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
            .<Image> of(image));
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());
      expect(image.getId()).andReturn("notImageId").atLeastOnce();
      expect(image.getLocation()).andReturn(defaultLocation).atLeastOnce();
      expect(image.getOperatingSystem()).andReturn(os).atLeastOnce();
      expect(image.getName()).andReturn(null).atLeastOnce();
      expect(image.getDescription()).andReturn(null).atLeastOnce();
      expect(image.getVersion()).andReturn(null).atLeastOnce();
      expect(image.getArchitecture()).andReturn(null).atLeastOnce();

      expect(os.getName()).andReturn(null).atLeastOnce();
      expect(os.getVersion()).andReturn(null).atLeastOnce();
      expect(os.getFamily()).andReturn(null).atLeastOnce();
      expect(os.getDescription()).andReturn(null).atLeastOnce();
      expect(os.getArch()).andReturn(null).atLeastOnce();
      expect(os.is64Bit()).andReturn(false).atLeastOnce();

      replay(image);
      replay(os);
      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(image, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);
      try {
         template.imageId("notImageId").build();
         assert false;
      } catch (NoSuchElementException e) {
         verify(image);
         verify(os);
         verify(defaultTemplate);
         verify(defaultLocation);
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
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(templateBuilderProvider.get()).andReturn(defaultTemplate);
      expect(defaultTemplate.options(options)).andReturn(defaultTemplate);
      expect(defaultTemplate.build()).andReturn(null);
      expect(optionsProvider.get()).andReturn(from).atLeastOnce();

      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
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
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of());

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

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);

      template.build();

      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   protected TemplateBuilderImpl createTemplateBuilder(Image knownImage, Supplier<Set<? extends Location>> locations,
         Supplier<Set<? extends Image>> images, Supplier<Set<? extends Size>> sizes, Location defaultLocation,
         Provider<TemplateOptions> optionsProvider, Provider<TemplateBuilder> templateBuilderProvider) {
      TemplateBuilderImpl template = new TemplateBuilderImpl(locations, images, sizes, Suppliers
            .ofInstance(defaultLocation), optionsProvider, templateBuilderProvider);
      return template;
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedLocationWithNoOptions() {
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of());
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of());
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
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
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from).atLeastOnce();

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
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
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);

      try {
         template.imageId("region/ami").build();
         assert false;
      } catch (NoSuchElementException e) {

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
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      TemplateOptions from = provideTemplateOptions();
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from);

      expect(from.getInboundPorts()).andReturn(new int[] { 22 });

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
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
      Supplier<Set<? extends Size>> sizes = Suppliers.<Set<? extends Size>> ofInstance(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(null, locations, images, sizes, defaultLocation,
            optionsProvider, templateBuilderProvider);

      template.architecture(Architecture.X86_32);
      template.imageDescriptionMatches("imageDescriptionMatches");
      template.imageNameMatches("imageNameMatches");
      template.imageVersionMatches("imageVersionMatches");
      template.osDescriptionMatches("osDescriptionMatches");
      template.osFamily(OsFamily.CENTOS);

      assertEquals(template.arch, Architecture.X86_32);
      assertEquals(template.imageDescription, "imageDescriptionMatches");
      assertEquals(template.imageName, "imageNameMatches");
      assertEquals(template.imageVersion, "imageVersionMatches");
      assertEquals(template.osDescription, "osDescriptionMatches");
      assertEquals(template.osFamily, OsFamily.CENTOS);
      assertEquals(template.imageId, null);

      template.imageId("myid");
      assertEquals(template.arch, null);
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

}
