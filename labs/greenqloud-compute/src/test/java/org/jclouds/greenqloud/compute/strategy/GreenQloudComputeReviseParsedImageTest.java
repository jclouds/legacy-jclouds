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
package org.jclouds.greenqloud.compute.strategy;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.config.EC2ComputeServiceDependenciesModule;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.strategy.EC2PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.xml.DescribeImagesResponseHandlerTest;
import org.jclouds.greenqloud.compute.strategy.GreenQloudComputeReviseParsedImage;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "GreenQloudComputeReviseParsedImageTest")
public class GreenQloudComputeReviseParsedImageTest {

   public void testParseGreenQloudImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/greenqloud_images.xml");
      assertEquals(result.size(), 9);

      assertEquals(
            Iterables.get(result, 0).toString(),
            new ImageBuilder()
                  .operatingSystem(
                        OperatingSystem.builder().family(OsFamily.UBUNTU).version("10.04")
                              .description("Ubuntu Server 10.04 (64-bit)").is64Bit(true)
                              .arch("paravirtual").build())
                  .description("Ubuntu Server 10.04 (64-bit)")
                  .name("Ubuntu Server 10.04.3")
                  .defaultCredentials(LoginCredentials.builder().user("root").build())
                  .id("us-east-1/qmi-4e5b842f")
                  .providerId("qmi-4e5b842f")
                  .location(defaultLocation)
                  .userMetadata(
                        ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                              "paravirtual", "hypervisor", "xen"))
                  .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());
      assertEquals(Iterables.get(result, 0).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);

      assertEquals(
            Iterables.get(result, 1).toString(),
            new ImageBuilder()
                  .operatingSystem(
                        OperatingSystem.builder().family(OsFamily.UBUNTU).version("11.10")
                              .description("Ubuntu Server 11.10 (64-bit)").is64Bit(true)
                              .arch("paravirtual").build())
                  .description("Ubuntu Server 11.10 (64-bit)")
                  .name("Ubuntu Server 11.10")
                  .defaultCredentials(LoginCredentials.builder().user("root").build())
                  .id("us-east-1/qmi-9ac92558")
                  .providerId("qmi-9ac92558")
                  .location(defaultLocation)
                  .userMetadata(
                        ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                              "paravirtual", "hypervisor", "xen"))
                  .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());
      assertEquals(Iterables.get(result, 1).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);
      
      assertEquals(
               Iterables.get(result, 2).toString(),
               new ImageBuilder()
                  .operatingSystem(
                        OperatingSystem.builder().family(OsFamily.CENTOS).version("5.5")
                              .description("CentOS 5.5 Server 64-bit").is64Bit(true)
                              .arch("paravirtual").build())
                  .description("CentOS 5.5 Server 64-bit")
                  .name("CentOS 5.5 Server")
                  .defaultCredentials(LoginCredentials.builder().user("root").build())
                  .id("us-east-1/qmi-33a467aa")
                  .providerId("qmi-33a467aa")
                  .location(defaultLocation)
                  .userMetadata(
                        ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                              "paravirtual", "hypervisor", "xen"))
                  .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());               
         assertEquals(Iterables.get(result, 2).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);
         
         assertEquals(
                  Iterables.get(result, 3).toString(),
                  new ImageBuilder()
                     .operatingSystem(
                           OperatingSystem.builder().family(OsFamily.DEBIAN).version("6.0")
                                 .description("Debian 6.0 (64-bit)").is64Bit(true)
                                 .arch("paravirtual").build())
                     .description("Debian 6.0 (64-bit)")
                     .name("Debian 6.0")
                     .defaultCredentials(LoginCredentials.builder().user("root").build())
                     .id("us-east-1/qmi-f2a9d2ba")
                     .providerId("qmi-f2a9d2ba")
                     .location(defaultLocation)
                     .userMetadata(
                           ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                                 "paravirtual", "hypervisor", "xen"))
                     .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());               
         assertEquals(Iterables.get(result, 3).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);         

         assertEquals(
                  Iterables.get(result, 4).toString(),
                  new ImageBuilder()
                     .operatingSystem(
                           OperatingSystem.builder().family(OsFamily.CENTOS).version("6.0")
                                 .description("CentOS 6.0 (64-bit)").is64Bit(true)
                                 .arch("paravirtual").build())
                     .description("CentOS 6.0 (64-bit)")
                     .name("CentOS 6.0")
                     .defaultCredentials(LoginCredentials.builder().user("root").build())
                     .id("us-east-1/qmi-96f82145")
                     .providerId("qmi-96f82145")
                     .location(defaultLocation)
                     .userMetadata(
                           ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                                 "paravirtual", "hypervisor", "xen"))
                     .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());               
            assertEquals(Iterables.get(result, 4).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);         

            assertEquals(
                     Iterables.get(result, 5).toString(),
                     new ImageBuilder()
                        .operatingSystem(
                              OperatingSystem.builder().family(OsFamily.CENTOS).version("6.0")
                                    .description("CentOS 6.0 GUI (64-bit)").is64Bit(true)
                                    .arch("paravirtual").build())
                        .description("CentOS 6.0 GUI (64-bit)")
                        .name("CentOS 6.0 GUI")
                        .defaultCredentials(LoginCredentials.builder().user("root").build())
                        .id("us-east-1/qmi-42e877f6")
                        .providerId("qmi-42e877f6")
                        .location(defaultLocation)
                        .userMetadata(
                              ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                                    "paravirtual", "hypervisor", "xen"))
                        .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());               
            assertEquals(Iterables.get(result, 5).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);      

            assertEquals(
                     Iterables.get(result, 6).toString(),
                     new ImageBuilder()
                           .operatingSystem(
                                 OperatingSystem.builder().family(OsFamily.UBUNTU).version("11.04")
                                       .description("Ubuntu Server 11.04 (64-bit)").is64Bit(true)
                                       .arch("paravirtual").build())
                           .description("Ubuntu Server 11.04 (64-bit)")
                           .name("Ubuntu Server 11.04")
                           .defaultCredentials(LoginCredentials.builder().user("root").build())
                           .id("us-east-1/qmi-eed8cea7")
                           .providerId("qmi-eed8cea7")
                           .location(defaultLocation)
                           .userMetadata(
                                 ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                                       "paravirtual", "hypervisor", "xen"))
                           .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());
            assertEquals(Iterables.get(result, 6).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);

            assertEquals(
                     Iterables.get(result, 7).toString(),
                     new ImageBuilder()
                        .operatingSystem(
                              // fedora is not in the image version map, yet
                              OperatingSystem.builder().family(OsFamily.FEDORA).version("")
                                    .description("Fedora 16 Server").is64Bit(false)
                                    .arch("paravirtual").build())
                        .description("Fedora 16 Server")
                        .name("Fedora 16 Server")
                        .defaultCredentials(LoginCredentials.builder().user("root").build())
                        .id("us-east-1/qmi-fa4bdae0")
                        .providerId("qmi-fa4bdae0")
                        .location(defaultLocation)
                        .userMetadata(
                              ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                                    "paravirtual", "hypervisor", "xen"))
                        .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());               
            assertEquals(Iterables.get(result, 7).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);      
           
            assertEquals(
                     Iterables.get(result, 8).toString(),
                     new ImageBuilder()
                        .operatingSystem(
                              OperatingSystem.builder().family(OsFamily.CENTOS).version("5.6")
                                    .description("CentOS 5.6 Server GUI 64-bit").is64Bit(true)
                                    .arch("paravirtual").build())
                        .description("CentOS 5.6 Server GUI 64-bit")
                        .name("CentOS 5.6 GUI")
                        .defaultCredentials(LoginCredentials.builder().user("root").build())
                        .id("us-east-1/qmi-93271d32")
                        .providerId("qmi-93271d32")
                        .location(defaultLocation)
                        .userMetadata(
                              ImmutableMap.of("owner", "admin", "rootDeviceType", "ebs", "virtualizationType",
                                    "paravirtual", "hypervisor", "xen"))
                        .status(org.jclouds.compute.domain.Image.Status.AVAILABLE).backendStatus("available").build().toString());               
            assertEquals(Iterables.get(result, 8).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);      

   }

   static Location defaultLocation = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1").description(
            "us-east-1").build();

   public static Set<org.jclouds.compute.domain.Image> convertImages(String resource) {

      Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
      }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
               .getInstance(Json.class));

      // note this method is what mandates the location id as us-east-1
      Set<Image> result = DescribeImagesResponseHandlerTest.parseImages(resource);
      EC2ImageParser parser = new EC2ImageParser(EC2ComputeServiceDependenciesModule.toPortableImageStatus,
               new EC2PopulateDefaultLoginCredentialsForImageStrategy(), map, Suppliers
                        .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
                        .ofInstance(defaultLocation), new GreenQloudComputeReviseParsedImage(map));
      return Sets.newLinkedHashSet(Iterables.filter(Iterables.transform(result, parser), Predicates.notNull()));
   }

}
