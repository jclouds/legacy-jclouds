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

import static org.jclouds.vcloud.VCloudMediaType.CATALOGITEM_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.CatalogImpl;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code CatalogHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CatalogHandlerTest {

   private Injector injector;

   private Factory factory;

   public void testVCloud1_0() {
      InputStream is = getClass().getResourceAsStream("/catalog-blank.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      Catalog result = (Catalog) factory.create(injector.getInstance(CatalogHandler.class)).parse(is);
      assertEquals(result, new CatalogImpl("Jclouds-private", "application/vnd.vmware.vcloud.catalog+xml", URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/catalog/921222081"), new ReferenceTypeImpl(null,
               "application/vnd.vmware.vcloud.org+xml", URI
                        .create("https://vcenterprise.bluelock.com/api/v1.0/org/9566014")), null, ImmutableMap
               .<String, ReferenceType> of(), ImmutableList.<Task> of(), false, false));
   }

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/catalog.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      Catalog result = (Catalog) factory.create(injector.getInstance(CatalogHandler.class)).parse(is);
      assertEquals(result.getName(), "Miami Environment 1");
      assert result.getDescription() == null;

      assertEquals(result.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog"));
      assertEquals(result.get("CentOS 5.3 (32-bit)"), new ReferenceTypeImpl("CentOS 5.3 (32-bit)", CATALOGITEM_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/5")));
      assertEquals(result.get("CentOS 5.3 (64-bit)"), new ReferenceTypeImpl("CentOS 5.3 (64-bit)", CATALOGITEM_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/6")));
      assertEquals(result.get("RHEL 5.3 (32-bit)"), new ReferenceTypeImpl("RHEL 5.3 (32-bit)", CATALOGITEM_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/7")));
      assertEquals(result.get("RHEL 5.3 (64-bit)"), new ReferenceTypeImpl("RHEL 5.3 (64-bit)", CATALOGITEM_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/8")));
      assertEquals(result.get("Ubuntu JeOS 9.04 (32-bit)"), new ReferenceTypeImpl("Ubuntu JeOS 9.04 (32-bit)",
               CATALOGITEM_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/11")));
      assertEquals(result.get("Ubuntu JeOS 9.04 (64-bit)"), new ReferenceTypeImpl("Ubuntu JeOS 9.04 (64-bit)",
               CATALOGITEM_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/12")));
      assertEquals(result.get("Ubuntu Server 9.04 (32-bit)"), new ReferenceTypeImpl("Ubuntu Server 9.04 (32-bit)",
               CATALOGITEM_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/9")));
      assertEquals(result.get("Ubuntu Server 9.04 (64-bit)"), new ReferenceTypeImpl("Ubuntu Server 9.04 (64-bit)",
               CATALOGITEM_XML, URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/10")));
      assertEquals(result.get("Windows 2003 Enterprise R2 (32-bit)"), new ReferenceTypeImpl(
               "Windows 2003 Enterprise R2 (32-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/1")));
      assertEquals(result.get("Windows 2003 Enterprise R2 (64-bit)"), new ReferenceTypeImpl(
               "Windows 2003 Enterprise R2 (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/2")));
      assertEquals(result.get("Windows 2003 Standard R2 (32-bit)"), new ReferenceTypeImpl(
               "Windows 2003 Standard R2 (32-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/3")));
      assertEquals(result.get("Windows 2003 Standard R2 (64-bit)"), new ReferenceTypeImpl(
               "Windows 2003 Standard R2 (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/4")));
      assertEquals(result.get("Windows 2003 Standard R2 w.SQL 2008 Web (64-bit)"), new ReferenceTypeImpl(
               "Windows 2003 Standard R2 w.SQL 2008 Web (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/23")));
      assertEquals(result.get("Windows Server 2008 Enterprise (32-bit)"), new ReferenceTypeImpl(
               "Windows Server 2008 Enterprise (32-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/13")));
      assertEquals(result.get("Windows Server 2008 Enterprise (64-bit)"), new ReferenceTypeImpl(
               "Windows Server 2008 Enterprise (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/15")));
      assertEquals(result.get("Windows Server 2008 Enterprise R2 (64-bit)"), new ReferenceTypeImpl(
               "Windows Server 2008 Enterprise R2 (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/16")));
      assertEquals(result.get("Windows Server 2008 Standard (32-bit)"), new ReferenceTypeImpl(
               "Windows Server 2008 Standard (32-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/17")));
      assertEquals(result.get("Windows Server 2008 Standard (64-bit)"), new ReferenceTypeImpl(
               "Windows Server 2008 Standard (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/18")));
      assertEquals(result.get("Windows Server 2008 Standard R2 (64-bit)"), new ReferenceTypeImpl(
               "Windows Server 2008 Standard R2 (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/19")));
      assertEquals(result.get("Windows Server 2008 Standard w.SQL 2008 Web (64-bit)"), new ReferenceTypeImpl(
               "Windows Server 2008 Standard w.SQL 2008 Web (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/14")));
      assertEquals(result.get("Windows Web Server 2008 (32-bit)"), new ReferenceTypeImpl(
               "Windows Web Server 2008 (32-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/20")));
      assertEquals(result.get("Windows Web Server 2008 (64-bit)"), new ReferenceTypeImpl(
               "Windows Web Server 2008 (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/21")));
      assertEquals(result.get("Windows Web Server 2008 R2 (64-bit)"), new ReferenceTypeImpl(
               "Windows Web Server 2008 R2 (64-bit)", CATALOGITEM_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/22")));
   }
}
