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

import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;

import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.jclouds.Constants;
import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.functions.admin.StartJettyIfNotAlreadyRunning;
import org.jclouds.virtualbox.functions.admin.StartVBoxIfNotAlreadyRunning;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VirtualBoxClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "BaseVirtualBoxClientLiveTest")
public class BaseVirtualBoxClientLiveTest extends BaseVersionedServiceLiveTest {
   public BaseVirtualBoxClientLiveTest() {
      provider = "virtualbox";
   }
   
   protected ComputeServiceContext context;
   protected VirtualBoxManager manager;
   protected Server jetty;
   protected String hostVersion;
   protected String operatingSystemIso;
   protected String guestAdditionsIso;
   protected String adminDisk;
   protected String workingDir;
   
   @Override
   protected void setupCredentials() {
      // default behavior is to bomb when no user is configured, but we know the default user of vbox
      ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults();
      super.setupCredentials();
   }

   protected void ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults() {
      Properties defaultVBoxProperties = new VirtualBoxPropertiesBuilder().build();
      if (!System.getProperties().containsKey("test." + provider + ".identity"))
         System.setProperty("test." + provider + ".identity", defaultVBoxProperties
                  .getProperty(Constants.PROPERTY_IDENTITY));
   }

   @BeforeClass(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider, identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new SshjSshClientModule()), overrides);
      Function<String, String> configProperties = context.utils().injector().getInstance(ValueOfConfigurationKeyOrNull.class);
      imageId = configProperties.apply(ComputeServiceConstants.PROPERTY_IMAGE_ID);
      workingDir = configProperties.apply(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR);
      
      jetty = new StartJettyIfNotAlreadyRunning(port).apply(basebaseResource);
      startVboxIfNotAlreadyRunning();
      hostVersion = Iterables.get(Splitter.on('r').split(context.getProviderSpecificContext().getBuildVersion()), 0);
      adminDisk = workingDir + "/testadmin.vdi";
      operatingSystemIso = String.format("%s/%s.iso", workingDir, imageId);
      guestAdditionsIso = String.format("%s/VBoxGuestAdditions_%s.iso", workingDir, hostVersion);
   }

   @AfterClass(groups = "live")
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
            .apply(context.getProviderSpecificContext().getEndpoint());

      assert manager.getSessionObject().getState() == SessionState.Unlocked : "manager needs to be in unlocked state or all tests will fail!!: "
            + manager;
   }
}
