/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.vcloud_0_8.compute;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.inject.Provider;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.compute.TerremarkVCloudComputeClient;
import org.jclouds.trmk.vcloud_0_8.compute.strategy.ParseVAppTemplateDescriptionToGetDefaultLoginCredentials;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.TerremarkVDC;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudExpressVApp;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudExpressVAppTemplate;
import org.jclouds.trmk.vcloud_0_8.options.TerremarkInstantiateVAppTemplateOptions;
import org.jclouds.trmk.vcloud_0_8.suppliers.InternetServiceAndPublicIpAddressSupplier;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class TerremarkVCloudComputeClientTest {
   @SuppressWarnings("unchecked")
   @Test
   public void testStartWindows() throws IOException {
      Map<String, Credentials> credentialStore = Maps.newHashMap();
      InputStream is = getClass().getResourceAsStream("/windows_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VCloudExpressVAppTemplate template = createMock(VCloudExpressVAppTemplate.class);
      TerremarkVDC vdc = createMock(TerremarkVDC.class);
      URI templateURI = URI.create("template");
      URI vdcURI = URI.create("vdc");

      expect(template.getDescription()).andReturn(description).atLeastOnce();
      TerremarkVCloudClient client = createMock(TerremarkVCloudClient.class);
      VCloudExpressVApp vApp = createMock(VCloudExpressVApp.class);
      InternetServiceAndPublicIpAddressSupplier supplier = createMock(InternetServiceAndPublicIpAddressSupplier.class);
      expect(client.getVAppTemplate(templateURI)).andReturn(template);
      expect(
               client.instantiateVAppTemplateInVDC(vdcURI, templateURI, "name",
                        new TerremarkInstantiateVAppTemplateOptions().productProperty("password", "password")))
               .andReturn(vApp);
      Task task = createMock(Task.class);
      URI vappLocation = URI.create("vapp");
      URI taskLocation = URI.create("task");

      expect(vApp.getHref()).andReturn(vappLocation).atLeastOnce();
      expect(vApp.getName()).andReturn("name").atLeastOnce();
      expect(client.deployVApp(vappLocation)).andReturn(task);
      expect(task.getHref()).andReturn(taskLocation).atLeastOnce();
      Predicate<URI> successTester = createMock(Predicate.class);
      expect(successTester.apply(taskLocation)).andReturn(true).atLeastOnce();
      expect(client.powerOnVApp(vappLocation)).andReturn(task);

      Predicate<VCloudExpressVApp> notFoundTester = createMock(Predicate.class);
      Map<Status, NodeState> vAppStatusToNodeState = createMock(Map.class);

      TerremarkVCloudComputeClient computeClient = new TerremarkVCloudComputeClient(client,
               new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials(), new Provider<String>() {

                  @Override
                  public String get() {
                     return "password";
                  }

               }, successTester, vAppStatusToNodeState, credentialStore, supplier);

      replay(vdc);
      replay(template);
      replay(vApp);
      replay(task);
      replay(client);
      replay(successTester);
      replay(notFoundTester);
      replay(vAppStatusToNodeState);

      VCloudExpressVApp response = computeClient.start(vdcURI, templateURI, "name",
               new TerremarkInstantiateVAppTemplateOptions());

      assertEquals(response.getHref().toASCIIString(), "vapp");
      assertEquals(credentialStore.get("node#vapp"), new Credentials("Administrator", "password"));

      verify(vdc);
      verify(template);
      verify(vApp);
      verify(task);
      verify(client);
      verify(successTester);
      verify(notFoundTester);
      verify(vAppStatusToNodeState);
   }
}
