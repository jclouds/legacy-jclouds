/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.network.DhcpService;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.IpRange;
import org.jclouds.vcloud.domain.network.IpScope;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code OrgNetworkHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class OrgNetworkHandlerTest {
   public void testIsolated() {
      InputStream is = getClass().getResourceAsStream("/orgnetwork-isolated.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      OrgNetwork result = factory.create(injector.getInstance(OrgNetworkHandler.class)).parse(is);
      assertEquals(result.getName(), "isolation01");
      assertEquals(result.getHref(), URI.create("https://vcenterprise.bluelock.com/api/v1.0/network/990419644"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.network+xml");
      assertEquals(result.getOrg(), new ReferenceTypeImpl(null, VCloudMediaType.ORG_XML, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/org/9566014")));
      assertEquals(result.getDescription(), null);
      assertEquals(result.getTasks(), ImmutableList.of());
      assert result.getConfiguration() != null;

      assertEquals(result.getConfiguration().getIpScope(), new IpScope(false, "192.168.15.1", "255.255.255.0",
               "23.172.173.113", null, null,
               ImmutableSet.<IpRange> of(new IpRange("192.168.15.100", "192.168.15.199")), ImmutableSet.<String> of()));
      assertEquals(result.getConfiguration().getParentNetwork(), null);
      assertEquals(result.getConfiguration().getFenceMode(), FenceMode.ISOLATED);
      assert result.getConfiguration().getFeatures() != null;

      assertEquals(result.getConfiguration().getFeatures().getDhcpService(), new DhcpService(false, 3600, 7200,
               new IpRange("192.168.15.2", "192.168.15.99")));
      assertEquals(result.getConfiguration().getFeatures().getFirewallService(), null);
      assertEquals(result.getConfiguration().getFeatures().getNatService(), null);

      assertEquals(result.getNetworkPool(), null);
      assertEquals(result.getAllowedExternalIpAddresses(), ImmutableSet.<String> of());

   }

   public void testBridged() {
      InputStream is = getClass().getResourceAsStream("/orgnetwork-bridged.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      OrgNetwork result = factory.create(injector.getInstance(OrgNetworkHandler.class)).parse(is);
      assertEquals(result.getName(), "internet01");
      assertEquals(result.getHref(), URI.create("https://vcenterprise.bluelock.com/api/v1.0/network/758634723"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.network+xml");
      assertEquals(result.getOrg(), new ReferenceTypeImpl(null, VCloudMediaType.ORG_XML, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/org/9566014")));
      assertEquals(result.getDescription(), null);
      assertEquals(result.getTasks(), ImmutableList.of());
      assert result.getConfiguration() != null;

      assertEquals(result.getConfiguration().getIpScope(), new IpScope(true, "174.47.101.161", "255.255.255.224",
               "24.172.173.113", null, null,
               ImmutableSet.<IpRange> of(new IpRange("174.47.101.164", "174.47.101.190")), ImmutableSet.<String> of()));
      assertEquals(result.getConfiguration().getParentNetwork(), null);
      assertEquals(result.getConfiguration().getFenceMode(), FenceMode.BRIDGED);
      assert result.getConfiguration().getFeatures() == null;
      assertEquals(result.getNetworkPool(), null);
      assertEquals(result.getAllowedExternalIpAddresses(), ImmutableSet.<String> of());

   }

}
