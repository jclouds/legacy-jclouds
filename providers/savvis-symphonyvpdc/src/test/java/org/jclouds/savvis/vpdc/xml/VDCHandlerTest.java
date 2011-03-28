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

package org.jclouds.savvis.vpdc.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.ResourceImpl;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VDC.Status;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VDCHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VDCHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/vdc.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "demo_vpdcname");
      assertEquals(result.getDescription(),
               "ServiceProfileName = Balanced; ServiceLocation = North America; Email = jim@company.com;");
      assertEquals(result.getStatus(), Status.DEPLOYED);
      assertEquals(
               result.getResourceEntities(),
               ImmutableSet
                        .<Resource> of(
                                 new ResourceImpl(
                                          "1001",
                                          "DemoHost-1",
                                          VCloudMediaType.VAPP_XML,
                                          URI
                                                   .create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1001")),
                                 new ResourceImpl(
                                          "1002",
                                          "DemoHost-2",
                                          VCloudMediaType.VAPP_XML,
                                          URI
                                                   .create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1002")),
                                 new ResourceImpl(
                                          "1003",
                                          "DemoHost-3",
                                          VCloudMediaType.VAPP_XML,
                                          URI
                                                   .create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1003")),
                                 new ResourceImpl(
                                          "1234",
                                          "CustomerTemplateName",
                                          VCloudMediaType.VAPPTEMPLATE_XML,
                                          URI
                                                   .create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vAppTemplate/1234")),
                                 new ResourceImpl(
                                          "FirewallService",
                                          "firewall",
                                          "api.symphonyvpdc.savvis.net+xml",
                                          URI
                                                   .create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/FirewallService"))));
      assertEquals(result.getAvailableNetworks(), ImmutableSet.of());
   }

   public void test1net() {
      InputStream is = getClass().getResourceAsStream("/vdc-1net.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "demo6");
      assertEquals(result.getDescription(),
               "ServiceProfileName = Essential; ServiceLocation = US_WEST; Email = red@chair.com;");
      assertEquals(result.getStatus(), Status.DEPLOYED);
      assertEquals(result.getResourceEntities(), ImmutableSet.of(new ResourceImpl("1001", "Host1",
               VCloudMediaType.VAPP_XML, URI
                        .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/1619/vApp/1001")),
               new ResourceImpl("1002", "Host2", VCloudMediaType.VAPP_XML, URI
                        .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/1619/vApp/1002")),
               new ResourceImpl("1003", "Host3", VCloudMediaType.VAPP_XML, URI
                        .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/1619/vApp/1003")),
               new ResourceImpl("1004", "Host4", VCloudMediaType.VAPP_XML, URI
                        .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/1619/vApp/1004"))));
      assertEquals(result.getAvailableNetworks(), ImmutableSet.of(ResourceImpl.builder().id("VM-Tier01").name(
               "VM Tier01").type(VCloudMediaType.NETWORK_XML).href(
               URI.create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/1619/network/VM-Tier01"))
               .build()));

   }

   public void testFailed() {
      InputStream is = getClass().getResourceAsStream("/vdc-failed.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "Demo7");
      assertEquals(result.getDescription(),
               "ServiceProfileName = Essential; ServiceLocation = US_WEST; Email = red@chair.com;");
      assertEquals(result.getStatus(), Status.FAILED);
      assertEquals(result.getResourceEntities(), ImmutableSet.of(new ResourceImpl("1001", "Host1",
               VCloudMediaType.VAPP_XML, URI
                        .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/1641/vApp/1001"))));
      assertEquals(result.getAvailableNetworks(), ImmutableSet.of(ResourceImpl.builder().id("VM-Tier01").name(
               "VM Tier01").type(VCloudMediaType.NETWORK_XML).href(
               URI.create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/1641/network/VM-Tier01"))
               .build()));

   }

   public void testSaved() {
      InputStream is = getClass().getResourceAsStream("/vdc-saved.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      VDC result = factory.create(injector.getInstance(VDCHandler.class)).parse(is);
      assertEquals(result.getName(), "CloudBurst1");
      assertEquals(result.getDescription(),
               "ServiceProfileName = Essential; ServiceLocation = US_WEST; Email = me@my.com;");
      assertEquals(result.getStatus(), Status.SAVED);
      assertEquals(
               result.getResourceEntities(),
               ImmutableSet
                        .of(
                                 ResourceImpl
                                          .builder()
                                          .name("templateHost")
                                          .type("application/vnd.vmware.vcloud.vApp+xml")
                                          .id("1001")
                                          .href(
                                                   URI
                                                            .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/2555/vApp/1001"))
                                          .build(),
                                 ResourceImpl
                                          .builder()
                                          .name("host-c501")
                                          .type("application/vnd.vmware.vcloud.vApp+xml")
                                          .id("1037")
                                          .href(
                                                   URI
                                                            .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/2555/vApp/1037"))
                                          .build(),
                                 ResourceImpl
                                          .builder()
                                          .name("host-c501")
                                          .type("application/vnd.vmware.vcloud.vApp+xml")
                                          .id("1038")
                                          .href(
                                                   URI
                                                            .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/2555/vApp/1038"))
                                          .build(),
                                 ResourceImpl
                                          .builder()
                                          .name("host-c601")
                                          .type("application/vnd.vmware.vcloud.vApp+xml")
                                          .id("1039")
                                          .href(
                                                   URI
                                                            .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/2555/vApp/1039"))
                                          .build()));
      assertEquals(result.getAvailableNetworks(), ImmutableSet.of(ResourceImpl.builder().id("VM-Tier01").name(
               "VM Tier01").type(VCloudMediaType.NETWORK_XML).href(
               URI.create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/606677.0/vdc/2555/network/VM-Tier01"))
               .build()));

   }
}
