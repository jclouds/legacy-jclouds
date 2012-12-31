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

package org.jclouds.virtualbox.functions.admin;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.net.URI;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.virtualbox.functions.HardcodedHostToHostNodeMetadata;
import org.jclouds.virtualbox.predicates.RetryIfSocketNotYetOpen;
import org.testng.annotations.Test;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Suppliers;
import com.google.common.net.HostAndPort;

@Test(groups = "live", singleThreaded = true, testName = "StartVBoxIfNotAlreadyRunningLiveTest")
public class StartVBoxIfNotAlreadyRunningLiveTest {

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Test
   public void testStartVboxConnectsToManagerWhenPortAlreadyListening() throws Exception {
      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      Factory runScriptOnNodeFactory = createMock(Factory.class);
      RunScriptOnNode runScriptOnNode = createMock(RunScriptOnNode.class);
      RetryIfSocketNotYetOpen client = createMock(RetryIfSocketNotYetOpen.class);
      HardcodedHostToHostNodeMetadata hardcodedHostToHostNodeMetadata = createMock(HardcodedHostToHostNodeMetadata.class);
      NodeMetadata host = new NodeMetadataBuilder().id("host").status(Status.RUNNING).build();
      URI provider = URI.create("http://localhost:18083/");
      expect(client.seconds(3)).andReturn(client);
      expect(client.apply(HostAndPort.fromParts(provider.getHost(), provider.getPort()))).andReturn(true).anyTimes();
      manager.connect(provider.toASCIIString(), "", "");
      expectLastCall().anyTimes();
      replay(manager, runScriptOnNodeFactory, runScriptOnNode, client);
      new StartVBoxIfNotAlreadyRunning((Function) Functions.constant(manager), runScriptOnNodeFactory, client,
               Suppliers.ofInstance(host), Suppliers.ofInstance(provider), hardcodedHostToHostNodeMetadata).start();
      verify(manager, runScriptOnNodeFactory, client);
   }
}
