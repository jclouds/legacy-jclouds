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
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.io.File;
import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.ssh.SshClient;
import org.jclouds.virtualbox.compute.VirtualBoxComputeServiceAdapter;
import org.jclouds.virtualbox.compute.extensions.VirtualBoxImageExtension;
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
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.LockType;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.VirtualBoxManager;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * @author Mattias Holmqvist, Andrea Turli
 */
@SuppressWarnings("unchecked")
public class VirtualBoxComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<IMachine, Hardware, Image, Location> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<IMachine, Hardware, Image, Location>>() {
      }).to(VirtualBoxComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<IMachine, NodeMetadata>>() {
      }).to(IMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<IMachine, Hardware>>() {
      }).to(IMachineToHardware.class);
      bind(new TypeLiteral<Function<IMachine, Image>>() {
      }).to(IMachineToImage.class);
      bind(new TypeLiteral<CacheLoader<IsoSpec, URI>>() {
      }).to(Class.class.cast(PreseedCfgServer.class));
      bind(new TypeLiteral<Function<URI, File>>() {
      }).to(Class.class.cast(FileDownloadFromURI.class));

      bind(new TypeLiteral<Supplier<VirtualBoxManager>>() {
      }).to(Class.class.cast(StartVBoxIfNotAlreadyRunning.class));
      // the yaml config to image mapper
      bind(new TypeLiteral<Supplier<Map<Image, YamlImage>>>() {
      }).to(Class.class.cast(ImagesToYamlImagesFromYamlDescriptor.class));
      // the yaml config provider
      bind(YamlImagesFromFileConfig.class);

      // the master machines cache
      bind(new TypeLiteral<LoadingCache<Image, Master>>() {
      }).to(MastersLoadingCache.class);

      // the vbox image extension
      bind(new TypeLiteral<ImageExtension>() {
      }).to(VirtualBoxImageExtension.class);

      // the master creating function
      bind(new TypeLiteral<Function<MasterSpec, IMachine>>() {
      }).to(Class.class.cast(CreateAndInstallVm.class));
      // the machine cloning function
      bind(new TypeLiteral<Function<NodeSpec, NodeAndInitialCredentials<IMachine>>>() {
      }).to(Class.class.cast(NodeCreator.class));
      bind(new TypeLiteral<Function<CloneSpec, IMachine>>() {
      }).to(Class.class.cast(CloneAndRegisterMachineFromIMachineIfNotAlreadyExists.class));
      // the jetty server provider
      bind(new TypeLiteral<Server>() {
      }).to(Class.class.cast(PreseedCfgServer.class)).asEagerSingleton();

      bind(new TypeLiteral<Function<IMachine, SshClient>>() {
      }).to(IMachineToSshClient.class);

      bind(ExecutionType.class).toInstance(ExecutionType.HEADLESS);
      bind(LockType.class).toInstance(LockType.Write);
   }

   @Provides
   @Singleton
   protected Function<Supplier<NodeMetadata>, VirtualBoxManager> provideVBox() {
      return new Function<Supplier<NodeMetadata>, VirtualBoxManager>() {

         @Override
         public VirtualBoxManager apply(Supplier<NodeMetadata> nodeSupplier) {
            if(nodeSupplier.get().getId() != null)
            	return VirtualBoxManager.createInstance(nodeSupplier.get().getId());

           	return VirtualBoxManager.createInstance("");
         }

         @Override
         public String toString() {
            return "createInstanceByNodeId()";
         }

      };
   }

   @Provides
   @Singleton
   protected Predicate<SshClient> sshResponds(SshResponds sshResponds, Timeouts timeouts) {
      return retry(sshResponds, timeouts.nodeRunning, 500l, MILLISECONDS);
   }

   @Override
   protected Optional<ImageExtension> provideImageExtension(Injector i) {
      return Optional.of(i.getInstance(ImageExtension.class));
   }

   @VisibleForTesting
   public static final Map<MachineState, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
            .<MachineState, NodeMetadata.Status> builder().put(MachineState.Running, NodeMetadata.Status.RUNNING)
            .put(MachineState.PoweredOff, NodeMetadata.Status.SUSPENDED)
            .put(MachineState.DeletingSnapshot, NodeMetadata.Status.PENDING)
            .put(MachineState.DeletingSnapshotOnline, NodeMetadata.Status.PENDING)
            .put(MachineState.DeletingSnapshotPaused, NodeMetadata.Status.PENDING)
            .put(MachineState.FaultTolerantSyncing, NodeMetadata.Status.PENDING)
            .put(MachineState.LiveSnapshotting, NodeMetadata.Status.PENDING)
            .put(MachineState.SettingUp, NodeMetadata.Status.PENDING)
            .put(MachineState.Starting, NodeMetadata.Status.PENDING)
            .put(MachineState.Stopping, NodeMetadata.Status.PENDING)
            .put(MachineState.Restoring, NodeMetadata.Status.PENDING)
            // TODO What to map these states to?
            .put(MachineState.FirstOnline, NodeMetadata.Status.PENDING)
            .put(MachineState.FirstTransient, NodeMetadata.Status.PENDING)
            .put(MachineState.LastOnline, NodeMetadata.Status.PENDING)
            .put(MachineState.LastTransient, NodeMetadata.Status.PENDING)
            .put(MachineState.Teleported, NodeMetadata.Status.PENDING)
            .put(MachineState.TeleportingIn, NodeMetadata.Status.PENDING)
            .put(MachineState.TeleportingPausedVM, NodeMetadata.Status.PENDING)
            .put(MachineState.Aborted, NodeMetadata.Status.ERROR)
            .put(MachineState.Stuck, NodeMetadata.Status.ERROR)
            .put(MachineState.Null, NodeMetadata.Status.TERMINATED).build();
   
   @Singleton
   @Provides
   protected Map<MachineState, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }
   
   @VisibleForTesting
   public static final Map<MachineState, Image.Status> toPortableImageStatus = ImmutableMap
            .<MachineState, Image.Status> builder().put(MachineState.Running, Image.Status.PENDING)
            .put(MachineState.PoweredOff, Image.Status.AVAILABLE)
            .put(MachineState.DeletingSnapshot, Image.Status.PENDING)
            .put(MachineState.DeletingSnapshotOnline, Image.Status.PENDING)
            .put(MachineState.DeletingSnapshotPaused, Image.Status.PENDING)
            .put(MachineState.FaultTolerantSyncing, Image.Status.PENDING)
            .put(MachineState.LiveSnapshotting, Image.Status.PENDING)
            .put(MachineState.SettingUp, Image.Status.PENDING)
            .put(MachineState.Starting, Image.Status.PENDING)
            .put(MachineState.Stopping, Image.Status.PENDING)
            .put(MachineState.Restoring, Image.Status.PENDING)
            // TODO What to map these states to?
            .put(MachineState.FirstOnline, Image.Status.PENDING)
            .put(MachineState.FirstTransient, Image.Status.PENDING)
            .put(MachineState.LastOnline, Image.Status.PENDING)
            .put(MachineState.LastTransient, Image.Status.PENDING)
            .put(MachineState.Teleported, Image.Status.PENDING)
            .put(MachineState.TeleportingIn, Image.Status.PENDING)
            .put(MachineState.TeleportingPausedVM, Image.Status.PENDING)
            .put(MachineState.Aborted, Image.Status.ERROR)
            .put(MachineState.Stuck, Image.Status.ERROR)
            .put(MachineState.Null, Image.Status.DELETED).build();
   
   @Singleton
   @Provides
   protected Map<MachineState, Image.Status> toPortableImageStatus() {
      return toPortableImageStatus;
   }

}
