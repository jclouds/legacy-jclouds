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

package org.jclouds.virtualbox.compute.extensions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_DEFAULT_DIR;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_WORKINGDIR;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.functions.IMachineToVmSpec;
import org.jclouds.virtualbox.functions.TakeSnapshotIfNotAlreadyAttached;
import org.jclouds.virtualbox.functions.admin.UnregisterMachineIfExistsAndDeleteItsMedia;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.CloneMode;
import org.virtualbox_4_1.CloneOptions;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.IProgress;
import org.virtualbox_4_1.ISnapshot;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Virtualbox implementation of {@link ImageExtension}
 * 
 * @author David Alves
 * 
 */
@Singleton
public class VirtualBoxImageExtension implements ImageExtension {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;
   private ComputeServiceAdapter<IMachine, Hardware, Image, Location> vboxAdapter;
   private Function<IMachine, NodeMetadata> machineToNode;
   private Supplier<VirtualBoxManager> manager;
   private String workingDir;
   private boolean isLinkedClone = true;
   private Function<IMachine, Image> imachineToImage;
   private MachineUtils machineUtils;

   @Inject
   public VirtualBoxImageExtension(ComputeServiceAdapter<IMachine, Hardware, Image, Location> vboxAdapter,
            Function<IMachine, NodeMetadata> machineToNode, Supplier<VirtualBoxManager> manager,
            @Named(VIRTUALBOX_WORKINGDIR) String workingDir, Function<IMachine, Image> imachineToImage,
            MachineUtils machineUtils) {
      this.vboxAdapter = vboxAdapter;
      this.machineToNode = machineToNode;
      this.manager = manager;
      this.workingDir = workingDir == null ? VIRTUALBOX_DEFAULT_DIR : workingDir;
      this.imachineToImage = imachineToImage;
      this.machineUtils = machineUtils;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, final String id) {
      Optional<NodeMetadata> sourceNode = getNodeById(id);
      checkState(sourceNode.isPresent(), " there is no node with id " + id);
      String vmName = VIRTUALBOX_IMAGE_PREFIX + name;

      IMachine vm = null;
      try {
         vm = manager.get().getVBox().findMachine(vmName);
      } catch (Exception e) {
      }
      checkState(vm == null, " a machine exists with name: " + vmName);
      return new ImageTemplateBuilder.CloneImageTemplateBuilder().name(vmName).nodeId(id).build();
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate, " vbox image extension only supports cloning for the moment.");
      CloneImageTemplate cloneTemplate = CloneImageTemplate.class.cast(template);

      IMachine source = manager.get().getVBox().findMachine(cloneTemplate.getSourceNodeId());

      String settingsFile = manager.get().getVBox().composeMachineFilename(template.getName(), workingDir);
      IMachine clonedMachine = manager.get().getVBox()
               .createMachine(settingsFile, template.getName(), source.getOSTypeId(), template.getName(), true);

      List<CloneOptions> options = Lists.newArrayList();
      if (isLinkedClone)
         options.add(CloneOptions.Link);

      // TODO snapshot name
      ISnapshot currentSnapshot = new TakeSnapshotIfNotAlreadyAttached(manager, "pre-image-spawn", "before spawning "
               + template.getName(), logger).apply(source);

      checkNotNull(currentSnapshot);

      // clone
      IProgress progress = currentSnapshot.getMachine().cloneTo(clonedMachine, CloneMode.MachineState, options);
      progress.waitForCompletion(-1);

      logger.debug(String.format("Machine %s is cloned correctly", clonedMachine.getName()));

      // registering
      manager.get().getVBox().registerMachine(clonedMachine);

      return Futures.immediateFuture(imachineToImage.apply(clonedMachine));
   }

   @Override
   public boolean deleteImage(String id) {
      try {
         IMachine machine = manager.get().getVBox().findMachine(VIRTUALBOX_IMAGE_PREFIX + id);
         machineUtils.applyForMachine(machine.getId(), new UnregisterMachineIfExistsAndDeleteItsMedia(
                  new IMachineToVmSpec().apply(machine)));
      } catch (Exception e) {
         logger.error(e, "Could not delete machine with id %s ", id);
         return false;
      }
      return true;
   }

   private Optional<NodeMetadata> getNodeById(final String id) {
      return Iterables.tryFind(Iterables.transform(vboxAdapter.listNodes(), machineToNode),
               new Predicate<NodeMetadata>() {
                  @Override
                  public boolean apply(NodeMetadata input) {
                     return input.getId().equals(id);
                  }
               });
   }

}
