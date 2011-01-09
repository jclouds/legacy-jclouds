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

package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.jclouds.vcloud.domain.internal.VCloudExpressVAppTemplateImpl;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VAppTemplateHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class VCloudExpressVAppTemplateHandlerTest {

   private Injector injector;

   private Factory factory;

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/express/vAppTemplate-trmk.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressVAppTemplate result = factory.create(injector.getInstance(VCloudExpressVAppTemplateHandler.class)).parse(is);
      assertEquals(result, new VCloudExpressVAppTemplateImpl("CentOS 5.3 (32-bit)", URI
            .create("https://services.vcloudexpress.terremark.com/api/v0.8/vAppTemplate/5"),
            "description of CentOS 5.3 (32-bit)", null));
   }

   public void testHosting() {
      InputStream is = getClass().getResourceAsStream("/express/vAppTemplate-hosting.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      VCloudExpressVAppTemplate result = (VCloudExpressVAppTemplate) factory.create(injector.getInstance(VCloudExpressVAppTemplateHandler.class)).parse(is);
      assertEquals(result, new VCloudExpressVAppTemplateImpl("cPanel (Linux) 64 Bit", URI
            .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/4"), "cPanel (Linux) 64 Bit",
            Status.RESOLVED));
   }
}
