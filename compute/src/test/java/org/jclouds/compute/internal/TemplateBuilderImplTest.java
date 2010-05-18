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
package org.jclouds.compute.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;

import javax.inject.Provider;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TemplateBuilderImplTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testNothingUsesDefaultTemplateBuilder() {

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

      TemplateBuilderImpl template = new TemplateBuilderImpl(ImmutableSet.<Location> of(),
               ImmutableSet.<Image> of(), ImmutableSet.<Size> of(), defaultLocation,
               optionsProvider, templateBuilderProvider);

      template.build();

      verify(defaultTemplate);
      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testSuppliedLocationWithNoOptions() {

      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = new TemplateBuilderImpl(ImmutableSet.<Location> of(),
               ImmutableSet.<Image> of(), ImmutableSet.<Size> of(), defaultLocation,
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

      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = new TemplateBuilderImpl(ImmutableSet.<Location> of(),
               ImmutableSet.<Image> of(), ImmutableSet.<Size> of(), defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("foo").options(TemplateOptions.NONE).locationId("location").build();
         assert false;
      } catch (NoSuchElementException e) {

      }

      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDefaultLocationWithNoOptions() {

      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(defaultLocation.getId()).andReturn("foo");
      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(defaultOptions);
      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = new TemplateBuilderImpl(ImmutableSet.<Location> of(),
               ImmutableSet.<Image> of(), ImmutableSet.<Size> of(), defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("foo").build();
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
   public void testDefaultLocationWithOptions() {

      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      expect(defaultLocation.getId()).andReturn("foo");

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = new TemplateBuilderImpl(ImmutableSet.<Location> of(),
               ImmutableSet.<Image> of(), ImmutableSet.<Size> of(), defaultLocation,
               optionsProvider, templateBuilderProvider);

      try {
         template.imageId("foo").options(TemplateOptions.NONE).build();
         assert false;
      } catch (NoSuchElementException e) {

      }

      verify(defaultLocation);
      verify(optionsProvider);
      verify(templateBuilderProvider);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testImageIdNullsEverythingElse() {
      Location defaultLocation = createMock(Location.class);
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);

      replay(defaultLocation);
      replay(optionsProvider);
      replay(templateBuilderProvider);

      TemplateBuilderImpl template = new TemplateBuilderImpl(ImmutableSet.<Location> of(),
               ImmutableSet.<Image> of(), ImmutableSet.<Size> of(), defaultLocation,
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
