/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType.CATALOG_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

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

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/vdc.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "Miami Environment 1");
      assertEquals(result.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"));
      assertEquals(result.getResourceEntities(), ImmutableMap.<String, ReferenceType> of(
            "adriantest",
            new ReferenceTypeImpl("adriantest", TerremarkVCloudMediaType.VAPP_XML, URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15124")),
            "centos-53",
            new ReferenceTypeImpl("centos-53", TerremarkVCloudMediaType.VAPP_XML, URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8/vapp/15120"))));

      assertEquals(result.getAvailableNetworks(), ImmutableMap.of(
            "10.114.34.128/26",
            new ReferenceTypeImpl("10.114.34.128/26", "application/vnd.vmware.vcloud.network+xml", URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8/network/1708"))));
      assertEquals(
            result.getCatalog(),
            new ReferenceTypeImpl("Miami Environment 1", CATALOG_XML, URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog")));
      assertEquals(
            result.getPublicIps(),
            new ReferenceTypeImpl("Public IPs", "application/xml", URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/publicIps")));
      assertEquals(
            result.getInternetServices(),
            new ReferenceTypeImpl("Internet Services", "application/xml", URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/internetServices")));
   }
}
