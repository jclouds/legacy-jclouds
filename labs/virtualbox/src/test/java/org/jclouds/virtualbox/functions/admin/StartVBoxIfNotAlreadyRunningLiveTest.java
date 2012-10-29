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
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import java.net.URI;

import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.callables.RunScriptOnNode.Factory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.virtualbox.predicates.RetryIfSocketNotYetOpen;
import org.testng.annotations.Test;
import org.virtualbox_4_1.VirtualBoxManager;

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
      RetryIfSocketNotYetOpen client = createMock(RetryIfSocketNotYetOpen.class);
      NodeMetadata host = new NodeMetadataBuilder().id("host").status(Status.RUNNING).build();
      URI provider = URI.create("http://localhost:18083/");
      expect(client.seconds(3)).andReturn(client);
      expect(client.apply(HostAndPort.fromParts(provider.getHost(), provider.getPort()))).andReturn(true).anyTimes();
      manager.connect(provider.toASCIIString(), "", "");
      expectLastCall().anyTimes();

      replay(manager, runScriptOnNodeFactory, client);

      new StartVBoxIfNotAlreadyRunning((Function) Functions.constant(manager), runScriptOnNodeFactory, client,
               Suppliers.ofInstance(host), Suppliers.ofInstance(provider), null).start();

      verify(manager, runScriptOnNodeFactory, client);

   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Test
   public void testStartVboxDisablesPasswordAccessOnWebsrvauthlibraryStartsVboxwebsrvInBackgroundAndConnectsManagerWhenPortIsNotListening()
            throws Exception {
      VirtualBoxManager manager = createMock(VirtualBoxManager.class);
      Factory runScriptOnNodeFactory = createMock(Factory.class);
      RetryIfSocketNotYetOpen client = createMock(RetryIfSocketNotYetOpen.class);
      RunScriptOnNode runScriptOnNode = createMock(RunScriptOnNode.class);
      NodeMetadata host = new NodeMetadataBuilder().id("host").status(Status.RUNNING).operatingSystem(
               OperatingSystem.builder().description("unix").build()).build();
      URI provider = URI.create("http://localhost:18083/");
      
      expect(client.seconds(3)).andReturn(client);
      expect(client.apply(HostAndPort.fromParts(provider.getHost(), provider.getPort()))).andReturn(false).once().andReturn(true).once();
      expect(runScriptOnNodeFactory.create(host, 
            Statements.exec("VBoxManage setproperty websrvauthlibrary null"),
                        runAsRoot(false).wrapInInitScript(false))).andReturn(runScriptOnNode);
      expect(runScriptOnNode.init()).andReturn(runScriptOnNode);
      expect(runScriptOnNode.call()).andReturn(new ExecResponse("", "", 0));
      
      expect(runScriptOnNodeFactory.create(host, 
                     Statements.exec("vboxwebsrv -t 10000 -v -b -H localhost"), runAsRoot(false)
                        .wrapInInitScript(false).blockOnComplete(false).nameTask("vboxwebsrv")))
                        .andReturn(runScriptOnNode);
      
      expect(runScriptOnNode.init()).andReturn(runScriptOnNode);
      expect(runScriptOnNode.call()).andReturn(new ExecResponse("", "", 0));
      manager.connect(provider.toASCIIString(), "", "");
      expectLastCall().anyTimes();

      replay(manager, runScriptOnNodeFactory, runScriptOnNode, client);
      new StartVBoxIfNotAlreadyRunning((Function) Functions.constant(manager), runScriptOnNodeFactory, client,
               Suppliers.ofInstance(host), Suppliers.ofInstance(provider), null);
      
      verify(manager, runScriptOnNodeFactory, runScriptOnNode, client);
   }
}
