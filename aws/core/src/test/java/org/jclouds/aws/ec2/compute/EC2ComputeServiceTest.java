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

package org.jclouds.aws.ec2.compute;

import static java.lang.String.format;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.Set;

import javax.inject.Provider;

import org.jclouds.aws.ec2.compute.domain.EC2Hardware;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.domain.internal.TemplateBuilderImpl;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Tests compute service specifically to EC2.
 * 
 * These tests are designed to verify the local functionality of jclouds, rather
 * than the interaction with Amazon Web Services.
 * 
 * @see EC2ComputeServiceLiveTest
 * 
 * @author Oleksiy Yarmula
 */
public class EC2ComputeServiceTest {
   private static final Location location = new LocationImpl(LocationScope.REGION, "us-east-1", "us east", null);

   public static final EC2Hardware CC1_4XLARGE = new EC2Hardware(location, InstanceType.CC1_4XLARGE, ImmutableList.of(
         new Processor(4.0, 4.0), new Processor(4.0, 4.0)), 23 * 1024, 1690, new String[] { "us-east-1/cc-image" });

   /**
    * Verifies that {@link TemplateBuilderImpl} would choose the correct size of
    * the instance, based on {@link org.jclouds.compute.domain.Hardware} from
    * {@link EC2Hardware}.
    * 
    * Expected size: m2.xlarge
    */
   @Test
   public void testTemplateChoiceForInstanceByhardwareId() throws Exception {
      Template template = newTemplateBuilder().os64Bit(true).hardwareId("m2.xlarge").locationId("us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert EC2Hardware.M2_XLARGE.equals(template.getHardware()) : format(
            "Incorrect image determined by the template. Expected: %s. Found: %s.", "m2.xlarge",
            String.valueOf(template.getHardware()));
   }

   @Test
   public void testTemplateChoiceForInstanceByCChardwareId() throws Exception {
      Template template = newTemplateBuilder().fastest().build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert CC1_4XLARGE.equals(template.getHardware()) : format(
            "Incorrect image determined by the template. Expected: %s. Found: %s.", CC1_4XLARGE.getId(),
            String.valueOf(template.getHardware()));
   }

   /**
    * Verifies that {@link TemplateBuilderImpl} would choose the correct size of
    * the instance, based on physical attributes (# of cores, ram, etc).
    * 
    * Expected size: m2.xlarge
    */
   @Test
   public void testTemplateChoiceForInstanceByAttributes() throws Exception {
      Template template = newTemplateBuilder().os64Bit(true).minRam(17510).minCores(6.5).smallest()
            .locationId("us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert CC1_4XLARGE.equals(template.getHardware()) : format(
            "Incorrect image determined by the template. Expected: %s. Found: %s.", CC1_4XLARGE,
            String.valueOf(template.getHardware()));
   }

   /**
    * Negative test version of
    * {@link #testTemplateChoiceForInstanceByAttributes}.
    * 
    * Verifies that {@link TemplateBuilderImpl} would not choose the
    * insufficient size of the instance, based on physical attributes (# of
    * cores, ram, etc).
    * 
    * Expected size: anything but m2.xlarge
    */
   @Test
   public void testNegativeTemplateChoiceForInstanceByAttributes() throws Exception {
      Template template = newTemplateBuilder().os64Bit(true).minRam(17510).minCores(6.7).smallest()
            .locationId("us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert !EC2Hardware.M2_XLARGE.equals(template.getHardware()) : format(
            "Incorrect image determined by the template. Expected: not %s. Found: %s.", "m2.xlarge",
            String.valueOf(template.getHardware()));
   }

   @SuppressWarnings("unchecked")
   private TemplateBuilder newTemplateBuilder() {

      Provider<TemplateOptions> optionsProvider = createMock(Provider.class);
      Provider<TemplateBuilder> templateBuilderProvider = createMock(Provider.class);
      TemplateOptions defaultOptions = createMock(TemplateOptions.class);

      expect(optionsProvider.get()).andReturn(defaultOptions);

      Image image = new ImageImpl("cc-image", "image", "us-east-1/cc-image", location, null,
            Maps.<String, String> newHashMap(),
            new OperatingSystem(OsFamily.UBUNTU, null, "1.0", null, "ubuntu", true), "description", "1.0",
            new Credentials("root", null));
      replay(optionsProvider);
      replay(templateBuilderProvider);
      Supplier<Set<? extends Location>> locations = Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet
            .<Location> of(location));
      Supplier<Set<? extends Image>> images = Suppliers.<Set<? extends Image>> ofInstance(ImmutableSet
            .<Image> of(image));
      Supplier<Set<? extends Hardware>> sizes = Suppliers.<Set<? extends Hardware>> ofInstance(ImmutableSet
            .<Hardware> of(EC2Hardware.T1_MICRO, EC2Hardware.C1_MEDIUM, EC2Hardware.C1_XLARGE, EC2Hardware.M1_LARGE,
                  EC2Hardware.M1_SMALL, EC2Hardware.M1_XLARGE, EC2Hardware.M2_XLARGE, EC2Hardware.M2_2XLARGE,
                  EC2Hardware.M2_4XLARGE, CC1_4XLARGE));

      return new TemplateBuilderImpl(locations, images, sizes, Suppliers.ofInstance(location), optionsProvider,
            templateBuilderProvider) {

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
