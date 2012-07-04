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
package org.jclouds.aws.ec2.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService.getJavaMethodForRequestAtIndex;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.OsFamilyVersion64Bit;
import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.EC2TemplateBuilderLiveTest;
import org.jclouds.ec2.compute.predicates.EC2ImagePredicates;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.options.DescribeAvailabilityZonesOptions;
import org.jclouds.ec2.options.DescribeRegionsOptions;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService;
import org.jclouds.location.reference.LocationConstants;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "AWSEC2TemplateBuilderLiveTest")
public class AWSEC2TemplateBuilderLiveTest extends EC2TemplateBuilderLiveTest {

   public AWSEC2TemplateBuilderLiveTest() {
      provider = "aws-ec2";
   }

   @Override
   protected Predicate<OsFamilyVersion64Bit> defineUnsupportedOperatingSystems() {
      return Predicates.not(new Predicate<OsFamilyVersion64Bit>() {

         @Override
         public boolean apply(OsFamilyVersion64Bit input) {
            switch (input.family) {
            case UBUNTU:
               return true;
            case DEBIAN:
               return true;
            case RHEL:
               return input.version.matches("5.[06]") || input.version.equals("");
            case CENTOS:
               return input.version.matches("5.[0246]") || (input.version.equals("6.0") && input.is64Bit)
                     || input.version.equals("");
            case WINDOWS:
               return input.version.matches("200[38]") || (input.version.equals("7") && !input.is64Bit)
                     || input.version.equals("");
            default:
               return false;
            }
         }

      });
   }

   @Test
   public void testTemplateBuilderM1MEDIUMWithNegativeLookaroundDoesntMatchTestImages() {

      Template template = view.getComputeService().templateBuilder().hardwareId(InstanceType.M1_MEDIUM)
      // need to select versions with double-digits so that lexicographic
      // doesn't end up prefering 9.x vs 11.x
            .osVersionMatches("1[012].[10][04]")
            // negative lookahead for daily and testing, but ensure match
            // ubuntu-images
            // http://www.regular-expressions.info/lookaround.html
            .imageDescriptionMatches("^(?!.*(daily|testing)).*ubuntu-images.*$").osFamily(OsFamily.UBUNTU).build();

      assert (template.getImage().getProviderId().startsWith("ami-")) : template;
      assert template.getImage().getDescription().indexOf("test") == -1 : template;
      assert template.getImage().getDescription().indexOf("daily") == -1 : template;
      assertEquals(template.getImage().getVersion(), "20100224");
      assertEquals(template.getImage().getOperatingSystem().getVersion(), "10.04");
      assertEquals(template.getImage().getOperatingSystem().is64Bit(), false);
      assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
      assertEquals(template.getLocation().getId(), "us-east-1");
      assertEquals(getCores(template.getHardware()), 1.0d);
      assertEquals(template.getHardware().getId(), InstanceType.M1_MEDIUM);
      assertEquals(template.getImage().getOperatingSystem().getArch(), "paravirtual");
   }

   @Test
   public void testUbuntuInstanceStoreGoesM1SmallNegativeLookaroundDoesntMatchTestImages() {

      Template template = view.getComputeService().templateBuilder()
            .imageMatches(EC2ImagePredicates.rootDeviceType(RootDeviceType.INSTANCE_STORE))
            // need to select versions with double-digits so that lexicographic
            // doesn't end up prefering 9.x vs 11.x
            .osVersionMatches("1[012].[10][04]")
            // negative lookahead for daily and testing, but ensure match
            // ubuntu-images
            // http://www.regular-expressions.info/lookaround.html
            .imageDescriptionMatches("^(?!.*(daily|testing)).*ubuntu-images.*$").osFamily(OsFamily.UBUNTU).build();

      assert (template.getImage().getProviderId().startsWith("ami-")) : template;
      assert template.getImage().getDescription().indexOf("test") == -1 : template;
      assert template.getImage().getDescription().indexOf("daily") == -1 : template;
      assertEquals(template.getImage().getOperatingSystem().getVersion(), "10.04");
      assertEquals(template.getImage().getOperatingSystem().is64Bit(), false);
      assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
      assertEquals(template.getLocation().getId(), "us-east-1");
      assertEquals(getCores(template.getHardware()), 1.0d);
      assertEquals(template.getHardware().getId(), InstanceType.M1_SMALL);
      assertEquals(template.getImage().getOperatingSystem().getArch(), "paravirtual");
   }

   @Test
   public void testTemplateBuilderCanUseImageIdAndhardwareIdAndAZ() {

      Template template = view.getComputeService().templateBuilder().imageId("us-east-1/ami-ccb35ea5")
            .hardwareId(InstanceType.M2_2XLARGE).locationId("us-east-1a").build();

      System.out.println(template.getHardware());
      assert (template.getImage().getProviderId().startsWith("ami-")) : template;
      assertEquals(template.getImage().getOperatingSystem().getVersion(), "5.4");
      assertEquals(template.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
      assertEquals(template.getImage().getVersion(), "4.4.10");
      assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
      assertEquals(template.getLocation().getId(), "us-east-1a");
      assertEquals(template.getImage().getLocation().getId(), "us-east-1");
      assertEquals(getCores(template.getHardware()), 4.0d);
      assertEquals(template.getHardware().getId(), InstanceType.M2_2XLARGE);
      assertEquals(template.getImage().getOperatingSystem().getArch(), "paravirtual");
   }

   @Test
   public void testDefaultTemplateBuilder() throws IOException {

      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      assert (defaultTemplate.getImage().getProviderId().startsWith("ami-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "pv-2012.03.3");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(defaultTemplate.getImage().getUserMetadata().get("rootDeviceType"), "ebs");
      assertEquals(defaultTemplate.getLocation().getId(), "us-east-1");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getArch(), "paravirtual");
   }

   @Test
   public void testAmazonLinuxInstanceStore() throws IOException {

      Template defaultTemplate = view.getComputeService().templateBuilder().osFamily(OsFamily.AMZN_LINUX)
            .imageMatches(EC2ImagePredicates.rootDeviceType(RootDeviceType.INSTANCE_STORE)).build();
      assert (defaultTemplate.getImage().getProviderId().startsWith("ami-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getVersion(), "pv-2012.03.3");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(defaultTemplate.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
      assertEquals(defaultTemplate.getLocation().getId(), "us-east-1");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getArch(), "paravirtual");
   }

   @Test
   public void testFastestTemplateBuilder() throws IOException {
      Template fastestTemplate = view.getComputeService().templateBuilder().fastest().osFamily(OsFamily.AMZN_LINUX)
            .build();
      assert (fastestTemplate.getImage().getProviderId().startsWith("ami-")) : fastestTemplate;
      assertEquals(fastestTemplate.getHardware().getProviderId(), InstanceType.CC2_8XLARGE);
      assertEquals(fastestTemplate.getImage().getOperatingSystem().getVersion(), "2011.09.2");
      assertEquals(fastestTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(fastestTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
      assertEquals(fastestTemplate.getImage().getUserMetadata().get("rootDeviceType"), "ebs");
      assertEquals(fastestTemplate.getLocation().getId(), "us-east-1");
      assertEquals(getCores(fastestTemplate.getHardware()), 16.0d);
      assertEquals(fastestTemplate.getImage().getOperatingSystem().getArch(), "hvm");

      fastestTemplate = view.getComputeService().templateBuilder().fastest().build();
      System.out.println(fastestTemplate.getImage());
      assert (fastestTemplate.getImage().getProviderId().startsWith("ami-")) : fastestTemplate;
      assertEquals(fastestTemplate.getHardware().getProviderId(), InstanceType.CC2_8XLARGE);
      assertEquals(fastestTemplate.getImage().getOperatingSystem().getVersion(), "12.04");
      assertEquals(fastestTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(fastestTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(fastestTemplate.getImage().getUserMetadata().get("rootDeviceType"), "ebs");
      assertEquals(fastestTemplate.getLocation().getId(), "us-east-1");
      assertEquals(getCores(fastestTemplate.getHardware()), 16.0d);
      assertEquals(fastestTemplate.getImage().getOperatingSystem().getArch(), "hvm");
   }

   @Test
   public void testTemplateBuilderMicro() throws IOException {

      Template microTemplate = view.getComputeService().templateBuilder().hardwareId(InstanceType.T1_MICRO)
            .osFamily(OsFamily.UBUNTU).osVersionMatches("10.10").os64Bit(true).build();

      assert (microTemplate.getImage().getProviderId().startsWith("ami-")) : microTemplate;
      assertEquals(microTemplate.getImage().getOperatingSystem().getVersion(), "10.10");
      assertEquals(microTemplate.getImage().getOperatingSystem().is64Bit(), true);
      assertEquals(microTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(microTemplate.getImage().getUserMetadata().get("rootDeviceType"), "ebs");
      assertEquals(microTemplate.getLocation().getId(), "us-east-1");
      assertEquals(getCores(microTemplate.getHardware()), 1.0d);
      assertEquals(microTemplate.getImage().getOperatingSystem().getArch(), "paravirtual");
   }

   @Test
   public void testTemplateBuilderWithNoOwnersParsesImageOnDemand() throws IOException {
      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         // set owners to nothing
         overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "");
         overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");

         context = createView(overrides, setupModules());

         assertEquals(context.getComputeService().listImages().size(), 0);

         Template template = context.getComputeService().templateBuilder().imageId("us-east-1/ami-ccb35ea5").build();
         System.out.println(template.getHardware());
         assert (template.getImage().getProviderId().startsWith("ami-")) : template;
         assertEquals(template.getImage().getOperatingSystem().getVersion(), "5.4");
         assertEquals(template.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
         assertEquals(template.getImage().getVersion(), "4.4.10");
         assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
         assertEquals(template.getLocation().getId(), "us-east-1");
         assertEquals(getCores(template.getHardware()), 1.0d);
         assertEquals(template.getHardware().getId(), "m1.small");

         // ensure we cache the new image for next time
         assertEquals(context.getComputeService().listImages().size(), 1);

      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testTemplateBuilderWithNoOwnersParsesImageOnDemandDeprecated() throws IOException {
      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         // set owners to nothing
         overrides.setProperty(EC2Constants.PROPERTY_EC2_AMI_OWNERS, "");
         overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");

         context = createView(overrides, setupModules());

         assertEquals(context.getComputeService().listImages().size(), 0);

         Template template = context.getComputeService().templateBuilder().imageId("us-east-1/ami-ccb35ea5").build();
         System.out.println(template.getHardware());
         assert (template.getImage().getProviderId().startsWith("ami-")) : template;
         assertEquals(template.getImage().getOperatingSystem().getVersion(), "5.4");
         assertEquals(template.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.CENTOS);
         assertEquals(template.getImage().getVersion(), "4.4.10");
         assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
         assertEquals(template.getLocation().getId(), "us-east-1");
         assertEquals(getCores(template.getHardware()), 1.0d);
         assertEquals(template.getHardware().getId(), "m1.small");

         // ensure we cache the new image for next time
         assertEquals(context.getComputeService().listImages().size(), 1);

      } finally {
         if (context != null)
            context.close();
      }
   }

   @Test
   public void testTemplateBuilderWithLessRegions() throws IOException, SecurityException, NoSuchMethodException {
      ComputeServiceContext context = null;
      try {
         Properties overrides = setupProperties();
         // set regions to only 1
         overrides.setProperty(LocationConstants.PROPERTY_REGIONS, Region.EU_WEST_1);
         overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_REGIONS, "");
         overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "");
         overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");

         final List<HttpCommand> commandsInvoked = Lists.newArrayList();
         
         context = createView(
               overrides,
               ImmutableSet.<Module> of(new Log4JLoggingModule(),
                     TrackingJavaUrlHttpCommandExecutorService.newTrackingModule(commandsInvoked)));

         assert context.getComputeService().listAssignableLocations().size() < this.view.getComputeService()
               .listAssignableLocations().size();

         assertOnlyOneRegionQueriedForAvailabilityZone(commandsInvoked);

         assert context.getComputeService().listImages().size() < this.view.getComputeService().listImages().size();

         Template template = context.getComputeService().templateBuilder().imageId("eu-west-1/ami-a33b06d7").build();
         assert (template.getImage().getProviderId().startsWith("ami-")) : template;
         assertEquals(template.getImage().getOperatingSystem().getVersion(), "2011.09.2");
         assertEquals(template.getImage().getOperatingSystem().is64Bit(), true);
         assertEquals(template.getImage().getOperatingSystem().getFamily(), OsFamily.AMZN_LINUX);
         assertEquals(template.getImage().getVersion(), "2011.09.2");
         assertEquals(template.getImage().getUserMetadata().get("rootDeviceType"), "instance-store");
         assertEquals(template.getLocation().getId(), "eu-west-1");
         assertEquals(getCores(template.getHardware()), 1.0d);
         assertEquals(template.getHardware().getId(), "m1.small");

      } finally {
         if (context != null)
            context.close();
      }
   }

   private static void assertOnlyOneRegionQueriedForAvailabilityZone(List<HttpCommand> commandsInvoked)
         throws NoSuchMethodException {
      assert commandsInvoked.size() == 2 : commandsInvoked;
      assertEquals(getJavaMethodForRequestAtIndex(commandsInvoked, 0),
            AvailabilityZoneAndRegionAsyncClient.class.getMethod("describeRegions", DescribeRegionsOptions[].class));
      assertEquals(getJavaMethodForRequestAtIndex(commandsInvoked, 1),
            AvailabilityZoneAndRegionAsyncClient.class.getMethod("describeAvailabilityZonesInRegion", String.class,
                  DescribeAvailabilityZonesOptions[].class));
   }

   @Test
   public void testTemplateBuilderCanUseImageIdFromNonDefaultOwner() {
      // This is the id of a public image, not owned by one of the four default
      // owners
      String imageId = "us-east-1/ami-44d02f2d";
      Template defaultTemplate = view.getComputeService().templateBuilder().imageId(imageId)
            .imageMatches(EC2ImagePredicates.rootDeviceType(RootDeviceType.INSTANCE_STORE)).build();
      assert (defaultTemplate.getImage().getProviderId().startsWith("ami-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getId(), imageId);
   }
   
   @Test
   public void testAssignability() {
      view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN);
      view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN);
   }

   @Override
   protected Set<String> getIso3166Codes() {
      return ImmutableSet.<String> of("US-VA", "US-CA", "US-OR", "BR-SP", "IE", "SG", "JP-13");
   }

}
