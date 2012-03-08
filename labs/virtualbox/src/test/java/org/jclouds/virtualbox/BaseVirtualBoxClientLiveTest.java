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

import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.byon.Node;
import org.jclouds.byon.config.CacheNodeStoreModule;
import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.strategy.PrioritizeCredentialsFromTemplate;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.jclouds.virtualbox.util.MachineUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.VirtualBoxManager;
import org.virtualbox_4_1.jaxws.MachineState;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
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
   
   @Inject
   protected Supplier<VirtualBoxManager> manager;
   
   @Inject
   void eagerlyStartManager(Supplier<VirtualBoxManager> manager){
      this.manager = manager;
      manager.get();
   }
   
   @Inject
   protected MachineUtils machineUtils;

   // this will eagerly startup Jetty, note the impl will shut itself down
   @Inject
   @Preconfiguration
   protected LoadingCache<IsoSpec, URI> preconfigurationUri;
   
   protected String hostVersion;
   protected String operatingSystemIso;
   protected String guestAdditionsIso;
   @Inject
   @Named(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR)
   protected String workingDir;
   protected String isosDir;
   @Inject
   protected Supplier<NodeMetadata> host;
   @Inject
   protected PrioritizeCredentialsFromTemplate prioritizeCredentialsFromTemplate;

   @Override
   protected void setupCredentials() {
      // default behavior is to bomb when no user is configured, but we know the
      // default user of
      // vbox
      ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults();
      super.setupCredentials();
   }

   protected void ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults() {
      Properties defaultVBoxProperties = new VirtualBoxPropertiesBuilder().build();
      if (!System.getProperties().containsKey("test." + provider + ".identity"))
         System.setProperty("test." + provider + ".identity",
                  defaultVBoxProperties.getProperty(Constants.PROPERTY_IDENTITY));
   }

   @BeforeClass(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = new VirtualBoxPropertiesBuilder(setupProperties()).build();

      CacheNodeStoreModule hostModule = new CacheNodeStoreModule(ImmutableMap.of(
               "host",
               Node.builder().id("host").name("host installing virtualbox").hostname("localhost")
                        .osFamily(OsFamily.LINUX.toString()).osDescription(System.getProperty("os.name"))
                        .osVersion(System.getProperty("os.version")).group("ssh")
                        .username(System.getProperty("user.name"))
                        .credentialUrl(URI.create("file://" + System.getProperty("user.home") + "/.ssh/id_rsa"))
                        .build()));

      context = new ComputeServiceContextFactory().createContext(provider, identity, credential,
               ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule(), hostModule), overrides);
      context.utils().injector().injectMembers(this);
      
      imageId = "ubuntu-11.04-server-i386";
      isosDir = workingDir + File.separator + "isos";
      
      hostVersion = Iterables.get(Splitter.on('r').split(context.getProviderSpecificContext().getBuildVersion()), 0);
      operatingSystemIso = String.format("%s/%s.iso", isosDir, imageId);
      guestAdditionsIso = String.format("%s/VBoxGuestAdditions_%s.iso", isosDir, hostVersion);
   }

   protected void undoVm(VmSpec vmSpecification) {
      machineUtils.unlockMachineAndApplyOrReturnNullIfNotRegistered(vmSpecification.getVmId(),
               new UnregisterMachineIfExistsAndDeleteItsMedia(vmSpecification));
   }

   protected void ensureMachineHasPowerDown(String vmName) {
      while (!manager.get().getVBox().findMachine(vmName).getState().equals(MachineState.POWERED_OFF)) {
         try {
            machineUtils.lockSessionOnMachineAndApply(vmName, LockType.Shared, new Function<ISession, Void>() {
               @Override
               public Void apply(ISession session) {
                  IProgress powerDownProgress = session.getConsole().powerDown();
                  powerDownProgress.waitForCompletion(-1);
                  return null;
               }
            });
         } catch (RuntimeException e) {
            // sometimes the machine might be powered of between the while test and the call to
            // lockSessionOnMachineAndApply
            if (e.getMessage().contains("Invalid machine state: PoweredOff")) {
               return;
            } else if (e.getMessage().contains("VirtualBox error: The object is not ready")) {
               continue;
            } else {
               throw e;
            }
         }
      }
   }
   
   public String adminDisk(String vmName) {
      return workingDir + File.separator + vmName + ".vdi";
   }

   @AfterClass(groups = "live")
   protected void tearDown() throws Exception {
      if (context != null)
         context.close();
   }

}
