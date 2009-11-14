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

import static org.jclouds.vcloud.VCloudMediaType.CATALOGITEM_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.endpoints.internal.CatalogItemRoot;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code CatalogHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.CatalogHandlerTest")
public class CatalogHandlerTest {

   private Injector injector;

   private Factory factory;

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/catalog.xml");
      injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @CatalogItemRoot
         String provide() {
            return "https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem";
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
      Catalog result = (Catalog) factory.create(injector.getInstance(CatalogHandler.class)).parse(
               is);
      assertEquals(result.getName(), "Miami Environment 1");
      assert result.getDescription() == null;

      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog"));

      assertEquals(result.get("CentOS 5.3 (32-bit)"), new NamedResourceImpl(5,
               "CentOS 5.3 (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/5")));
      assertEquals(result.get("CentOS 5.3 (64-bit)"), new NamedResourceImpl(6,
               "CentOS 5.3 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/6")));
      assertEquals(result.get("RHEL 5.3 (32-bit)"), new NamedResourceImpl(7, "RHEL 5.3 (32-bit)",
               CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/7")));
      assertEquals(result.get("RHEL 5.3 (64-bit)"), new NamedResourceImpl(8, "RHEL 5.3 (64-bit)",
               CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/8")));
      assertEquals(result.get("Ubuntu JeOS 9.04 (32-bit)"), new NamedResourceImpl(11,
               "Ubuntu JeOS 9.04 (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/11")));
      assertEquals(result.get("Ubuntu JeOS 9.04 (64-bit)"), new NamedResourceImpl(12,
               "Ubuntu JeOS 9.04 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/12")));
      assertEquals(result.get("Ubuntu Server 9.04 (32-bit)"), new NamedResourceImpl(9,
               "Ubuntu Server 9.04 (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/9")));
      assertEquals(result.get("Ubuntu Server 9.04 (64-bit)"), new NamedResourceImpl(10,
               "Ubuntu Server 9.04 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/10")));
      assertEquals(result.get("Windows 2003 Enterprise R2 (32-bit)"), new NamedResourceImpl(1,
               "Windows 2003 Enterprise R2 (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/1")));
      assertEquals(result.get("Windows 2003 Enterprise R2 (64-bit)"), new NamedResourceImpl(2,
               "Windows 2003 Enterprise R2 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/2")));
      assertEquals(result.get("Windows 2003 Standard R2 (32-bit)"), new NamedResourceImpl(3,
               "Windows 2003 Standard R2 (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/3")));
      assertEquals(result.get("Windows 2003 Standard R2 (64-bit)"), new NamedResourceImpl(4,
               "Windows 2003 Standard R2 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/4")));
      assertEquals(
               result.get("Windows 2003 Standard R2 w.SQL 2008 Web (64-bit)"),
               new NamedResourceImpl(
                        23,
                        "Windows 2003 Standard R2 w.SQL 2008 Web (64-bit)",
                        CATALOGITEM_XML,
                        URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/23")));
      assertEquals(result.get("Windows Server 2008 Enterprise (32-bit)"), new NamedResourceImpl(13,
               "Windows Server 2008 Enterprise (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/13")));
      assertEquals(result.get("Windows Server 2008 Enterprise (64-bit)"), new NamedResourceImpl(15,
               "Windows Server 2008 Enterprise (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/15")));
      assertEquals(result.get("Windows Server 2008 Enterprise R2 (64-bit)"), new NamedResourceImpl(
               16, "Windows Server 2008 Enterprise R2 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/16")));
      assertEquals(result.get("Windows Server 2008 Standard (32-bit)"), new NamedResourceImpl(17,
               "Windows Server 2008 Standard (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/17")));
      assertEquals(result.get("Windows Server 2008 Standard (64-bit)"), new NamedResourceImpl(18,
               "Windows Server 2008 Standard (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/18")));
      assertEquals(result.get("Windows Server 2008 Standard R2 (64-bit)"), new NamedResourceImpl(
               19, "Windows Server 2008 Standard R2 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/19")));
      assertEquals(
               result.get("Windows Server 2008 Standard w.SQL 2008 Web (64-bit)"),
               new NamedResourceImpl(
                        14,
                        "Windows Server 2008 Standard w.SQL 2008 Web (64-bit)",
                        CATALOGITEM_XML,
                        URI
                                 .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/14")));
      assertEquals(result.get("Windows Web Server 2008 (32-bit)"), new NamedResourceImpl(20,
               "Windows Web Server 2008 (32-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/20")));
      assertEquals(result.get("Windows Web Server 2008 (64-bit)"), new NamedResourceImpl(21,
               "Windows Web Server 2008 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/21")));
      assertEquals(result.get("Windows Web Server 2008 R2 (64-bit)"), new NamedResourceImpl(22,
               "Windows Web Server 2008 R2 (64-bit)", CATALOGITEM_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/22")));

   }

   public void testHosting() {
      InputStream is = getClass().getResourceAsStream("/catalog-hosting.xml");
      injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings("unused")
         @Provides
         @CatalogItemRoot
         String provide() {
            return "https://vcloud.safesecureweb.com/api/v0.8/catalogItem";
         }

      });
      factory = injector.getInstance(ParseSax.Factory.class);
      Catalog result = (Catalog) factory.create(injector.getInstance(CatalogHandler.class)).parse(
               is);
      assertEquals(result.getName(), "HMSCatalog");
      assertEquals(result.getDescription(), "HMS Shared Catalog");
      assertEquals(result.getLocation(), URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1"));

      assertEquals(result.get("Plesk (Linux) 64-bit Template"), new NamedResourceImpl(1,
               "Plesk (Linux) 64-bit Template", CATALOGITEM_XML, URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/1")));

      assertEquals(result.get("Windows 2008 Datacenter 64 Bit Template"), new NamedResourceImpl(2,
               "Windows 2008 Datacenter 64 Bit Template", CATALOGITEM_XML, URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2")));

      assertEquals(result.get("Cent OS 64 Bit Template"), new NamedResourceImpl(3,
               "Cent OS 64 Bit Template", CATALOGITEM_XML, URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/3")));

      assertEquals(result.get("cPanel (Linux) 64 Bit Template"), new NamedResourceImpl(4,
               "cPanel (Linux) 64 Bit Template", CATALOGITEM_XML, URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/4")));
   }
}
