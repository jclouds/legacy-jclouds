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
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VDCHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VDCHandlerTest {
   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/vdc-1.0.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "Jclouds-Commit-compG1xstorA01");
      assertEquals(result.getHref(), URI.create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1014839439"));
      assertEquals(result.getType(), "application/vnd.vmware.vcloud.vdc+xml");
      assertEquals(result.getStatus(), VDCStatus.READY);
      assertEquals(result.getOrg(), new ReferenceTypeImpl(null, VCloudMediaType.ORG_XML, URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/org/9566014")));
      assertEquals(result.getDescription(), null);
      assertEquals(result.getTasks(), ImmutableList.of());
      assertEquals(result.getAllocationModel(), AllocationModel.ALLOCATION_POOL);
      assertEquals(result.getStorageCapacity(), new Capacity("MB", 1024000, 1024000, 0, 0));
      assertEquals(result.getCpuCapacity(), new Capacity("MHz", 20000, 20000, 0, 0));
      assertEquals(result.getMemoryCapacity(), new Capacity("MB", 30720, 30720, 0, 0));
      assertEquals(result.getResourceEntities(), ImmutableMap.<String, ReferenceType> of());
      assertEquals(result.getAvailableNetworks(), ImmutableMap.of("isolation01", new ReferenceTypeImpl("isolation01",
               "application/vnd.vmware.vcloud.network+xml", URI
                        .create("https://vcenterprise.bluelock.com/api/v1.0/network/990419644")), "internet01",
               new ReferenceTypeImpl("internet01", "application/vnd.vmware.vcloud.network+xml", URI
                        .create("https://vcenterprise.bluelock.com/api/v1.0/network/758634723"))));
      assertEquals(result.getNicQuota(), 0);
      assertEquals(result.getNetworkQuota(), 100);
      assertEquals(result.getVmQuota(), 50);
      assert result.isEnabled();
   }

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/vdc.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "Miami Environment 1");
      assertEquals(result.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"));
      assertEquals(result.getDescription(), null);
      assertEquals(result.getStorageCapacity(), new Capacity("bytes * 10^9", 100, 0, 8, 0));
      assertEquals(result.getCpuCapacity(), new Capacity("hz * 10^6", 5000, 0, 0, 0));
      assertEquals(result.getMemoryCapacity(), new Capacity("bytes * 2^20", 10240, 0, 0, 0));
      assertEquals(result.getVmQuota(), 0);
      assertEquals(result.getResourceEntities(), ImmutableMap.<String, ReferenceType> of("adriantest",
               new ReferenceTypeImpl("adriantest", VCloudMediaType.VAPP_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15124")), "centos-53",
               new ReferenceTypeImpl("centos-53", VCloudMediaType.VAPP_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15120"))));
      assertEquals(result.getAvailableNetworks(), ImmutableMap.of("10.114.34.128/26", new ReferenceTypeImpl(
               "10.114.34.128/26", "application/vnd.vmware.vcloud.network+xml", URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/network/1708"))));
   }

   public void testHostingDotCom() {
      InputStream is = getClass().getResourceAsStream("/vdc-hosting.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "vDC Name");
      assertEquals(result.getHref(), URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/188849"));
      assertEquals(result.getDescription(), "vDC Name");
      assertEquals(result.getStorageCapacity(), new Capacity("bytes * 10^9", 0, 0, 40960, 0));
      assertEquals(result.getCpuCapacity(), new Capacity("hz * 10^6", 0, 0, 2400, 0));
      assertEquals(result.getMemoryCapacity(), new Capacity("bytes * 10^9", 0, 0, 2, 0));
      assertEquals(result.getVmQuota(), 0);
      assertEquals(result.getResourceEntities(), new ImmutableMap.Builder<String, ReferenceType>().put(
               "Plesk (Linux) 64-bit Template",
               new ReferenceTypeImpl("Plesk (Linux) 64-bit Template", "application/vnd.vmware.vcloud.vAppTemplate+xml",
                        URI.create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/1"))).put(

               "Windows 2008 Datacenter 64 Bit Template",
               new ReferenceTypeImpl("Windows 2008 Datacenter 64 Bit Template",
                        "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/2"))).put(
               "Cent OS 64 Bit Template",
               new ReferenceTypeImpl("Cent OS 64 Bit Template", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/3"))).put(
               "cPanel (Linux) 64 Bit Template",
               new ReferenceTypeImpl("cPanel (Linux) 64 Bit Template",
                        "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/4"))).put(
               "188849-1",
               new ReferenceTypeImpl("188849-1", "application/vnd.vmware.vcloud.vApp+xml", URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-1"))).put(
               "188849-2",
               new ReferenceTypeImpl("188849-2", "application/vnd.vmware.vcloud.vApp+xml", URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-2"))).build());

      assertEquals(result.getAvailableNetworks(), ImmutableMap.<String, ReferenceType> of());
   }
}
