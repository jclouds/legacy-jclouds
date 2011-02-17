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

package org.jclouds.ec2.compute.functions;

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
import org.jclouds.ec2.compute.strategy.EC2PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
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
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class EC2ImageParserTest {

   public void testParseAmznImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/amzn_images.xml");

      assertEquals(Iterables.get(result, 0), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UNRECOGNIZED).arch("paravirtual").version("").description(
                        "137112412989/amzn-ami-0.9.7-beta.i386-ebs").is64Bit(false).build()).description("Amazon")
               .defaultCredentials(new Credentials("ec2-user", null)).id("us-east-1/ami-82e4b5c7").providerId(
                        "ami-82e4b5c7").location(defaultLocation).userMetadata(
                        ImmutableMap.of("owner", "137112412989", "rootDeviceType", "ebs")).build());

      assertEquals(Iterables.get(result, 3), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.UNRECOGNIZED).arch("paravirtual").version("").description(
                        "amzn-ami-us-west-1/amzn-ami-0.9.7-beta.x86_64.manifest.xml").is64Bit(true).build())
               .description("Amazon Linux AMI x86_64 S3").defaultCredentials(new Credentials("ec2-user", null)).id(
                        "us-east-1/ami-f2e4b5b7").providerId("ami-f2e4b5b7").location(defaultLocation).userMetadata(
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
                        .ofInstance(defaultLocation), new ReviseParsedImage.NoopReviseParsedImage());
      return Sets.newLinkedHashSet(Iterables.filter(Iterables.transform(result, parser), Predicates.notNull()));
   }

}
