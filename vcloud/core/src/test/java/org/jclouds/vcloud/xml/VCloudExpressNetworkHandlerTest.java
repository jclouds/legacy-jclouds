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

package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.VCloudExpressNetwork;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;
import org.jclouds.vcloud.domain.network.internal.VCloudExpressNetworkImpl;
import org.jclouds.vcloud.domain.network.nat.rules.PortForwardingRule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VCloudExpressNetworkHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VCloudExpressNetworkHandlerTest {

   private Injector injector;

   private Factory factory;

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/express/network-terremark.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressNetwork result = factory.create(injector.getInstance(VCloudExpressNetworkHandler.class)).parse(is);
      assertEquals(result, new VCloudExpressNetworkImpl("10.122.209.128/28",
               "application/vnd.vmware.vcloud.network+xml", URI
                        .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/network/40031"), null,
               ImmutableSet.<String> of(), "10.122.209.129", "255.255.255.240", ImmutableSet
                        .<FenceMode> of(FenceMode.ISOLATED), null, ImmutableSet.<PortForwardingRule> of(), ImmutableSet
                        .<FirewallRule> of()));
   }

   public void testHosting() {
      InputStream is = getClass().getResourceAsStream("/express/network-hosting.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressNetwork result = (VCloudExpressNetwork) factory.create(
               injector.getInstance(VCloudExpressNetworkHandler.class)).parse(is);
      assertEquals(result, new VCloudExpressNetworkImpl("188849 trust", null, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/network/1183"), "188849 trust", ImmutableSet
               .<String> of("76.12.32.110", "208.112.89.187"), "204.12.53.89", "255.255.255.248", ImmutableSet
               .<FenceMode> of(), null, ImmutableSet.<PortForwardingRule> of(), ImmutableSet.<FirewallRule> of()));
   }

   public void testBluelock() {
      InputStream is = getClass().getResourceAsStream("/express/network-bluelock.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressNetwork result = (VCloudExpressNetwork) factory.create(
               injector.getInstance(VCloudExpressNetworkHandler.class)).parse(is);
      assertEquals(result, new VCloudExpressNetworkImpl("Pod03_Private", null, URI
               .create("https://express3.bluelock.com/api/v0.8/network/1"), "Pod 03 Private Network", ImmutableSet
               .<String> of("24.172.173.113", "66.133.112.131"), "172.18.8.1", "255.255.248.0", ImmutableSet
               .<FenceMode> of(), null, ImmutableSet.<PortForwardingRule> of(), ImmutableSet.<FirewallRule> of()));
   }

}
