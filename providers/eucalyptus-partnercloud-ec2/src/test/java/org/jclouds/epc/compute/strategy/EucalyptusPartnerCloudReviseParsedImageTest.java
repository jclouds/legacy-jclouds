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

package org.jclouds.epc.compute.strategy;

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
import org.jclouds.epc.strategy.EucalyptusPartnerCloudReviseParsedImage;
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
public class EucalyptusPartnerCloudReviseParsedImageTest {

   public void testParseEucalyptusImage() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/eucalyptus_images.xml");
      assertEquals(result.size(), 3);

      assertEquals(Iterables.get(result, 0), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.CENTOS).arch("paravirtual").version("5.3").description(
                        "centos-5.3-x86_64-xen/centos.5-3.x86-64.img.manifest.xml").is64Bit(true).build()).description(
               "centos-5.3-x86_64-xen/centos.5-3.x86-64.img.manifest.xml").defaultCredentials(
               new Credentials("root", null)).id("us-east-1/emi-F96014E1").providerId("emi-F96014E1").location(
               defaultLocation).userMetadata(ImmutableMap.of("owner", "admin", "rootDeviceType", "instance-store"))
               .build());

      assertEquals(Iterables.get(result, 1), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.WINDOWS).arch("paravirtual").version("2008").description(
                        "windows-2008-server/windows.my2008server.img.manifest.xml").is64Bit(true).build())
               .description("windows-2008-server/windows.my2008server.img.manifest.xml").defaultCredentials(
                        new Credentials("root", null)).id("us-east-1/emi-767B178C").providerId("emi-767B178C")
               .location(defaultLocation).userMetadata(
                        ImmutableMap.of("owner", "admin", "rootDeviceType", "instance-store")).build());

      assertEquals(Iterables.get(result, 2), new ImageBuilder().operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.CENTOS).arch("paravirtual").version("5.3").description(
                        "centos-5.3-x86_64-kvm/centos.5-3.x86-64.img.manifest.xml").is64Bit(true).build()).description(
               "centos-5.3-x86_64-kvm/centos.5-3.x86-64.img.manifest.xml").defaultCredentials(
               new Credentials("root", null)).id("us-east-1/emi-F9ED14E7").providerId("emi-F9ED14E7").location(
               defaultLocation).userMetadata(ImmutableMap.of("owner", "admin", "rootDeviceType", "instance-store"))
               .build());
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
                        .ofInstance(defaultLocation), new EucalyptusPartnerCloudReviseParsedImage(map));
      return Sets.newLinkedHashSet(Iterables.filter(Iterables.transform(result, parser), Predicates.notNull()));
   }

}
