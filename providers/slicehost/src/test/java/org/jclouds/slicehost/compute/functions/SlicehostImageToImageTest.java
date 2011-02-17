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

package org.jclouds.slicehost.compute.functions;

import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.slicehost.xml.ImageHandlerTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SlicehostImageToImageTest {
   Location provider = new LocationBuilder().scope(LocationScope.ZONE).id("dallas").description("description").build();

   @Test
   public void test() throws UnknownHostException {
      assertEquals(convertImage(), new ImageBuilder().name("CentOS 5.2").operatingSystem(
               new OperatingSystemBuilder().family(OsFamily.CENTOS).version("5.2").description("CentOS 5.2").is64Bit(
                        true).build()).description("CentOS 5.2").defaultCredentials(new Credentials("root", null)).ids(
               "2").build());
   }

   @Test
   public void test32() throws UnknownHostException {
      assertEquals(convertImage("/test_get_image32.xml"), new ImageBuilder().name("Ubuntu 10.10 (maverick) 32-bit")
               .operatingSystem(
                        new OperatingSystemBuilder().family(OsFamily.UBUNTU).version("10.10").description(
                                 "Ubuntu 10.10 (maverick) 32-bit").build()).description(
                        "Ubuntu 10.10 (maverick) 32-bit").defaultCredentials(new Credentials("root", null)).ids("70")
               .build());
   }

   public static Image convertImage() {
      return convertImage("/test_get_image.xml");
   }

   public static Image convertImage(String resource) {
      org.jclouds.slicehost.domain.Image image = ImageHandlerTest.parseImage(resource);

      SlicehostImageToImage parser = new SlicehostImageToImage(new SlicehostImageToOperatingSystem(
               new BaseComputeServiceContextModule() {
               }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice
                        .createInjector(new GsonModule()).getInstance(Json.class))));

      return parser.apply(image);
   }
}
