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

package org.jclouds.ibmdev.functions;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.ibmdev.config.IBMDeveloperCloudParserModule;
import org.jclouds.ibmdev.domain.Image;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseImagesFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ibmdev.ParseImagesFromJsonTest")
public class ParseImagesFromJsonTest {

   private ParseImagesFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule(),
               new IBMDeveloperCloudParserModule());
      handler = injector.getInstance(ParseImagesFromJson.class);
   }

   public void test() {
      Image image1 = new Image();

      image1.setName("Rational Build Forge Agent");
      image1
               .setManifest(HttpUtils
                        .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram.ws/RAMSecure/artifact/{A233F5A0-05A5-F21D-3E92-3793B722DFBD}/1.0/parameters.xml"));
      image1.setState(1);
      image1.setVisibility(Image.Visibility.PUBLIC);
      image1.setOwner("SYSTEM");
      image1.setArchitecture("i386");
      image1.setPlatform("SUSE Linux Enterprise/10 SP2");
      image1.setCreatedTime(new Date(1240632000000l));
      image1.setLocation("1");
      image1.setSupportedInstanceTypes(ImmutableSet.of("SMALL", "MEDIUM", "LARGE"));
      image1.setProductCodes(ImmutableSet.of("fd2d0478b132490897526b9b4433a334"));
      image1
               .setDocumentation(HttpUtils
                        .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram.ws/RAMSecure/artifact/{A233F5A0-05A5-F21D-3E92-3793B722DFBD}/1.0/GettingStarted.html"));
      image1.setId("2");
      image1
               .setDescription("Rational Build Forge provides an adaptive process execution framework that automates, orchestrates, manages, and tracks all the processes between each handoff within the assembly line of software development, creating an automated software factory.");

      Image image2 = new Image();
      image2.setName("Rational Requirements Composer");
      image2
               .setManifest(HttpUtils
                        .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram.ws/RAMSecure/artifact/{28C7B870-2C0A-003F-F886-B89F5B413B77}/1.0/parameters.xml"));
      image2.setState(1);
      image2.setVisibility(Image.Visibility.PUBLIC);
      image2.setOwner("mutdosch@us.ibm.com");
      image2.setArchitecture("i386");
      image2.setPlatform("Redhat Enterprise Linux (32-bit)/5.4");
      image2.setCreatedTime(new Date(1265647398869l));
      image2.setLocation("1");
      image2.setSupportedInstanceTypes(ImmutableSet.of("LARGE", "MEDIUM"));
      // image.setProductCodes();
      image2
               .setDocumentation(HttpUtils
                        .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram.ws/RAMSecure/artifact/{28C7B870-2C0A-003F-F886-B89F5B413B77}/1.0/GettingStarted.html"));
      image2.setId("10005598");
      image2
               .setDescription("Rational Requirements Composer helps teams define and use requirements effectively across the project lifecycle.");

      Set<? extends Image> compare = handler.apply(new HttpResponse(ParseImagesFromJsonTest.class
               .getResourceAsStream("/images.json")));
      assert (compare.contains(image1));
      assert (compare.contains(image2));
   }
}
