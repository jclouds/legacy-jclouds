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

package org.jclouds.aws.ec2.compute.strategy;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.strategy.EC2PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.xml.DescribeImagesResponseHandlerTest;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class AWSEC2ImageParserTest {
   public void testParseAlesticCanonicalImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/alestic_canonical.xml");

      assertEquals(Iterables.get(result, 0), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UBUNTU).arch("paravirtual").version("8.04").description(
                        "ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml").is64Bit(false).build())
               .description("ubuntu-images-us/ubuntu-hardy-8.04-i386-server-20091130.manifest.xml").defaultCredentials(
                        new Credentials("ubuntu", null)).id("us-east-1/ami-7e28ca17").providerId("ami-7e28ca17")
               .location(defaultLocation).version("20091130").userMetadata(
                        ImmutableMap.of("owner", "099720109477", "rootDeviceType", "instance-store")).build());

      assertEquals(Iterables.get(result, 4), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UBUNTU).arch("paravirtual").version("8.04").description(
                        "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml").is64Bit(false).build()).description(
               "alestic/ubuntu-8.04-hardy-base-20080905.manifest.xml").defaultCredentials(
               new Credentials("ubuntu", null)).id("us-east-1/ami-c0fa1ea9").providerId("ami-c0fa1ea9").location(
               defaultLocation).version("20080905").userMetadata(
               ImmutableMap.of("owner", "063491364108", "rootDeviceType", "instance-store")).build());

      assertEquals(Iterables.get(result, 6), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UBUNTU).arch("paravirtual").version("10.04").description(
                        "099720109477/ebs/ubuntu-images/ubuntu-lucid-10.04-i386-server-20100827").is64Bit(false)
                        .build()).description("099720109477/ebs/ubuntu-images/ubuntu-lucid-10.04-i386-server-20100827")
               .defaultCredentials(new Credentials("ubuntu", null)).id("us-east-1/ami-10f3a255").providerId(
                        "ami-10f3a255").location(defaultLocation).version("20100827").userMetadata(
                        ImmutableMap.of("owner", "099720109477", "rootDeviceType", "ebs")).build());

   }

   public void testParseVostokImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/vostok.xml");

      assertEquals(Iterables.get(result, 0), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UNRECOGNIZED).arch("paravirtual").version("").description(
                        "vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml").is64Bit(false).build())
               .description("vostok-builds/vostok-0.95-5622/vostok-0.95-5622.manifest.xml").defaultCredentials(
                        new Credentials("root", null)).id("us-east-1/ami-870de2ee").providerId("ami-870de2ee")
               .location(defaultLocation).version("5622").userMetadata(
                        ImmutableMap.of("owner", "133804938231", "rootDeviceType", "instance-store")).build());

   }

   public void testParseCCImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/describe_images_cc.xml");

      assertEquals(Iterables.get(result, 0), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.CENTOS).arch("hvm").version("5.4").description(
                        "amazon/EC2 CentOS 5.4 HVM AMI").is64Bit(true).build()).description("EC2 CentOS 5.4 HVM AMI")
               .defaultCredentials(new Credentials("root", null)).id("us-east-1/ami-7ea24a17").providerId(
                        "ami-7ea24a17").location(defaultLocation).userMetadata(
                        ImmutableMap.of("owner", "206029621532", "rootDeviceType", "ebs")).build());

   }

   public void testParseRightScaleImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/rightscale_images.xml");

      assertEquals(Iterables.get(result, 0), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.CENTOS).arch("paravirtual").version("5.4").description(
                        "rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml").is64Bit(true).build()).description(
               "rightscale-us-east/CentOS_5.4_x64_v4.4.10.manifest.xml").defaultCredentials(
               new Credentials("root", null)).id("us-east-1/ami-ccb35ea5").providerId("ami-ccb35ea5").location(
               defaultLocation).version("4.4.10").userMetadata(
               ImmutableMap.of("owner", "admin", "rootDeviceType", "instance-store")).build());

      assertEquals(
               new Gson().toJson(Iterables.get(result, 1)),
               "{\"operatingSystem\":{\"family\":\"UBUNTU\",\"arch\":\"paravirtual\",\"version\":\"9.10\",\"description\":\"411009282317/RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha\",\"is64Bit\":true},\"version\":\"4.5.3_EBS_Alpha\",\"description\":\"RightImage_Ubuntu_9.10_x64_v4.5.3_EBS_Alpha\",\"defaultCredentials\":{\"identity\":\"root\"},\"id\":\"us-east-1/ami-c19db6b5\",\"type\":\"IMAGE\",\"providerId\":\"ami-c19db6b5\",\"location\":{\"scope\":\"REGION\",\"id\":\"us-east-1\",\"description\":\"us-east-1\",\"iso3166Codes\":[],\"metadata\":{}},\"userMetadata\":{\"owner\":\"411009282317\",\"rootDeviceType\":\"ebs\"}}");

      assertEquals(
               new Gson().toJson(Iterables.get(result, 2)),
               "{\"operatingSystem\":{\"family\":\"WINDOWS\",\"arch\":\"hvm\",\"version\":\"2003\",\"description\":\"411009282317/RightImage Windows_2003_i386_v5.4.3\",\"is64Bit\":false},\"version\":\"5.4.3\",\"description\":\"Built by RightScale\",\"defaultCredentials\":{\"identity\":\"root\"},\"id\":\"us-east-1/ami-710c2605\",\"type\":\"IMAGE\",\"providerId\":\"ami-710c2605\",\"location\":{\"scope\":\"REGION\",\"id\":\"us-east-1\",\"description\":\"us-east-1\",\"iso3166Codes\":[],\"metadata\":{}},\"userMetadata\":{\"owner\":\"411009282317\",\"rootDeviceType\":\"ebs\"}}");
   }

   public void testParseAmznImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/amzn_images.xml");

      assertEquals(Iterables.get(result, 0), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.AMZN_LINUX).arch("paravirtual").version("0.9.7-beta")
                        .description("137112412989/amzn-ami-0.9.7-beta.i386-ebs").is64Bit(false).build()).description(
               "Amazon").defaultCredentials(new Credentials("ec2-user", null)).id("us-east-1/ami-82e4b5c7").providerId(
               "ami-82e4b5c7").location(defaultLocation).version("0.9.7-beta").userMetadata(
               ImmutableMap.of("owner", "137112412989", "rootDeviceType", "ebs")).build());

      assertEquals(Iterables.get(result, 3), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.AMZN_LINUX).arch("paravirtual").version("0.9.7-beta")
                        .description("amzn-ami-us-west-1/amzn-ami-0.9.7-beta.x86_64.manifest.xml").is64Bit(true)
                        .build()).description("Amazon Linux AMI x86_64 S3").defaultCredentials(
               new Credentials("ec2-user", null)).id("us-east-1/ami-f2e4b5b7").providerId("ami-f2e4b5b7").location(
               defaultLocation).version("0.9.7-beta").userMetadata(
               ImmutableMap.of("owner", "137112412989", "rootDeviceType", "ebs")).build());
   }

   static Location defaultLocation = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1").description(
            "us-east-1").build();

   public static Set<org.jclouds.compute.domain.Image> convertImages(String resource) {

      Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
      }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
               .getInstance(Json.class));

      Set<Image> result = DescribeImagesResponseHandlerTest.parseImages(resource);
      EC2ImageParser parser = new EC2ImageParser(new EC2PopulateDefaultLoginCredentialsForImageStrategy(), map,
               Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
                        .ofInstance(defaultLocation), new AWSEC2ReviseParsedImage(map));
      return Sets.newLinkedHashSet(Iterables.filter(Iterables.transform(result, parser), Predicates.notNull()));
   }
}
