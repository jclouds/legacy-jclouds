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
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.VCloudExpressNetwork;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;
import org.jclouds.vcloud.domain.network.nat.rules.PortForwardingRule;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVCloudExpressNetwork;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code TerremarkVCloudExpressNetworkHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TerremarkVCloudExpressNetworkHandlerTest {

   private Injector injector;

   private Factory factory;

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/network-terremark.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressNetwork result = factory.create(injector.getInstance(TerremarkVCloudExpressNetworkHandler.class))
               .parse(is);
      assertEquals(
               result,
               new TerremarkVCloudExpressNetwork(
                        "10.122.209.128/28",
                        "application/vnd.vmware.vcloud.network+xml",
                        URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/network/40031"),
                        null,
                        ImmutableSet.<String> of(),
                        "10.122.209.129",
                        "255.255.255.240",
                        ImmutableSet.<FenceMode> of(FenceMode.ISOLATED),
                        null,
                        ImmutableSet.<PortForwardingRule> of(),
                        ImmutableSet.<FirewallRule> of(),

                        new ReferenceTypeImpl(
                                 "10.122.209.128/28",
                                 "application/xml",
                                 URI
                                          .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/extensions/network/40031")),
                        new ReferenceTypeImpl(
                                 "IP Addresses",
                                 "application/xml",
                                 URI
                                          .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/extensions/network/40031/ips"))));

   }
}
