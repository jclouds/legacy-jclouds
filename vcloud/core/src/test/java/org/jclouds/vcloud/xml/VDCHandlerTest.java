/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rest.domain.NamedLink;
import org.jclouds.rest.internal.NamedResourceImpl;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.Quota;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code VDCHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VDCHandlerTest")
public class VDCHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/vdc.xml");
      Injector injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @VCloudApi
         URI provide() {
            return URI.create("https://services.vcloudexpress.terremark.com/api/v0.8");
         }

      });
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "Miami Environment 1");
      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"));
      assertEquals(result.getDescription(), null);
      assertEquals(result.getStorageCapacity(), null);
      assertEquals(result.getCpuCapacity(), null);
      assertEquals(result.getMemoryCapacity(), null);
      assertEquals(result.getInstantiatedVmsQuota(), null);
      assertEquals(result.getDeployedVmsQuota(), null);
      assertEquals(result.getResourceEntities(), ImmutableMap.<String, NamedLink> of());
      assertEquals(
               result.getAvailableNetworks(),
               ImmutableMap
                        .of(
                                 "10.114.34.128/26",
                                 new NamedResourceImpl(
                                          "1708",
                                          "10.114.34.128/26",
                                          "application/vnd.vmware.vcloud.network+xml",
                                          URI
                                                   .create("https://services.vcloudexpress.terremark.com/api/v0.8/network/1708"))));
   }

   public void testApplyHosting() {
      InputStream is = getClass().getResourceAsStream("/vdc-hosting.xml");
      Injector injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @VCloudApi
         URI provide() {
            return URI.create("https://vcloud.safesecureweb.com/api/v0.8");
         }

      });
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "vDC Name");
      assertEquals(result.getLocation(), URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/188849"));
      assertEquals(result.getDescription(), "vDC Name");
      assertEquals(result.getStorageCapacity(), new Capacity("bytes * 10^9", 0, 40960));
      assertEquals(result.getCpuCapacity(), new Capacity("hz * 10^6", 0, 2400));
      assertEquals(result.getMemoryCapacity(), new Capacity("bytes * 10^9", 0, 2));
      assertEquals(result.getInstantiatedVmsQuota(), new Quota(0, 2));
      assertEquals(result.getDeployedVmsQuota(), new Quota(0, 2));
      assertEquals(
               result.getResourceEntities(),
               new ImmutableMap.Builder<String, NamedLink>()
                        .put(
                                 "Plesk (Linux) 64-bit Template",
                                 new NamedResourceImpl(
                                          "1",
                                          "Plesk (Linux) 64-bit Template",
                                          "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                          URI
                                                   .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/1")))
                        .put(

                                 "Windows 2008 Datacenter 64 Bit Template",
                                 new NamedResourceImpl(
                                          "2",
                                          "Windows 2008 Datacenter 64 Bit Template",
                                          "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                          URI
                                                   .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/2")))
                        .put(
                                 "Cent OS 64 Bit Template",
                                 new NamedResourceImpl(
                                          "3",
                                          "Cent OS 64 Bit Template",
                                          "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                          URI
                                                   .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/3")))
                        .put(
                                 "cPanel (Linux) 64 Bit Template",
                                 new NamedResourceImpl(
                                          "4",
                                          "cPanel (Linux) 64 Bit Template",
                                          "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                          URI
                                                   .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/4")))
                        .put(
                                 "188849-1",
                                 new NamedResourceImpl(
                                          "188849-1",
                                          "188849-1",
                                          "application/vnd.vmware.vcloud.vApp+xml",
                                          URI
                                                   .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-1")))
                        .put(
                                 "188849-2",
                                 new NamedResourceImpl(
                                          "188849-2",
                                          "188849-2",
                                          "application/vnd.vmware.vcloud.vApp+xml",
                                          URI
                                                   .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-2")))
                        .build());

      assertEquals(result.getAvailableNetworks(), ImmutableMap.<String, NamedLink> of());
   }
}
