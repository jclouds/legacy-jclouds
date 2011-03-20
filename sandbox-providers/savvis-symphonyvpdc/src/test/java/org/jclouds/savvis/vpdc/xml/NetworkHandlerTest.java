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

package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.savvis.vpdc.domain.Network;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code NetworkHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class NetworkHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/network.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Network result = factory.create(injector.getInstance(NetworkHandler.class)).parse(is);
      assertEquals(
            result.toString(),
            Network.builder().type(VCloudMediaType.NETWORK_XML).name("VM-Tier01").gateway("1.1.1.1")
                  .netmask("255.255.255.240").build().toString());
   }

   public void testNat() {
      InputStream is = getClass().getResourceAsStream("/network-nat.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Network result = factory.create(injector.getInstance(NetworkHandler.class)).parse(is);
      assertEquals(result.toString(),
            Network.builder().type(VCloudMediaType.NETWORK_XML).name("VM Tier01").gateway("1.1.1.1").netmask("2.2.2.2")
                  .internalToExternalNATRule("3.3.3.3", "4.4.4.4").internalToExternalNATRule("3.3.3.4", "4.4.4.5")
                  .build().toString());
   }

   public void tesWhenNoVAppsInNetworkSetsAllZerosToGatewayAndNetmask() {
      InputStream is = getClass().getResourceAsStream("/network-unused.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Network result = factory.create(injector.getInstance(NetworkHandler.class)).parse(is);
      assertEquals(result.toString(),
            Network.builder().type(VCloudMediaType.NETWORK_XML).name("VM Tier01").gateway("0.0.0.0").netmask("0.0.0.0")
                  .build().toString());
   }
}
