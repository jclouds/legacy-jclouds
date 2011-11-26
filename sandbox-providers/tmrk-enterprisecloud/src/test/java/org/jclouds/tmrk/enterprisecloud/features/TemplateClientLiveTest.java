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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.tmrk.enterprisecloud.domain.NamedResource;
import org.jclouds.tmrk.enterprisecloud.domain.template.*;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

/**
 * Tests behavior of {@code TemplateClientLiveTest}
 * TODO: don't hard-code uri's it should be possible to determine them but that means chaining the tests potentially.
 * @author Jason King
 */
@Test(groups = "live", testName = "TemplateClientLiveTest")
public class TemplateClientLiveTest extends BaseTerremarkEnterpriseCloudClientLiveTest {
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      super.setupClient();
      client = context.getApi().getTemplateClient();
   }

   private TemplateClient client;

   public void testGetTemplates() throws Exception {
      Templates templates = client.getTemplates(new URI("/cloudapi/ecloud/templates/computepools/89"));
      for(TemplateFamily family: templates.getTemplateFamilies()) {
         for(TemplateCategory category: family.getTemplateCategories()) {
            for(TemplateOperatingSystem os: category.getTemplateOperatingSystems()) {
               for(NamedResource templateReference: os.getTemplates()) {
                  testTemplate(templateReference);
               }
            }
         }
      }
   }

   private void testTemplate(NamedResource templateReference) {
      Template template = client.getTemplate(templateReference.getHref());
      assertNotNull(template);
      assertNotNull(template.getDescription());
   }

   public void testGetTemplatesWhenMissing() throws Exception {
       Templates templates = client.getTemplates(new URI("/cloudapi/ecloud/templates/computepools/-1"));
       assertNull(templates);
   }

   public void testGetTemplateWhenMissing() throws Exception {
       Template template = client.getTemplate(new URI("/cloudapi/ecloud/templates/-1/computepools/89"));
       assertNull(template);
   }
}
