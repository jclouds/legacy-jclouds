/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudservers.compute.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.cloudservers.compute.config.CloudServersComputeServiceContextModule;
import org.jclouds.cloudservers.functions.ParseImageFromJsonResponseTest;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CloudServersImageToImageTest")
public class CloudServersImageToImageTest {

   @Test
   public void test() {
      Image toTest = convertImage();
      assertEquals(toTest, new ImageBuilder().name("CentOS 5.2").operatingSystem(
               new OperatingSystem.Builder().family(OsFamily.CENTOS).version("5.2").description("CentOS 5.2").is64Bit(
                        true).build()).description("CentOS 5.2").ids("2").status(Image.Status.PENDING).version(
               "1286712000000").build());
      assertEquals(toTest.getStatus(), Image.Status.PENDING);
   }

   public static Image convertImage() {
      org.jclouds.cloudservers.domain.Image image = ParseImageFromJsonResponseTest.parseImage();

      CloudServersImageToImage parser = new CloudServersImageToImage(
               CloudServersComputeServiceContextModule.toPortableImageStatus, new CloudServersImageToOperatingSystem(
                        new BaseComputeServiceContextModule() {
                        }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(
                                 new GsonModule()).getInstance(Json.class))));

      return parser.apply(image);
   }
}
