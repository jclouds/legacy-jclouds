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

package org.jclouds.vcloud.domain.network.internal;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.IpRange;
import org.jclouds.vcloud.domain.network.IpScope;
import org.jclouds.vcloud.domain.network.OrgNetwork;
import org.jclouds.vcloud.domain.network.VCloudExpressNetwork;
import org.jclouds.vcloud.xml.VCloudExpressNetworkHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VCloudExpressOrgNetworkAdapter}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudExpressOrgNetworkAdapterTest")
public class VCloudExpressOrgNetworkAdapterTest {

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/express/network-terremark.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressNetwork in = factory.create(injector.getInstance(VCloudExpressNetworkHandler.class)).parse(is);
      OrgNetwork result = new VCloudExpressOrgNetworkAdapter(in);
      assertEquals(result.getName(), "10.122.209.128/28");
      assertEquals(result.getHref(), URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/network/40031"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.network+xml");
      assertEquals(result.getOrg(), null);
      assertEquals(result.getDescription(), null);
      assertEquals(result.getTasks(), ImmutableList.of());
      assert result.getConfiguration() != null;

      assertEquals(result.getConfiguration().getIpScope(), new IpScope(true, "10.122.209.129", "255.255.255.240", null,
               null, null, ImmutableSet.<IpRange> of(), ImmutableSet.<String> of()));

      assertEquals(result.getConfiguration().getParentNetwork(), null);
      assertEquals(result.getConfiguration().getFenceMode(), FenceMode.ISOLATED);
      assert result.getConfiguration().getFeatures() != null;

      assertEquals(result.getConfiguration().getFeatures().getDhcpService(), null);
      assertEquals(result.getConfiguration().getFeatures().getFirewallService(), null);
      assertEquals(result.getConfiguration().getFeatures().getNatService(), null);

      assertEquals(result.getNetworkPool(), null);
      assertEquals(result.getAllowedExternalIpAddresses(), ImmutableSet.<String> of());

   }

   public void testBluelock() {
      InputStream is = getClass().getResourceAsStream("/express/network-bluelock.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressNetwork in = factory.create(injector.getInstance(VCloudExpressNetworkHandler.class)).parse(is);
      OrgNetwork result = new VCloudExpressOrgNetworkAdapter(in);
      assertEquals(result.getName(), "Pod03_Private");
      assertEquals(result.getHref(), URI.create("https://express3.bluelock.com/api/v0.8/network/1"));
      assertEquals(result.getType(), null);
      assertEquals(result.getOrg(), null);
      assertEquals(result.getDescription(), "Pod 03 Private Network");
      assertEquals(result.getTasks(), ImmutableList.of());
      assert result.getConfiguration() != null;

      assertEquals(result.getConfiguration().getIpScope(), new IpScope(true, "172.18.8.1", "255.255.248.0",
               "66.133.112.131", "24.172.173.113", null, ImmutableSet.<IpRange> of(), ImmutableSet.<String> of()));

      assertEquals(result.getConfiguration().getParentNetwork(), null);
      assertEquals(result.getConfiguration().getFenceMode(), FenceMode.BRIDGED);
      assert result.getConfiguration().getFeatures() != null;

      assertEquals(result.getConfiguration().getFeatures().getDhcpService(), null);
      assertEquals(result.getConfiguration().getFeatures().getFirewallService(), null);
      assertEquals(result.getConfiguration().getFeatures().getNatService(), null);

      assertEquals(result.getNetworkPool(), null);
      assertEquals(result.getAllowedExternalIpAddresses(), ImmutableSet.<String> of());

   }
}
