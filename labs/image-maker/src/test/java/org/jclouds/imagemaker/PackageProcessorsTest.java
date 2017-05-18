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

package org.jclouds.imagemaker;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.imagemaker.internal.AptCacher;
import org.jclouds.imagemaker.internal.AptInstaller;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class PackageProcessorsTest {

   private static final String nodeName = "test_node";
   private Capture<Statement> stmtCapture;
   private Capture<String> nodeCapture;

   @Test
   public void testAptInstaller() {
      AptInstaller installer = new AptInstaller(mockContextThatCapturesExecutedStatement());
      installer.process(mockNode(), ImmutableList.of("package1", "package2"));
      assertEquals(nodeCapture.getValue(), nodeName);
      assertEquals(stmtCapture.getValue().render(OsFamily.UNIX), "apt-get -y install package1 package2");
   }

   @Test
   public void testAptCacher() {
      AptCacher installer = new AptCacher(mockContextThatCapturesExecutedStatement());
      installer.process(mockNode(), ImmutableList.of("package1", "package2"));
      assertEquals(nodeCapture.getValue(), nodeName);
      assertEquals(stmtCapture.getValue().render(OsFamily.UNIX), "apt-get -d -y install package1 package2");
   }

   private ComputeServiceContext mockContextThatCapturesExecutedStatement() {
      ComputeServiceContext ctx = EasyMock.createMock(ComputeServiceContext.class);
      ComputeService service = EasyMock.createMock(ComputeService.class);
      stmtCapture = new Capture<Statement>();
      nodeCapture = new Capture<String>();
      expect(service.runScriptOnNode(capture(nodeCapture), capture(stmtCapture)))
               .andReturn(new ExecResponse("", "", 0)).once();
      expect(ctx.getComputeService()).andReturn(service).once();
      replay(service, ctx);
      return ctx;
   }

   private NodeMetadata mockNode() {
      NodeMetadata node = EasyMock.createMock(NodeMetadata.class);
      expect(node.getId()).andReturn(nodeName).once();
      replay(node);
      return node;
   }
}
