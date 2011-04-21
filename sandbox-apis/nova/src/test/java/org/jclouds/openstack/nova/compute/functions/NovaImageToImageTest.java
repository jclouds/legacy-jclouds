/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.openstack.nova.compute.functions;

import com.google.inject.Guice;
import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.functions.ParseImageFromJsonResponseTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static org.testng.Assert.assertEquals;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class NovaImageToImageTest {

   @Test
   public void testApplyWhereImageNotFound() throws UnknownHostException, URISyntaxException {
      Image image = new ImageBuilder()
            .name("CentOS 5.2")
            .operatingSystem(
                  new OperatingSystem.Builder().family(OsFamily.CENTOS).version("5.2").description("CentOS 5.2").is64Bit(true)
                        .build()).description("CentOS 5.2").defaultCredentials(new Credentials("root", null))
            .ids("1").version("1286712000000").uri(new URI("https://servers.api.rackspacecloud.com/v1.1/1234/images/1")).build();
      Image parsedImage = convertImage();

      assertEquals(parsedImage, image);
   }

   public static Image convertImage() {
      org.jclouds.openstack.nova.domain.Image image = ParseImageFromJsonResponseTest.parseImage();

      NovaImageToImage parser = new NovaImageToImage(new NovaImageToOperatingSystem(new BaseComputeServiceContextModule() {
      }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
            .getInstance(Json.class))));

      return parser.apply(image);
   }
}
