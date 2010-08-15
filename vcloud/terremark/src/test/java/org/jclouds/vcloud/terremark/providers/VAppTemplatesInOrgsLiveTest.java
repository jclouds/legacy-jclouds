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

package org.jclouds.vcloud.terremark.providers;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.domain.Image;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.functions.AllCatalogItemsInOrganization;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.compute.config.providers.VAppTemplatesInOrgs;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VAppTemplatesInOrgs}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "terremark.VAppTemplatesInOrgsLiveTest")
public class VAppTemplatesInOrgsLiveTest {

   private TerremarkVCloudClient tmClient;
   private VAppTemplatesInOrgs parser;
   private Closer closer;
   private AllCatalogItemsInOrganization allCatalogItemsInOrganization;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      String endpoint = System.getProperty("jclouds.test.endpoint");
      Properties props = new Properties();
      if (endpoint != null && !"".equals(endpoint))
         props.setProperty("terremark.endpoint", endpoint);
      Injector injector = new RestContextFactory().createContextBuilder("trmk-vcloudexpress", identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), props).buildInjector();

      tmClient = injector.getInstance(TerremarkVCloudClient.class);
      allCatalogItemsInOrganization = injector.getInstance(AllCatalogItemsInOrganization.class);
      parser = injector.getInstance(VAppTemplatesInOrgs.class);
      closer = injector.getInstance(Closer.class);
   }

   @Test
   public void testParseAllImages() {

      Set<? extends Image> images = parser.get();

      Iterable<? extends CatalogItem> templates = allCatalogItemsInOrganization.apply(tmClient
            .findOrganizationNamed(null));

      assertEquals(images.size(), Iterables.size(templates));
      assert images.size() > 0;
   }

   @AfterGroups(groups = { "live" })
   public void close() throws IOException {
      closer.close();
   }
}
