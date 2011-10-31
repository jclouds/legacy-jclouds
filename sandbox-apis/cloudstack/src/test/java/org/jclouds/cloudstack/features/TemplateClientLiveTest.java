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
package org.jclouds.cloudstack.features;

import static org.jclouds.cloudstack.options.ListTemplatesOptions.Builder.zoneId;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudstack.domain.Template;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code TemplateClientLiveTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "TemplateClientLiveTest")
public class TemplateClientLiveTest extends BaseCloudStackClientLiveTest {

   public void testListTemplates() throws Exception {
      Set<Template> response = client.getTemplateClient().listTemplates();
      assert null != response;
      long templateCount = response.size();
      assertTrue(templateCount >= 0);
      for (Template template : response) {
         Template newDetails = Iterables.getOnlyElement(client.getTemplateClient().listTemplates(
               zoneId(template.getZoneId()).id(template.getId())));
         assertEquals(template, newDetails);
         assertEquals(template, client.getTemplateClient().getTemplateInZone(template.getZoneId(), template.getId()));
         assert template.getId() > 0 : template;
         assert template.getName() != null : template;
         assert template.getDisplayText() != null : template;
         assert template.getCreated() != null : template;
         assert template.getFormat() != null && template.getFormat() != Template.Format.UNRECOGNIZED : template;
         assert template.getOSType() != null : template;
         assert template.getOSTypeId() > 0 : template;
         assert template.getAccount() != null : template;
         assert template.getZone() != null : template;
         assert template.getZoneId() > 0 : template;
         assert template.getStatus() == null : template;
         assert template.getType() != null && template.getType() != Template.Type.UNRECOGNIZED : template;
         assert template.getHypervisor() != null : template;
         assert template.getDomain() != null : template;
         assert template.getDomainId() > 0 : template;
      }
   }

}
