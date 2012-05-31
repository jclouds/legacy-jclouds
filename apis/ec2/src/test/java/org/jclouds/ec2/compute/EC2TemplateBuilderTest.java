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
package org.jclouds.ec2.compute;

import static java.lang.String.format;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.c1_medium;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.c1_xlarge;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.cc1_4xlarge;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m1_large;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m1_small;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m1_xlarge;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m2_2xlarge;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m2_4xlarge;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.m2_xlarge;
import static org.jclouds.ec2.compute.domain.EC2HardwareBuilder.t1_micro;
import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Provider;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.ImagesToRegionAndIdMap;
import org.jclouds.ec2.compute.internal.EC2TemplateBuilderImpl;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests compute service specifically to EC2.
 * 
 * These tests are designed to verify the local functionality of jclouds, rather than the
 * interaction with Amazon Web Services.
 * 
 * @see EC2ComputeServiceLiveTest
 * 
 * @author Oleksiy Yarmula
 */
@Test(testName = "EC2TemplateBuilderTest")
public class EC2TemplateBuilderTest {
   Location provider = new LocationBuilder().scope(LocationScope.PROVIDER).id("aws-ec2").description("aws-ec2").build();

   protected Location location = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1").description("us-east-1")
         .parent(provider).build();

   public static final Hardware CC1_4XLARGE = cc1_4xlarge().supportsImageIds(ImmutableSet.of("us-east-1/cc-image"))
            .build();

   /**
    * Verifies that {@link TemplateBuilderImpl} would choose the correct size of the instance, based
    * on {@link org.jclouds.compute.domain.Hardware} from {@link EC2Hardware}.
    * 
    * Expected size: m2.xlarge
    */
   @Test
   public void testTemplateChoiceForInstanceByhardwareId() throws Exception {
      Template template = newTemplateBuilder().os64Bit(true).hardwareId("m2.xlarge").locationId("us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      // assert m2_xlarge().build().equals(template.getHardware()) : format(
      // "Incorrect image determined by the template. Expected: %s. Found: %s.", "m2.xlarge",
      // String.valueOf(template.getHardware()));
      assertEquals(m2_xlarge().build().getId(), template.getHardware().getId());
   }

   @Test
   public void testTemplateChoiceForInstanceByCChardwareId() throws Exception {
      Template template = newTemplateBuilder().fastest().build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert CC1_4XLARGE.equals(template.getHardware()) : format(
               "Incorrect image determined by the template. Expected: %s. Found: %s.", CC1_4XLARGE.getId(), template
                        .getHardware().getId());
   }

   /**
    * Verifies that {@link TemplateBuilderImpl} would choose the correct size of the instance, based
    * on physical attributes (# of cores, ram, etc).
    * 
    * Expected size: CC1_4XLARGE
    */
   @Test
   public void testTemplateChoiceForInstanceByAttributes() throws Exception {
      Template template = newTemplateBuilder().os64Bit(true).minRam(17510).minCores(6.5).smallest().locationId(
               "us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assertEquals(template.getHardware().getId(), "cc1.4xlarge");
   }

   /**
    * Negative test version of {@link #testTemplateChoiceForInstanceByAttributes}.
    * 
    * Verifies that {@link TemplateBuilderImpl} would not choose the insufficient size of the
    * instance, based on physical attributes (# of cores, ram, etc).
    * 
    * Expected size: anything but m2.xlarge
    */
   @Test
   public void testNegativeTemplateChoiceForInstanceByAttributes() throws Exception {
      Template template = newTemplateBuilder().os64Bit(true).minRam(17510).minCores(6.7).smallest().locationId(
               "us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert !m2_xlarge().build().equals(template.getHardware()) : format(
               "Incorrect image determined by the template. Expected: not %s. Found: %s.", "m2.xlarge", template
                        .getHardware().getId());
   }

   @Test
   public void testTemplateChoiceForInstanceByImageId() throws Exception {
      Template template = newTemplateBuilder().imageId("us-east-1/cc-image").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assertEquals(template.getImage().getId(), "us-east-1/cc-image");
   }

   @Test
   public void testTemplateChoiceForInstanceByImageIdDoesNotGetAllImages() throws Exception {
      @SuppressWarnings("unchecked")
      Supplier<Set<? extends Image>> images = createMock(Supplier.class);
      replay(images);
      
      final Image image = new ImageBuilder().providerId("cc-image").name("image").id("us-east-1/cc-image").location(location)
               .operatingSystem(new OperatingSystem(OsFamily.UBUNTU, null, "1.0", "hvm", "ubuntu", true))
               .description("description").version("1.0").defaultCredentials(new LoginCredentials("root", false))
               .status(Image.Status.AVAILABLE)
               .build();
      Map<RegionAndName, Image> imageMap = ImmutableMap.of(
               new RegionAndName(image.getLocation().getId(), image.getProviderId()), image);
      
      // weird compilation error means have to declare extra generics for call to build() - see https://bugs.eclipse.org/bugs/show_bug.cgi?id=365818
      Supplier<LoadingCache<RegionAndName, ? extends Image>> imageCache = Suppliers.<LoadingCache<RegionAndName, ? extends Image>> ofInstance(
               CacheBuilder.newBuilder().<RegionAndName,Image>build(CacheLoader.from(Functions.forMap(imageMap))));

      Template template = newTemplateBuilder(images, imageCache).imageId("us-east-1/cc-image").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assertEquals(template.getImage().getId(), "us-east-1/cc-image");
   }

   @Test(expectedExceptions={NoSuchElementException.class})
   public void testNegativeTemplateChoiceForInstanceByImageId() throws Exception {
      newTemplateBuilder().imageId("wrongregion/wrongimageid").build();
   }

   private TemplateBuilder newTemplateBuilder() {
      final Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet.<Image> of(
               new ImageBuilder().providerId("cc-image").name("image").id("us-east-1/cc-image").location(location)
                        .operatingSystem(new OperatingSystem(OsFamily.UBUNTU, null, "1.0", "hvm", "ubuntu", true))
                        .description("description").version("1.0").defaultCredentials(new LoginCredentials("root", false))
                        .status(Image.Status.AVAILABLE)
                        .build(), 
               new ImageBuilder().providerId("normal-image").name("image").id("us-east-1/normal-image").location(location)
                        .operatingSystem(new OperatingSystem(OsFamily.UBUNTU, null, "1.0", "paravirtual", "ubuntu", true))
                        .description("description").version("1.0").defaultCredentials(new LoginCredentials("root", false))
                        .status(Image.Status.AVAILABLE)
                        .build()));
      
      // weird compilation error means have to cast this - see https://bugs.eclipse.org/bugs/show_bug.cgi?id=365818
      @SuppressWarnings("unchecked")
      ImmutableMap<RegionAndName, Image> imageMap = (ImmutableMap<RegionAndName, Image>) ImagesToRegionAndIdMap.imagesToMap(images.get());
      Supplier<LoadingCache<RegionAndName, ? extends Image>> imageCache = Suppliers.<LoadingCache<RegionAndName, ? extends Image>> ofInstance(
               CacheBuilder.newBuilder().<RegionAndName,Image>build(CacheLoader.from(Functions.forMap(imageMap))));

      return newTemplateBuilder(images, imageCache);
   }

   @SuppressWarnings("unchecked")
   private TemplateBuilder newTemplateBuilder(Supplier<Set<? extends Image>> images, Supplier<LoadingCache<RegionAndName, ? extends Image>> imageCache) {
      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      replay(optionsProvider);
      replay(templateBuilderProvider);
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
               .<Location> of(location));
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
               .<Hardware> of(t1_micro().build(), c1_medium().build(), c1_xlarge().build(), m1_large().build(),
                        m1_small().build(), m1_xlarge().build(), m2_xlarge().build(), m2_2xlarge().build(),
                        m2_4xlarge().build(), CC1_4XLARGE));

      return new EC2TemplateBuilderImpl(locations, images, sizes, Suppliers.ofInstance(location), optionsProvider,
               templateBuilderProvider, imageCache) {
      };
   }
   
   Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getProviderId();
         }
      };
   }
}
