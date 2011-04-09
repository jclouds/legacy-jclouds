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
package org.jclouds.ibmdev.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.ibmdev.config.IBMDeveloperCloudParserModule;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.domain.InstanceType;
import org.jclouds.ibmdev.domain.Price;
import org.jclouds.ibmdev.domain.Image.Visibility;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
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
      Injector injector = Guice.createInjector(new IBMDeveloperCloudParserModule(), new GsonModule());
      handler = injector.getInstance(ParseImageFromJson.class);
   }

   public void test() {

      Image image = new Image(
            "SUSE Linux Enterprise Server 11 for x86",
            HttpUtils
                  .createUri("https://www-147.ibm.com/cloud/enterprise/ram.ws/RAMSecure/artifact/{F006D027-02CC-9D08-D389-6C729D939D44}/1.0/parameters.xml"),
            1,
            Visibility.PUBLIC,
            "SYSTEM",
            "SUSE Linux Enterprise Server/11",
            new Date(1216944000000l),
            "41",
            ImmutableSet.<InstanceType> of(new InstanceType("Bronze 32 bit", new Price(0.17, "UHR  ", "897", null,
                  "USD", 1), "BRZ32.1/2048/175"), new InstanceType("Gold 32 bit", new Price(0.41, "UHR  ", "897", null,
                  "USD", 1), "GLD32.4/4096/350"), new InstanceType("Silver 32 bit", new Price(0.265, "UHR  ", "897",
                  null, "USD", 1), "SLV32.2/4096/350")),
            ImmutableSet.<String> of("ifeE7VOzRG6SGvoDlRPTQw"),
            HttpUtils
                  .createUri("https://www-147.ibm.com/cloud/enterprise/ram.ws/RAMSecure/artifact/{F006D027-02CC-9D08-D389-6C729D939D44}/1.0/GettingStarted.html"),
            "20001150", "SUSE Linux Enterprise Server 11 for x86 Base OS 32-bit with pay for use licensing");

      Image compare = handler.apply(new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(ParseImageFromJsonTest.class.getResourceAsStream("/image.json"))));
      assertEquals(compare, image);
   }
}
