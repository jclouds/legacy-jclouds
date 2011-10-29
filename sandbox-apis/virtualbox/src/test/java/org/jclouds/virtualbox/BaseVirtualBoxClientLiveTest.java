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

package org.jclouds.virtualbox;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;

import java.net.URI;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.functions.admin.StartJettyIfNotAlreadyRunning;
import org.jclouds.virtualbox.functions.admin.StartVBoxIfNotAlreadyRunning;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VirtualBoxClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BaseVirtualBoxClientLiveTest {

   protected String provider = "virtualbox";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint", "http://localhost:18083/");
      apiversion = System.getProperty("test." + provider + ".apiversion", "4.1.2r73507");
   }

   protected ComputeServiceContext context;
   protected VirtualBoxManager manager;
   protected Server jetty;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      Properties properties = new Properties();
      properties.setProperty(provider + ".endpoint", endpoint);
      properties.setProperty(provider + ".apiversion", apiversion);
      context = new ComputeServiceContextFactory().createContext(provider, identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()));
      jetty = new StartJettyIfNotAlreadyRunning(port).apply(basebaseResource);
      startVboxIfNotAlreadyRunning();
   }

   @AfterGroups(groups = "live")
   protected void tearDown() throws Exception {
      if (context != null)
         context.close();
      if (jetty != null)
         jetty.stop();
      // TODO: should we stop the vbox manager?
   }

   private String basebaseResource = ".";
   // TODO: I'd not use 8080, maybe something like 28080
   // also update pom.xml so that this passes through
   private int port = Integer.parseInt(System.getProperty(VirtualBoxConstants.VIRTUALBOX_JETTY_PORT, "8080"));

   protected void startVboxIfNotAlreadyRunning() {
      Credentials localhostCredentials = new Credentials("toor", "password");
      ComputeServiceContext localHostContext = computeServiceForLocalhostAndGuest("hostId", "localhost", "guestId",
            "localhost", localhostCredentials);

      manager = new StartVBoxIfNotAlreadyRunning(localHostContext.getComputeService(),
            VirtualBoxManager.createInstance("hostId"), new InetSocketAddressConnect(), "hostId", localhostCredentials)
            .apply(URI.create(endpoint));

      assert manager.getSessionObject().getState() == SessionState.Unlocked : "manager needs to be in unlocked state or all tests will fail!!: "
            + manager;
   }
}
