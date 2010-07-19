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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.ibmdev.config.IBMDeveloperCloudParserModule;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.domain.Image.Visibility;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseImageFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ibmdev.ParseImageFromJsonTest")
public class ParseImageFromJsonTest {

   private ParseImageFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule(),
            new IBMDeveloperCloudParserModule());
      handler = injector.getInstance(ParseImageFromJson.class);
   }

   public void test() {

      Image image = new Image();
      image.setName("Rational Requirements Composer");
      image
            .setManifest(HttpUtils
                  .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram.ws/RAMSecure/artifact/{28C7B870-2C0A-003F-F886-B89F5B413B77}/1.0/parameters.xml"));
      image.setState(1);
      image.setVisibility(Visibility.PUBLIC);
      image.setOwner("mutdosch@us.ibm.com");
      image.setArchitecture("i386");
      image.setPlatform("Redhat Enterprise Linux (32-bit)/5.4");
      image.setCreatedTime(new Date(1265647398000l));
      image.setLocation("1");
      image.setSupportedInstanceTypes(ImmutableSet.of("LARGE", "MEDIUM"));
      // image.setProductCodes();
      image
            .setDocumentation(HttpUtils
                  .createUri("https://www-180.ibm.com/cloud/enterprise/beta/ram.ws/RAMSecure/artifact/{28C7B870-2C0A-003F-F886-B89F5B413B77}/1.0/GettingStarted.html"));
      image.setId("10005598");
      image
            .setDescription("Rational Requirements Composer helps teams define and use requirements effectively across the project lifecycle.");

      Image compare = handler.apply(new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(ParseImageFromJsonTest.class
                  .getResourceAsStream("/image.json"))));
      assertEquals(compare, image);
   }
}
