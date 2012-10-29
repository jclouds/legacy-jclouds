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
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_NAME_SEPARATOR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_NODE_PREFIX;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.Master;
import org.jclouds.virtualbox.domain.NodeSpec;
import org.jclouds.virtualbox.domain.YamlImage;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndForceDeleteItsMedia;
import org.jclouds.virtualbox.util.MachineController;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISession;
import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.VBoxException;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Singleton;

/**
 * Defines the connection between the {@link org.virtualbox_4_1.VirtualBoxManager} implementation
 * and the jclouds {@link org.jclouds.compute.ComputeService}
 * 
 * @author Mattias Holmqvist, Andrea Turli, David Alves
 */
@Singleton
public class VirtualBoxComputeServiceAdapter implements ComputeServiceAdapter<IMachine, Hardware, Image, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final Supplier<VirtualBoxManager> manager;
   private final Map<Image, YamlImage> imagesToYamlImages;
   private final LoadingCache<Image, Master> mastersLoader;
   private final Function<NodeSpec, NodeAndInitialCredentials<IMachine>> cloneCreator;
   private final Function<IMachine, Image> imachineToImage;
   private final MachineController machineController;

   @Inject
   public VirtualBoxComputeServiceAdapter(Supplier<VirtualBoxManager> manager,
            Supplier<Map<Image, YamlImage>> imagesMapper, LoadingCache<Image, Master> mastersLoader,
            Function<NodeSpec, NodeAndInitialCredentials<IMachine>> cloneCreator,
            Function<IMachine, Image> imachineToImage,
            MachineController machineController) {
      this.manager = checkNotNull(manager, "manager");
      this.imagesToYamlImages = imagesMapper.get();
      this.mastersLoader = mastersLoader;
      this.cloneCreator = cloneCreator;
      this.imachineToImage = imachineToImage;
      this.machineController = machineController;
   }

   @Override
   public NodeAndInitialCredentials<IMachine> createNodeWithGroupEncodedIntoName(String tag, String name,
            Template template) {
      try {
         checkState(!tag.contains(VIRTUALBOX_NODE_NAME_SEPARATOR), "tag names cannot contain \""
                  + VIRTUALBOX_NODE_NAME_SEPARATOR + "\"");
         checkState(!name.contains(VIRTUALBOX_NODE_NAME_SEPARATOR), "node names cannot contain \""
                  + VIRTUALBOX_NODE_NAME_SEPARATOR + "\"");
         Master master = mastersLoader.get(template.getImage());
         checkState(master != null, "could not find a master for image: " + template.getImage());
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
   public Iterable<Hardware> listHardwareProfiles() {
      Set<Hardware> hardware = Sets.newLinkedHashSet();
      hardware.add(new HardwareBuilder().ids("t1.micro").hypervisor("VirtualBox").name("t1.micro").ram(512).build());
      hardware.add(new HardwareBuilder().ids("m1.small").hypervisor("VirtualBox").name("m1.small").ram(1024).build());
      hardware.add(new HardwareBuilder().ids("m1.medium").hypervisor("VirtualBox").name("m1.medium").ram(3840).build());
      hardware.add(new HardwareBuilder().ids("m1.large").hypervisor("VirtualBox").name("m1.large").ram(7680).build());
      return hardware;
   }

   @Override
   public Iterable<Image> listImages() {
      // the set of image vm names that were (or could be) built from the yaml file
      final Set<String> imagesFromYamlNames = Sets.newHashSet(Iterables.transform(imagesToYamlImages.keySet(),
               new Function<Image, String>() {
                  @Override
                  public String apply(Image input) {
                     return VIRTUALBOX_IMAGE_PREFIX + input.getId();
                  }

               }));

      // IMachines that were not built from the yaml file transformed to Images
      Set<Image> imagesFromCloning = Sets.newHashSet(Iterables.transform(
               Iterables.filter(imageMachines(), new Predicate<IMachine>() {
                  @Override
                  public boolean apply(IMachine input) {
                     return !imagesFromYamlNames.contains(input.getName());
                  }
               }), imachineToImage));

      // final set of images are those from yaml and those from vbox that were not a transformation
      // of the yaml ones
      return Sets.union(imagesToYamlImages.keySet(), imagesFromCloning);
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
         if (e.getMessage().contains("Could not find a registered machine named")) {
            return null;
         }
         throw Throwables.propagate(e);
      }
   }
   
   @Override
   public Image getImage(String vmName) {
      IMachine image = getNode(vmName);
      if (image == null)
         return null;
      return imachineToImage.apply(image);
   }

   @Override
   public synchronized void destroyNode(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      powerDownMachine(machine);
      try {
         new UnregisterMachineIfExistsAndForceDeleteItsMedia().apply(machine);
      } catch (Exception e) {
         logger.error("Machine (%s) not unregistered!", vmName);
      }
   }

   @Override
   public void rebootNode(String vmName) {
      IMachine machine = manager.get().getVBox().findMachine(vmName);
      powerDownMachine(machine);
      launchVMProcess(machine, manager.get().getSessionObject());
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

   private void launchVMProcess(IMachine machine, ISession session) {
      IProgress prog = machine.launchVMProcess(session, "gui", "");
      prog.waitForCompletion(-1);
      session.unlockMachine();
   }

   private void powerDownMachine(IMachine machine) {
      try {
         if (machine.getState() == MachineState.PoweredOff) {
            logger.debug("vm was already powered down: ", machine.getId());
            return;
         }
         logger.debug("powering down vm: %s", machine.getName());
         machineController.ensureMachineHasPowerDown(machine.getName());
      } catch (Exception e) {
         logger.error(e, "problem in powering down the %s", machine.getName());
         throw Throwables.propagate(e);
      }
   }

}
