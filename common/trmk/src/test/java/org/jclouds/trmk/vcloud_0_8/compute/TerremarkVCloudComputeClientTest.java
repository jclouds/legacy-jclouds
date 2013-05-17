/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.compute;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.inject.Provider;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.options.InstantiateVAppTemplateOptions;
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
      VAppTemplate template = createMock(VAppTemplate.class);
      VDC vdc = createMock(VDC.class);
      URI templateURI = URI.create("template");
      URI vdcURI = URI.create("vdc");

      expect(template.getDescription()).andReturn(description).atLeastOnce();
      TerremarkVCloudClient client = createMock(TerremarkVCloudClient.class);
      VApp vApp = createMock(VApp.class);
      InternetServiceAndPublicIpAddressSupplier supplier = createMock(InternetServiceAndPublicIpAddressSupplier.class);
      expect(client.getVAppTemplate(templateURI)).andReturn(template);
      expect(
            client.instantiateVAppTemplateInVDC(vdcURI, templateURI, "name",
                  new InstantiateVAppTemplateOptions().productProperty("password", "password"))).andReturn(vApp);
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

      Predicate<VApp> notFoundTester = createMock(Predicate.class);
      Map<Status, NodeMetadata.Status> vAppStatusToNodeStatus = createMock(Map.class);

      TerremarkVCloudComputeClient computeClient = new TerremarkVCloudComputeClient(client,
            new Provider<String>() {

               @Override
               public String get() {
                  return "password";
               }

            }, successTester, vAppStatusToNodeStatus, credentialStore, supplier);

      replay(vdc);
      replay(template);
      replay(vApp);
      replay(task);
      replay(client);
      replay(successTester);
      replay(notFoundTester);
      replay(vAppStatusToNodeStatus);

      NodeAndInitialCredentials<VApp> response = computeClient.startAndReturnCredentials(vdcURI, templateURI, "name", new InstantiateVAppTemplateOptions());

      assertEquals(response.getNodeId(), "vapp");
      assertEquals(response.getNode(),vApp);
      assertEquals(response.getCredentials(), LoginCredentials.builder().password("password").build());

      verify(vdc);
      verify(template);
      verify(vApp);
      verify(task);
      verify(client);
      verify(successTester);
      verify(notFoundTester);
      verify(vAppStatusToNodeStatus);
   }
}
