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

package org.jclouds.vcloud.terremark.compute;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Provider;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.terremark.TerremarkVCloudExpressClient;
import org.jclouds.vcloud.terremark.compute.strategy.ParseVAppTemplateDescriptionToGetDefaultLoginCredentials;
import org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.io.ByteStreams;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "terremark.TerremarkVCloudComputeClientTest")
public class TerremarkVCloudComputeClientTest {
   @SuppressWarnings("unchecked")
   @Test
   public void testStartWindows() throws IOException {
      InputStream is = getClass().getResourceAsStream("/terremark/windows_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VAppTemplate template = createMock(VAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      TerremarkVCloudExpressClient client = createMock(TerremarkVCloudExpressClient.class);
      expect(client.getVAppTemplate("templateId")).andReturn(template);
      VApp vApp = createMock(VApp.class);

      expect(
            client.instantiateVAppTemplateInOrg("org", "vDC", "name", "templateId",
                  new TerremarkInstantiateVAppTemplateOptions().productProperty("password", "password"))).andReturn(
            vApp);
      Task task = createMock(Task.class);

      expect(vApp.getId()).andReturn("1").atLeastOnce();
      expect(client.getVAppTemplate("templateId")).andReturn(template);
      expect(client.deployVApp("1")).andReturn(task);
      expect(task.getId()).andReturn("1").atLeastOnce();
      Predicate<String> successTester = createMock(Predicate.class);
      expect(successTester.apply("1")).andReturn(true).atLeastOnce();
      expect(client.powerOnVApp("1")).andReturn(task);

      Predicate<VApp> notFoundTester = createMock(Predicate.class);
      Map<VAppStatus, NodeState> vAppStatusToNodeState = createMock(Map.class);

      TerremarkVCloudComputeClient computeClient = new TerremarkVCloudComputeClient(client,
            new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(), new Provider<String>() {

               @Override
               public String get() {
                  return "password";
               }

            }, successTester, vAppStatusToNodeState);

      replay(template);
      replay(vApp);
      replay(task);
      replay(client);
      replay(successTester);
      replay(notFoundTester);
      replay(vAppStatusToNodeState);

      Map<String, String> response = computeClient.start("org", "vDC", "name", "templateId",
            new TerremarkInstantiateVAppTemplateOptions());

      assertEquals(response.get("id"), "1");
      assertEquals(response.get("username"), "Administrator");
      assertEquals(response.get("password"), "password");

      verify(template);
      verify(vApp);
      verify(task);
      verify(client);
      verify(successTester);
      verify(notFoundTester);
      verify(vAppStatusToNodeState);
   }
}
