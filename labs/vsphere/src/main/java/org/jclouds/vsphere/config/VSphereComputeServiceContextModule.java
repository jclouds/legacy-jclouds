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
package org.jclouds.vsphere.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.ssh.SshClient;
import org.jclouds.vsphere.compute.VSphereComputeServiceAdapter;
import org.jclouds.vsphere.functions.CreateAndConnectVSphereClient;
import org.jclouds.vsphere.functions.VirtualMachineToImage;
import org.jclouds.vsphere.functions.VirtualMachineToNodeMetadata;
import org.jclouds.vsphere.functions.VirtualMachineToSshClient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * 
 * @author Andrea Turli
 */
public class VSphereComputeServiceContextModule extends
         ComputeServiceAdapterContextModule<VirtualMachine, Hardware, Image, Location> {

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, Hardware, Image, Location>>() {
      }).to(VSphereComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<Location, Location>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Image, Image>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<Hardware, Hardware>>() {
      }).to(Class.class.cast(IdentityFunction.class));
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Supplier<ServiceInstance>>() {
      }).to((Class) CreateAndConnectVSphereClient.class);
      
      bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
      }).to(VirtualMachineToNodeMetadata.class);
      bind(new TypeLiteral<Function<VirtualMachine, SshClient>>() {
      }).to(VirtualMachineToSshClient.class);
      bind(new TypeLiteral<Function<VirtualMachine, Image>>() {
      }).to(VirtualMachineToImage.class);
   }
   
   @VisibleForTesting
   public static final Map<VirtualMachinePowerState, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
            .<VirtualMachinePowerState, NodeMetadata.Status> builder()
            .put(VirtualMachinePowerState.poweredOff, NodeMetadata.Status.TERMINATED)
            .put(VirtualMachinePowerState.poweredOn, NodeMetadata.Status.RUNNING)
            .put(VirtualMachinePowerState.suspended, NodeMetadata.Status.SUSPENDED).build();

   @Singleton
   @Provides
   protected Map<VirtualMachinePowerState, NodeMetadata.Status> toPortableNodeStatus() {
      return toPortableNodeStatus;
   }

   @Provides
   @Singleton
   protected Function<Supplier<NodeMetadata>, ServiceInstance> client() {
      return new Function<Supplier<NodeMetadata>, ServiceInstance>() {

         @Override
         public ServiceInstance apply(Supplier<NodeMetadata> nodeSupplier) {
            try {
               return new ServiceInstance(new URL("https://localhost/sdk"), "root", "", true);
            } catch (RemoteException e) {
               Throwables.propagate(e);
               return null;
            } catch (MalformedURLException e) {
               Throwables.propagate(e);
               return null;
            }
         }

         @Override
         public String toString() {
            return "createInstanceByNodeId()";
         }

      };
   }
   
   @VisibleForTesting
   public static final Map<VirtualMachinePowerState, Image.Status> toPortableImageStatus = ImmutableMap
            .<VirtualMachinePowerState, Image.Status> builder().put(VirtualMachinePowerState.poweredOn, Image.Status.PENDING)
            .put(VirtualMachinePowerState.poweredOff, Image.Status.AVAILABLE)
            .put(VirtualMachinePowerState.suspended, Image.Status.PENDING)
            .build();
   
   @Singleton
   @Provides
   protected Map<VirtualMachinePowerState, Image.Status> toPortableImageStatus() {
      return toPortableImageStatus;
   }

}