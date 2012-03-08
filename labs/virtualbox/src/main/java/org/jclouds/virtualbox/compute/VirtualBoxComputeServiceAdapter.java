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

package org.jclouds.virtualbox.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.util.MachineController;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Singleton;

/**
 * Defines the connection between the {@link org.virtualbox_4_1.VirtualBoxManager} implementation
 * and the jclouds {@link org.jclouds.compute.ComputeService}
 * 
 * @author Mattias Holmqvist, Andrea Turli
 */
@Singleton
public class VirtualBoxComputeServiceAdapter implements ComputeServiceAdapter<IMachine, IMachine, Image, Location> {

   private final Supplier<VirtualBoxManager> manager;
   private final Map<Image, YamlImage> images;
   private final LoadingCache<Image, Master> mastersLoader;
   private final Function<NodeSpec, NodeAndInitialCredentials<IMachine>> cloneCreator;
   private final MachineController machineController;

   @Inject
   public VirtualBoxComputeServiceAdapter(Supplier<VirtualBoxManager> manager,
            Supplier<Map<Image, YamlImage>> imagesMapper, LoadingCache<Image, Master> mastersLoader,
            Function<NodeSpec, NodeAndInitialCredentials<IMachine>> cloneCreator, MachineController machineController) {
      this.manager = checkNotNull(manager, "manager");
      this.images = imagesMapper.get();
      this.mastersLoader = mastersLoader;
      this.cloneCreator = cloneCreator;
      this.machineController = machineController;
   }

   @Override
   public NodeAndInitialCredentials<IMachine> createNodeWithGroupEncodedIntoName(String tag, String name,
            Template template) {
      try {
         Master master = mastersLoader.get(template.getImage());
         NodeSpec nodeSpec = NodeSpec.builder().master(master).name(name).tag(tag).template(template).build();
         return cloneCreator.apply(nodeSpec);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Iterable<IMachine> listNodes() {
      return Iterables.filter(manager.get().getVBox().getMachines(), new Predicate<IMachine>() {
         @Override
         public boolean apply(IMachine arg0) {
            return !arg0.getName().startsWith(VIRTUALBOX_NODE_PREFIX);
         }
      });
   }

   @Override
   public Iterable<IMachine> listHardwareProfiles() {
      return imageMachines();
   }

   @Override
   public Iterable<Image> listImages() {
      return images.keySet();
   }

   private Iterable<IMachine> imageMachines() {
      final Predicate<? super IMachine> imagePredicate = new Predicate<IMachine>() {
         @Override
         public boolean apply(@Nullable IMachine iMachine) {
            return iMachine.getName().startsWith(VIRTUALBOX_IMAGE_PREFIX);
         }
      };
      final Iterable<IMachine> imageMachines = filter(manager.get().getVBox().getMachines(), imagePredicate);
      return imageMachines;
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location> of();
   }

   @Override
   public IMachine getNode(String vmName) {
      return manager.get().getVBox().findMachine(vmName);
   }

   @Override
   public void destroyNode(String vmName) {
      machineController.ensureMachineIsPoweredOff(vmName);
      // TODO
      //machineUtils.unlockMachineAndApplyOrReturnNullIfNotRegistered(vmName,
      //      new UnregisterMachineIfExistsAndDeleteItsMedia(vmSpecification));   
      }

   @Override
   public void rebootNode(String vmName) {
      machineController.ensureMachineIsRunning(vmName);
   }

   @Override
   public void resumeNode(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      ISession machineSession;
      try {
         machineSession = manager.get().openMachineSession(machine);
         machineSession.getConsole().resume();
         machineSession.unlockMachine();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public void suspendNode(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      ISession machineSession;
      try {
         machineSession = manager.get().openMachineSession(machine);
         machineSession.getConsole().pause();
         machineSession.unlockMachine();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }


}
