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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.functions.IMachineToVmSpec;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.jclouds.virtualbox.util.MachineController;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.VBoxException;
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

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final Supplier<VirtualBoxManager> manager;
   private final Map<Image, YamlImage> images;
   private final LoadingCache<Image, Master> mastersLoader;
   private final Function<NodeSpec, NodeAndInitialCredentials<IMachine>> cloneCreator;
   private final MachineController machineController;
   private final MachineUtils machineUtils;

   @Inject
   public VirtualBoxComputeServiceAdapter(Supplier<VirtualBoxManager> manager,
            Supplier<Map<Image, YamlImage>> imagesMapper, LoadingCache<Image, Master> mastersLoader,
            Function<NodeSpec, NodeAndInitialCredentials<IMachine>> cloneCreator, MachineController machineController, 
            MachineUtils machineUtils) {
      this.manager = checkNotNull(manager, "manager");
      this.images = imagesMapper.get();
      this.mastersLoader = mastersLoader;
      this.cloneCreator = cloneCreator;
      this.machineController = machineController;
      this.machineUtils = machineUtils;
   }

   @Override
   public NodeAndInitialCredentials<IMachine> createNodeWithGroupEncodedIntoName(String tag, String name,
            Template template) {
      try {
         Master master = mastersLoader.get(template.getImage());
         checkState(master != null, "could not find a master for image: "+ template.getClass());
         NodeSpec nodeSpec = NodeSpec.builder().master(master).name(name).tag(tag).template(template).build();
         return cloneCreator.apply(nodeSpec);
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public Iterable<IMachine> listNodes() {
      return Iterables.filter(manager.get().getVBox().getMachines(), new Predicate<IMachine>() {
         @Override
         public boolean apply(IMachine arg0) {
            return arg0.getName().startsWith(VIRTUALBOX_NODE_PREFIX);
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
      try {
         return manager.get().getVBox().findMachine(vmName);
      } catch (VBoxException e) {
         if (e.getMessage().contains("Could not find a registered machine named")){
            return null;
         }
         throw Throwables.propagate(e);
      }
   }

   @Override
   public synchronized void destroyNode(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      machineController.ensureMachineHasPowerDown(vmName);
      machineUtils.unlockMachineAndApplyOrReturnNullIfNotRegistered(
				vmName,
				new UnregisterMachineIfExistsAndDeleteItsMedia(new IMachineToVmSpec().apply(machine)));      
   }

   @Override
   public void rebootNode(String vmName) {
      machineController.ensureMachineHasPowerDown(vmName);
      machineController.ensureMachineIsLaunched(vmName);
   }

   @Override
   public void resumeNode(String vmName) {
	   machineController.ensureMachineIsResumed(vmName);
   }

   @Override
   public void suspendNode(String vmName) {
	   machineController.ensureMachineIsPaused(vmName);
   }


}
