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

package org.jclouds.compute.internal;

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
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.SizeImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Location;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.util.Providers;

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
      Image image2 = createMock(Image.class);

      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(),
               1.0, 0, 0, ImagePredicates.any());

      Provider<Set<? extends Location>> locations = Providers.<Set<? extends Location>> of(ImmutableSet
               .<Location> of(defaultLocation));
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet
               .<Image> of(image, image2));
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(image.getName()).andReturn("image");
      expect(image2.getName()).andReturn("image");
      expect(image.getVersion()).andReturn("version");
      expect(image2.getVersion()).andReturn("version");
      expect(image.getOsDescription()).andReturn("osDescription");
      expect(image2.getOsDescription()).andReturn("osDescription");
      expect(image.getArchitecture()).andReturn(Architecture.X86_64).atLeastOnce();
      expect(image2.getArchitecture()).andReturn(Architecture.X86_64).atLeastOnce();

      replay(image);
      replay(image2);
      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

      assertEquals(template.resolveImage(size), image2);

      verify(image);
      verify(image2);
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

      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(),
               1.0, 0, 0, ImagePredicates.any());

      Provider<Set<? extends Location>> locations = Providers.<Set<? extends Location>> of(ImmutableSet
               .<Location> of(defaultLocation));
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet
               .<Image> of(image, image2));
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());

      expect(image.getLocation()).andReturn(defaultLocation).atLeastOnce();
      expect(image.getArchitecture()).andReturn(Architecture.X86_32).atLeastOnce();

      expect(image2.getLocation()).andReturn(defaultLocation).atLeastOnce();
      expect(image2.getArchitecture()).andReturn(Architecture.X86_64).atLeastOnce();

      replay(image);
      replay(image2);
      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

      assertEquals(template.smallest().architecture(Architecture.X86_32).build().getImage(), image);

      verify(image);
      verify(image2);
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
      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(), 0,
               0, 0, ImagePredicates.idEquals("imageId"));

      Provider<Set<? extends Location>> locations = Providers.<Set<? extends Location>> of(ImmutableSet
               .<Location> of(defaultLocation));
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of(image));
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());
      expect(image.getId()).andReturn("imageId").atLeastOnce();
      expect(image.getLocation()).andReturn(defaultLocation).atLeastOnce();
      expect(image.getOsFamily()).andReturn(null);
      expect(image.getName()).andReturn(null);
      expect(image.getDescription()).andReturn(null);
      expect(image.getOsDescription()).andReturn(null);
      expect(image.getVersion()).andReturn(null);
      expect(image.getArchitecture()).andReturn(null).atLeastOnce();

      replay(image);
      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

      template.imageId("imageId").build();

      verify(image);
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
      Size size = new SizeImpl("sizeId", null, "sizeId", defaultLocation, null, ImmutableMap.<String, String> of(), 0,
               0, 0, ImagePredicates.idEquals("imageId"));

      Provider<Set<? extends Location>> locations = Providers.<Set<? extends Location>> of(ImmutableSet
               .<Location> of(defaultLocation));
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of(image));
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of(size));
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateBuilder defaultTemplate = createMock(TemplateBuilder.class);

      expect(optionsProvider.get()).andReturn(new TemplateOptions());
      expect(image.getId()).andReturn("notImageId").atLeastOnce();

      replay(image);
      replay(defaultTemplate);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);
      try {
         template.imageId("notImageId").build();
         assert false;
      } catch (NoSuchElementException e) {
         verify(image);
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

      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.<Location> of());
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of());
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of());
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

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

      template.options(options).build();

      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testNothingUsesDefaultTemplateBuilder() {

      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.<Location> of());
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of());
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of());

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

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

      template.build();

      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   protected TemplateBuilderImpl createTemplateBuilder(Provider<Set<? extends Location>> locations,
            Provider<Set<? extends Image>> images, Provider<Set<? extends Size>> sizes, Location defaultLocation,
            Provider<TemplateOptions> optionsProvider, Provider<TemplateBuilder> templateBuilderProvider) {
      TemplateBuilderImpl template = new TemplateBuilderImpl(locations, images, sizes, defaultLocation,
               optionsProvider, templateBuilderProvider);
      return template;
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedLocationWithNoOptions() {
      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.<Location> of());
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of());
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

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

      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.<Location> of());
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of());
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from).atLeastOnce();

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

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
      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.<Location> of());
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of());
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

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
      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.<Location> of());
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of());
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      TemplateOptions from = provideTemplateOptions();
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(optionsProvider.get()).andReturn(from);

      expect(from.getInboundPorts()).andReturn(new int[] { 22 });

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

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
      Provider<Set<? extends Location>> locations = Providers
               .<Set<? extends Location>> of(ImmutableSet.<Location> of());
      Provider<Set<? extends Image>> images = Providers.<Set<? extends Image>> of(ImmutableSet.<Image> of());
      Provider<Set<? extends Size>> sizes = Providers.<Set<? extends Size>> of(ImmutableSet.<Size> of());
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = createTemplateBuilder(locations, images, sizes, defaultLocation, optionsProvider,
               templateBuilderProvider);

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
      assertEquals(template.os, OsFamily.CENTOS);
      assertEquals(template.imageId, null);

      template.imageId("myid");
      assertEquals(template.arch, null);
      assertEquals(template.imageDescription, null);
      assertEquals(template.imageName, null);
      assertEquals(template.imageVersion, null);
      assertEquals(template.osDescription, null);
      assertEquals(template.os, null);
      assertEquals(template.imageId, "myid");

      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

}
