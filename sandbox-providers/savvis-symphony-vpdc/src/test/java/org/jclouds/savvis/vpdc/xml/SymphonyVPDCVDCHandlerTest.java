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
import org.jclouds.savvis.vpdc.domain.SymphonyVPDCVDC;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code SymphonyVPDCVDCHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class SymphonyVPDCVDCHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/savvis/vdc.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      SymphonyVPDCVDC result = (SymphonyVPDCVDC) factory.create(injector.getInstance(SymphonyVPDCVDCHandler.class))
            .parse(is);
      assertEquals(result.getName(), "demo_vpdcname");
      assertEquals(result.getHref(), null);
      assertEquals(result.getDescription(),
            "ServiceProfileName = Balanced; ServiceLocation = North America; Email = jim@company.com;");
      assertEquals(result.getOfferingTag(), "Deployed");
      assertEquals(result.getStorageCapacity(), null);
      assertEquals(result.getCpuCapacity(), null);
      assertEquals(result.getMemoryCapacity(), null);
      assertEquals(result.getVmQuota(), 0);
      assertEquals(
            result.getResourceEntities(),
            ImmutableMap.<String, ReferenceType> of(
                  "DemoHost-1",
                  new ReferenceTypeImpl(
                        "DemoHost-1",
                        VCloudMediaType.VAPP_XML,
                        URI.create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1001")),
                  "DemoHost-2",
                  new ReferenceTypeImpl(
                        "DemoHost-2",
                        VCloudMediaType.VAPP_XML,
                        URI.create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1002")),
                  "DemoHost-3",
                  new ReferenceTypeImpl(
                        "DemoHost-3",
                        VCloudMediaType.VAPP_XML,
                        URI.create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vApp/1003")),
                  "CustomerTemplateName",
                  new ReferenceTypeImpl(
                        "CustomerTemplateName",
                        VCloudMediaType.VAPPTEMPLATE_XML,
                        URI.create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/vAppTemplate/1234")),
                  "firewall",
                  new ReferenceTypeImpl(
                        "firewall",
                        "api.sandbox.symphonyVPDC.savvis.net+xml",
                        URI.create("https://api.sandbox.symphonyvpdc.savvis.net/rest/api/v0.8/org/100000.0/vdc/2736/FirewallService"))));
      assertEquals(result.getAvailableNetworks(), ImmutableMap.of());
   }
}
