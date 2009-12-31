/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.vcloud.domain.FenceMode;
import org.jclouds.vcloud.domain.FirewallRule;
import org.jclouds.vcloud.domain.NatRule;
import org.jclouds.vcloud.domain.Network;
import org.jclouds.vcloud.domain.internal.NetworkImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code NetworkHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.NetworkHandlerTest")
public class NetworkHandlerTest {

   private Injector injector;

   private Factory factory;

   public void testTerremark() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/network-terremark.xml");
      injector = Guice.createInjector(new ParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      Network result = factory.create(injector.getInstance(NetworkHandler.class)).parse(is);
      assertEquals(result, new NetworkImpl("1708", "10.114.34.128/26", URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/network/1708"), null,
               ImmutableSet.<InetAddress> of(), InetAddress.getByName("10.114.34.129"), InetAddress
                        .getByName("255.255.255.192"), ImmutableSet
                        .<FenceMode> of(FenceMode.ISOLATED), null, ImmutableSet.<NatRule> of(),
               ImmutableSet.<FirewallRule> of()));
   }

   public void testHosting() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/network-hosting.xml");
      injector = Guice.createInjector(new ParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      Network result = (Network) factory.create(injector.getInstance(NetworkHandler.class)).parse(
               is);
      assertEquals(result, new NetworkImpl("1183", "188849 trust", URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/network/1183"), "188849 trust",
               ImmutableSet.<InetAddress> of(InetAddress.getByName("76.12.32.110"), InetAddress
                        .getByName("208.112.89.187")), InetAddress.getByName("204.12.53.89"),
               InetAddress.getByName("255.255.255.248"), ImmutableSet.<FenceMode> of(), null,
               ImmutableSet.<NatRule> of(), ImmutableSet.<FirewallRule> of()));
   }

}
