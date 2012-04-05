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

package org.jclouds.virtualbox.config;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_ARCH;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_OS;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_IMAGE_VERSION;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.jclouds.byon.BYONApiMetadata;
import org.jclouds.byon.Node;
import org.jclouds.byon.functions.NodeToNodeMetadata;
import org.jclouds.byon.suppliers.SupplyFromProviderURIOrNodesProperty;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.Host;
import org.jclouds.virtualbox.compute.VirtualBoxComputeServiceAdapter;
import org.jclouds.virtualbox.domain.CloneSpec;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.functions.CloneAndRegisterMachineFromIMachineIfNotAlreadyExists;
import org.jclouds.virtualbox.functions.CreateAndInstallVm;
import org.jclouds.virtualbox.functions.IMachineToHardware;
import org.jclouds.virtualbox.functions.IMachineToImage;
import org.jclouds.virtualbox.functions.IMachineToNodeMetadata;
import org.jclouds.virtualbox.functions.IMachineToSshClient;
import org.jclouds.virtualbox.functions.MastersLoadingCache;
import org.jclouds.virtualbox.functions.NodeCreator;
import org.jclouds.virtualbox.functions.YamlImagesFromFileConfig;
import org.jclouds.virtualbox.functions.admin.FileDownloadFromURI;
import org.jclouds.virtualbox.functions.admin.ImagesToYamlImagesFromYamlDescriptor;
import org.jclouds.virtualbox.functions.admin.PreseedCfgServer;
import org.jclouds.virtualbox.functions.admin.StartVBoxIfNotAlreadyRunning;
import org.jclouds.virtualbox.predicates.SshResponds;
import org.testng.internal.annotations.Sets;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * @author Mattias Holmqvist, Andrea Turli
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class VirtualBoxComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<Supplier, Supplier, IMachine, IMachine, Image, Location> {

   public VirtualBoxComputeServiceContextModule() {
      super(Supplier.class, Supplier.class);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<IMachine, IMachine, Image, Location>>() {
      }).to(VirtualBoxComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<IMachine, NodeMetadata>>() {
      }).to(IMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<IMachine, Hardware>>() {
      }).to(IMachineToHardware.class);
      bind(new TypeLiteral<Function<IMachine, Image>>() {
      }).to(IMachineToImage.class);
      bind(new TypeLiteral<CacheLoader<IsoSpec, URI>>() {
      }).to((Class) PreseedCfgServer.class);
      bind(new TypeLiteral<Function<URI, File>>() {
      }).to((Class) FileDownloadFromURI.class);

      bind(new TypeLiteral<Supplier<VirtualBoxManager>>() {
      }).to((Class) StartVBoxIfNotAlreadyRunning.class);
      // the yaml config to image mapper
      bind(new TypeLiteral<Supplier<Map<Image, YamlImage>>>() {
      }).to((Class) ImagesToYamlImagesFromYamlDescriptor.class);
      // the yaml config provider
      bind(YamlImagesFromFileConfig.class);

      // the master machines cache
      bind(new TypeLiteral<LoadingCache<Image, Master>>() {
      }).to(MastersLoadingCache.class);

      // the master creating function
      bind(new TypeLiteral<Function<MasterSpec, IMachine>>() {
      }).to((Class) CreateAndInstallVm.class);
      // the machine cloning function
      bind(new TypeLiteral<Function<NodeSpec, NodeAndInitialCredentials<IMachine>>>() {
      }).to((Class) NodeCreator.class);
      bind(new TypeLiteral<Function<CloneSpec, IMachine>>() {
      }).to((Class) CloneAndRegisterMachineFromIMachineIfNotAlreadyExists.class);
      // the jetty server provider
      bind(new TypeLiteral<Server>() {
      }).to((Class) PreseedCfgServer.class).asEagerSingleton();

      // for byon
      bind(new TypeLiteral<Function<URI, InputStream>>() {
      }).to(SupplyFromProviderURIOrNodesProperty.class);

      bind(new TypeLiteral<Function<IMachine, SshClient>>() {
      }).to(IMachineToSshClient.class);

      bind(ExecutionType.class).toInstance(ExecutionType.HEADLESS);
      bind(LockType.class).toInstance(LockType.Write);
   }

   @Provides
   @Host
   @Singleton
   protected ComputeServiceContext provideHostController() {
      return ComputeServiceContextBuilder.newBuilder(new BYONApiMetadata())
            .credentials("", "")
            .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule()))
            .build();
   }

   @Provides
   @Singleton
   protected Function<Supplier<NodeMetadata>, VirtualBoxManager> provideVBox() {
      return new Function<Supplier<NodeMetadata>, VirtualBoxManager>() {

         @Override
         public VirtualBoxManager apply(Supplier<NodeMetadata> nodeSupplier) {
            return VirtualBoxManager.createInstance(nodeSupplier.get().getId());
         }

         @Override
         public String toString() {
            return "createInstanceByNodeId()";
         }

      };
   }

   @Provides
   @Singleton
   protected Supplier defaultClient(Supplier<VirtualBoxManager> in) {
      return in;
   }

   @Provides
   @Singleton
   protected Predicate<SshClient> sshResponds(SshResponds sshResponds, Timeouts timeouts) {
      return new RetryablePredicate<SshClient>(sshResponds, timeouts.nodeRunning, 500l, TimeUnit.MILLISECONDS);
   }

   @Override
   protected Supplier provideHardware(ComputeServiceAdapter<IMachine, IMachine, Image, Location> adapter,
            Function<IMachine, Hardware> transformer) {
      // since no vms might be available we need to list images
      Iterable<Image> images = adapter.listImages();
      Set<Hardware> hardware = Sets.newHashSet();
      for (Image image : images) {
         hardware.add(new HardwareBuilder().ids(image.getId()).hypervisor("VirtualBox").name(image.getName()).build());
      }
      return Suppliers.ofInstance(hardware);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(VIRTUALBOX_DEFAULT_IMAGE_OS).osVersionMatches(VIRTUALBOX_DEFAULT_IMAGE_VERSION)
               .osArchMatches(VIRTUALBOX_DEFAULT_IMAGE_ARCH);
   }

   @Provides
   @Singleton
   protected Supplier<NodeMetadata> host(Supplier<LoadingCache<String, Node>> nodes, NodeToNodeMetadata converter)
            throws ExecutionException {
      return Suppliers.compose(Functions.compose(converter, new Function<LoadingCache<String, Node>, Node>() {

         @Override
         public Node apply(LoadingCache<String, Node> arg0) {
            return arg0.apply("host");
         }
      }), nodes);
   }

   @VisibleForTesting
   public static final Map<MachineState, NodeState> machineToNodeState = ImmutableMap
            .<MachineState, NodeState> builder().put(MachineState.Running, NodeState.RUNNING)
            .put(MachineState.PoweredOff, NodeState.SUSPENDED)
            .put(MachineState.DeletingSnapshot, NodeState.PENDING)
            .put(MachineState.DeletingSnapshotOnline, NodeState.PENDING)
            .put(MachineState.DeletingSnapshotPaused, NodeState.PENDING)
            .put(MachineState.FaultTolerantSyncing, NodeState.PENDING)
            .put(MachineState.LiveSnapshotting, NodeState.PENDING)
            .put(MachineState.SettingUp, NodeState.PENDING)
            .put(MachineState.Starting, NodeState.PENDING)
            .put(MachineState.Stopping, NodeState.PENDING)
            .put(MachineState.Restoring, NodeState.PENDING)
            // TODO What to map these states to?
            .put(MachineState.FirstOnline, NodeState.PENDING).put(MachineState.FirstTransient, NodeState.PENDING)
            .put(MachineState.LastOnline, NodeState.PENDING).put(MachineState.LastTransient, NodeState.PENDING)
            .put(MachineState.Teleported, NodeState.PENDING).put(MachineState.TeleportingIn, NodeState.PENDING)
            .put(MachineState.TeleportingPausedVM, NodeState.PENDING).put(MachineState.Aborted, NodeState.ERROR)
            .put(MachineState.Stuck, NodeState.ERROR).put(MachineState.Null, NodeState.UNRECOGNIZED).build();

}
