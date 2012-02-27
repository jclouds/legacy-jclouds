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

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import org.jclouds.Constants;
import org.jclouds.byon.Node;
import org.jclouds.byon.config.CacheNodeStoreModule;
import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.config.ValueOfConfigurationKeyOrNull;
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
import org.virtualbox_4_1.VirtualBoxManager;

import java.net.URI;
import java.util.Properties;

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
   protected Supplier<VirtualBoxManager> manager;
   protected MachineUtils machineUtils;
   protected LoadingCache<IsoSpec, URI> preconfigurationUri;
   protected String hostVersion;
   protected String operatingSystemIso;
   protected String guestAdditionsIso;
   protected String adminDisk;
   protected String workingDir;
   protected Supplier<NodeMetadata> host;

   @Override
   protected void setupCredentials() {
      // default behavior is to bomb when no user is configured, but we know the
      // default user of
      // vbox
      ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults();
      super.setupCredentials();
   }

   protected void ensureIdentityPropertyIsSpecifiedOrTakeFromDefaults() {
      Properties defaultVBoxProperties = new VirtualBoxPropertiesBuilder()
              .build();
      if (!System.getProperties().containsKey("test." + provider + ".identity"))
         System.setProperty("test." + provider + ".identity",
                 defaultVBoxProperties.getProperty(Constants.PROPERTY_IDENTITY));
   }

   @BeforeClass(groups = "live")
   public void setupClient() {
      setupCredentials();
      Properties overrides = new VirtualBoxPropertiesBuilder(setupProperties()).build();

      CacheNodeStoreModule hostModule = new CacheNodeStoreModule(
              ImmutableMap.of(
                      "host",
                      Node.builder()
                              .id("host")
                              .name("host installing virtualbox")
                              .hostname("localhost")
                              .osFamily(OsFamily.LINUX.toString())
                              .osDescription(System.getProperty("os.name"))
                              .osVersion(System.getProperty("os.version"))
                              .group("ssh")
                              .username(System.getProperty("user.name"))
                              .credentialUrl(
                                      URI.create("file://"
                                              + System.getProperty("user.home")
                                              + "/.ssh/id_rsa")).build()));

      context = new ComputeServiceContextFactory().createContext(provider,
              identity, credential, ImmutableSet.<Module>of(
              new SLF4JLoggingModule(), new SshjSshClientModule(),
              hostModule), overrides);
      Function<String, String> configProperties = context.utils().injector()
              .getInstance(ValueOfConfigurationKeyOrNull.class);
      imageId = configProperties
              .apply(ComputeServiceConstants.PROPERTY_IMAGE_ID);
      workingDir = configProperties
              .apply(VirtualBoxConstants.VIRTUALBOX_WORKINGDIR);
      host = context.utils().injector()
              .getInstance(Key.get(new TypeLiteral<Supplier<NodeMetadata>>() {
              }));

      // this will eagerly startup Jetty, note the impl will shut itself down
      preconfigurationUri = context.utils().injector().getInstance(Key.get(new TypeLiteral<LoadingCache<IsoSpec, URI>>() {
      }, Preconfiguration.class));
      // this will eagerly startup Jetty, note the impl will shut itself down

      manager = context.utils().injector().getInstance(Key.get(new TypeLiteral<Supplier<VirtualBoxManager>>() {
      }));
      // this will eagerly startup vbox
      manager.get();

      machineUtils = context.utils().injector().getInstance(MachineUtils.class);

      hostVersion = Iterables.get(
              Splitter.on('r').split(
                      context.getProviderSpecificContext().getBuildVersion()), 0);
      adminDisk = workingDir + "/testadmin.vdi";
      operatingSystemIso = String.format("%s/%s.iso", workingDir, imageId);
      guestAdditionsIso = String.format("%s/VBoxGuestAdditions_%s.iso",
              workingDir, hostVersion);
   }

   protected void undoVm(VmSpec vmSpecification) {
      machineUtils.unlockMachineAndApplyOrReturnNullIfNotRegistered(
              vmSpecification.getVmId(),
              new UnregisterMachineIfExistsAndDeleteItsMedia(vmSpecification));
   }

   @AfterClass(groups = "live")
   protected void tearDown() throws Exception {
      if (context != null)
         context.close();
   }

}
