/*
 * *
 *  * Licensed to jclouds, Inc. (jclouds) under one or more
 *  * contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  jclouds licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.jclouds.virtualbox.config;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.OnlyLocationOrFirstZone;
import org.jclouds.virtualbox.compute.VirtualBoxComputeServiceAdapter;
import org.jclouds.virtualbox.functions.IMachineToHardware;
import org.jclouds.virtualbox.functions.IMachineToImage;
import org.jclouds.virtualbox.functions.IMachineToNodeMetadata;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * @author Mattias Holmqvist, Andrea Turli
 */
public class VirtualBoxComputeServiceContextModule extends ComputeServiceAdapterContextModule<VirtualBoxManager, VirtualBoxManager, IMachine, IMachine, Image, Location> {

   public VirtualBoxComputeServiceContextModule() {
      super(VirtualBoxManager.class, VirtualBoxManager.class);
   }
   
   @Provides
   @Singleton
   protected VirtualBoxManager createInstance(@Provider URI endpoint, @Named(Constants.PROPERTY_IDENTITY) String identity,
           @Named(Constants.PROPERTY_CREDENTIAL) String credential) {
	   
	   VirtualBoxManager manager = VirtualBoxManager.createInstance("");
       manager.connect(endpoint.toASCIIString(), identity, credential);
		return manager;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<IMachine, IMachine, Image, Location>>() {
      }).to(VirtualBoxComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<IMachine, NodeMetadata>>() {
      }).to(IMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to((Class) IdentityFunction.class);
      bind(new TypeLiteral<Function<IMachine, Hardware>>() {
      }).to(IMachineToHardware.class);
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to((Class) IdentityFunction.class);
      /*
      bind(new TypeLiteral<Function<IMachine, Image>>() {
      }).to(IMachineToImage.class);
      */
      bind(new TypeLiteral<Supplier<Location>>() {
      }).to(OnlyLocationOrFirstZone.class);
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(OsFamily.UBUNTU).os64Bit(false).osVersionMatches("11.04-server");
   }

   @VisibleForTesting
   public static final Map<MachineState, NodeState> machineToNodeState = ImmutableMap
           .<MachineState, NodeState>builder()
           .put(MachineState.Running, NodeState.RUNNING)
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
           .put(MachineState.FirstOnline, NodeState.PENDING)
           .put(MachineState.FirstTransient, NodeState.PENDING)
           .put(MachineState.LastOnline, NodeState.PENDING)
           .put(MachineState.LastTransient, NodeState.PENDING)
           .put(MachineState.Teleported, NodeState.PENDING)
           .put(MachineState.TeleportingIn, NodeState.PENDING)
           .put(MachineState.TeleportingPausedVM, NodeState.PENDING)


           .put(MachineState.Aborted, NodeState.ERROR)
           .put(MachineState.Stuck, NodeState.ERROR)

           .put(MachineState.Null, NodeState.UNRECOGNIZED).build();

}
