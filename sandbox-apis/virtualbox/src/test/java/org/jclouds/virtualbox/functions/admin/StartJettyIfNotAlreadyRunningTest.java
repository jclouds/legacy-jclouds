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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.easymock.EasyMock;
import org.eclipse.jetty.server.Server;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.testng.annotations.Test;

/**
 * @author Andrea Turli, Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "StartJettyIfNotAlreadyRunningTest")
public class StartJettyIfNotAlreadyRunningTest {
   @Test
   public void testLaunchJettyServerWhenAlreadyRunningDoesntLaunchAgain() throws Exception {
      Server jetty = createMock(Server.class);
      Supplier<Server> serverSupplier = createMock(Supplier.class);

      String preconfigurationUrl = "http://foo:8080";

      expect(jetty.getState()).andReturn(Server.STARTED);
      expect(serverSupplier.get()).andReturn(jetty);

      replay(jetty, serverSupplier);

      StartJettyIfNotAlreadyRunning starter = new StartJettyIfNotAlreadyRunning(preconfigurationUrl, serverSupplier);

      IsoSpec isoSpec = IsoSpec.builder()
              .sourcePath("/tmp/myisos/ubuntu.iso")
              .installationScript("install").build();
      assertEquals(starter.load(isoSpec), URI.create(preconfigurationUrl));
      verify(jetty);
   }

   @Test
   public void testLaunchJettyServerWhenNotRunningStartsJettyOnCorrectHostPortAndBasedir() {
      // TODO: all yours!
   }
}
