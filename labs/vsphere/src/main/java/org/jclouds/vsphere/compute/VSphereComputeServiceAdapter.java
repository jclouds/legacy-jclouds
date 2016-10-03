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
 * 
 * @author Andrea Turli
 */
package org.jclouds.vsphere.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.vsphere.utils.ManagedEntities.fetchDatastores;
import static org.jclouds.vsphere.utils.ManagedEntities.getDatastoreByPolicy;
import static org.jclouds.vsphere.utils.ManagedEntities.listManagedEntities;
import static org.jclouds.vsphere.utils.ManagedEntities.tryFindManagedEntity;
import static org.jclouds.vsphere.utils.ManagedEntities.tryFindVmByName;

import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.vsphere.functions.MasterToVirtualMachineCloneSpec;
import org.jclouds.vsphere.functions.VirtualMachineToImage;
import org.jclouds.vsphere.utils.VirtualMachines;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.InvalidDatastore;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VimFault;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VmConfigFault;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

@Singleton
public class VSphereComputeServiceAdapter implements
         ComputeServiceAdapter<VirtualMachine, Hardware, Image, Datacenter> {
   
   private final ReentrantLock lock = new ReentrantLock();

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private Supplier<ServiceInstance> serviceInstance;
   private final VirtualMachineToImage virtualMachineToImage;
   private final GroupNamingConvention.Factory namingConvention;
   
   @Inject
   public VSphereComputeServiceAdapter(Supplier<ServiceInstance> serviceInstance, 
           VirtualMachineToImage virtualMachineToImage,
           GroupNamingConvention.Factory namingConvention) {
      this.serviceInstance = checkNotNull(serviceInstance, "serviceInstance");
      this.virtualMachineToImage = virtualMachineToImage;
      this.namingConvention = namingConvention;
   }

   @Override
   /**
    * This will create nodes that are clones of a template.
    * A template is a master copy of a virtual machine that can be used to create and provision virtual machines. 
    * There is not a single API to clone a virtual machine or deploy a virtual machine from a template,   
    * so first you need to mark the template as vm, clone the vm and re-mark the vm as template
    */
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(String tag, String name,
            Template template) {
      VirtualMachine master = getMaster(template.getImage().getName());
      Folder rootFolder = serviceInstance.get().getRootFolder();
      Optional<ResourcePool> optionalResourcePool = tryFindManagedEntity(rootFolder, ResourcePool.class);
      checkState(optionalResourcePool.isPresent(), " there is not an available host on this rootFolder " + rootFolder);
      ResourcePool resourcePool = optionalResourcePool.get();

      Optional<HostSystem> optionalHost = tryFindManagedEntity(rootFolder, HostSystem.class);
      checkState(optionalHost.isPresent(), " there is not an available host on this rootFolder " + rootFolder);
      HostSystem host = optionalHost.get();

      Set<Datastore> availableDatastores = fetchDatastores(rootFolder);
      
      Ordering<Datastore> byCapacityOrdering = new Ordering<Datastore>() {
         public int compare(Datastore left, Datastore right) {
            return Longs.compare(left.getSummary().getCapacity(), right.getSummary().getCapacity());
         }
      };
      Datastore datastore = checkNotNull(getDatastoreByPolicy(availableDatastores, byCapacityOrdering), "datastore");
      
      try {
         markTemplateAsVirtualMachine(master, resourcePool, host);
      } catch (Exception e) {
         logger.error(String.format("Can't mark template %s as vm", master.getName(), e));
         throw propagate(e);
      }
      
      String zoneId = template.getLocation().getId();
      String imageId = template.getImage().getProviderId();
      String flavorId = template.getHardware().getProviderId();
      logger.debug(">> cloning new vm zone(%s) name(%s) image(%s) flavor(%s) options(%s)", zoneId, name, imageId, flavorId, null);
      String cloningStrategy = "linked";
      VirtualMachineCloneSpec cloneSpec = 
              new MasterToVirtualMachineCloneSpec(resourcePool, datastore, cloningStrategy ).apply(master); 
      VirtualMachine cloned = cloneMaster(master, tag, name, cloneSpec);
      
      try {
         markVirtualMachineAsTemplate(master);
      } catch (Exception e) {
         logger.error(String.format("Can't mark vm %s as template", master.getName(), e));
         throw propagate(e);
      }
      
      NodeAndInitialCredentials<VirtualMachine> nodeAndInitialCredentials = 
            new NodeAndInitialCredentials<VirtualMachine>(cloned, cloned.getName(), 
            LoginCredentials.builder().user(template.getOptions().getLoginUser())
               .password(template.getOptions().getLoginPassword()).authenticateSudo(true).build());
      return nodeAndInitialCredentials;
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      Folder folder = serviceInstance.get().getRootFolder();
      return listManagedEntities(folder, VirtualMachine.class);
      }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      Set<org.jclouds.compute.domain.Hardware> hardware = Sets.newLinkedHashSet();
      hardware.add(new HardwareBuilder().ids("t1.micro").hypervisor("vSphere").name("t1.micro")
               .processor(new Processor(1, 1.0)).ram(512).build());
      hardware.add(new HardwareBuilder().ids("m1.small").hypervisor("vSphere").name("m1.small")
               .processor(new Processor(1, 1.0)).ram(1024).build());
      hardware.add(new HardwareBuilder().ids("m1.medium").hypervisor("vSphere").name("m1.medium")
               .processor(new Processor(1, 1.0)).ram(3840).build());
      hardware.add(new HardwareBuilder().ids("m1.large").hypervisor("vSphere").name("m1.large")
               .processor(new Processor(1, 1.0)).ram(7680).build());
      return hardware;
   }

   @Override
   public Iterable<Image> listImages() {
      return FluentIterable.from(listNodes()).filter(VirtualMachines.getTemplatePredicate()).transform(virtualMachineToImage);
   }

   @Override
   public Iterable<Datacenter> listLocations() {
      Folder rootFolder = serviceInstance.get().getRootFolder();
      return listManagedEntities(rootFolder, Datacenter.class);
   }

   @Override
   public VirtualMachine getNode(String vmName) {
      Folder rootFolder = serviceInstance.get().getRootFolder();
      return tryFindVmByName(rootFolder, vmName).orNull();
   }

   @Override
   public void destroyNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName);
      if(virtualMachine == null) {
         return;
      }
      try {
         Task powerOffTask = virtualMachine.powerOffVM_Task();
         if (awaitComplete(powerOffTask)) {
            String msg = String.format("VM %s powered off", vmName);
            logger.debug(msg);
        }
      } catch (RuntimeFault e) {
         logger.error(e.getMessage());
         throw propagate(e);
      } catch (RemoteException e) {
         logger.error(e.getMessage());
         throw propagate(e);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         logger.error(e.getMessage());
         throw propagate(e);
      }

      try {
         Task destroyTask = virtualMachine.destroy_Task();
         if (awaitComplete(destroyTask))
            logger.debug(String.format("VM %s destroyed", vmName));
      } catch (VimFault e) {
         logger.error(e.getMessage());
         throw propagate(e);
      } catch (RuntimeFault e) {
         logger.error(e.getMessage());
         throw propagate(e);
      } catch (RemoteException e) {
         logger.error(e.getMessage());
         throw propagate(e);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         logger.error(e.getMessage());
         throw propagate(e);
      }
   }

   @Override
   public void rebootNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName); 
      try {
         virtualMachine.rebootGuest();
      } catch (Exception e) {
         logger.error("Can't reboot vm " + vmName, e);
         throw propagate(e);
      }
      logger.debug(vmName + " rebooted");
   }

   @Override
   public void resumeNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName);
      try {
         Task task = virtualMachine.powerOnVM_Task(null);
         if(awaitComplete(task))
            logger.debug(virtualMachine.getName() + " resumed");
      } catch (Exception e) {
         logger.error("Can't resume vm " + vmName, e);
         throw propagate(e);
      }
   }

   @Override
   public void suspendNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName);
      try {
         Task task = virtualMachine.suspendVM_Task();
         if(awaitComplete(task))
            logger.debug(vmName + " suspended");
      } catch (Exception e) {
         logger.error("Can't suspend vm " + vmName, e);
         throw propagate(e);
      }
   }

   @Override
   public Image getImage(String imageName) {
      VirtualMachine node = getNode(imageName);
      checkState(VirtualMachines.isTemplate(node), "cannot find an image called " + imageName);
      return virtualMachineToImage.apply(node);
   }

   private VirtualMachine cloneMaster(VirtualMachine master, String tag, String name, VirtualMachineCloneSpec cloneSpec) {
      try {
         String clonedName = namingConvention.create().uniqueNameForGroup(tag);
         Task task = master.cloneVM_Task((Folder) master.getParent(), clonedName, cloneSpec);
         if (awaitComplete(task)) {
            return (VirtualMachine) new InventoryNavigator((Folder) master.getParent()).searchManagedEntity(
                  "VirtualMachine", clonedName);
         } else {
            String msg = String.format("Can't clone %s from %s", clonedName, master.getName());
            throw new RuntimeException(msg);
         }
      } catch (Exception e) {
         logger.error("Can't clone vm " + master.getName(), e);
         throw propagate(e);
      }
   }

   private VirtualMachine getMaster(String templateName) {
      Folder rootFolder = serviceInstance.get().getRootFolder();
      Optional<VirtualMachine> optionalVm = tryFindVmByName(rootFolder, templateName);
      checkState(optionalVm.isPresent(), 
            String.format("there is not an available vm '%s' on rootFolder '%s'", templateName, rootFolder));
      checkState(VirtualMachines.isTemplate(optionalVm.get()), 
            String.format("VM %s is available on rootFolder '%s', but it is not a template (a master copy of a virtual machine)", optionalVm.get().getName(), rootFolder));
      return optionalVm.get();
   }
   
   private void markVirtualMachineAsTemplate(VirtualMachine vm) throws VmConfigFault, InvalidState, RuntimeFault,
            RemoteException {
      // during createNodeWithGroup, a thread can change from vm -> template and another thread can find a wrong status
      // the vm
      lock.lock();
      try {
         if (!VirtualMachines.isTemplate(vm))
            vm.markAsTemplate();
      } finally {
         lock.unlock();
      }
   }

   private void markTemplateAsVirtualMachine(VirtualMachine master, ResourcePool resourcePool, HostSystem host)
            throws VmConfigFault, FileFault, InvalidState, InvalidDatastore, RuntimeFault, RemoteException,
            InvalidName, SnapshotFault, TaskInProgress, InterruptedException {
      // during createNodeWithGroup, a thread can change from vm -> template and another thread can find a wrong status
      // the vm
      lock.lock();
      try {
         if (master.getConfig().isTemplate())
            master.markAsVirtualMachine(resourcePool, host);
      } finally {
         lock.unlock();
      }
   }
   
   private boolean awaitComplete(Task task)
         throws RuntimeFault, RemoteException, InterruptedException, InvalidProperty {
      if (!task.waitForTask().equals(Task.SUCCESS)) {
         String message = task.getTaskInfo().getError().getLocalizedMessage();
         logErrorAndthrowRuntimeException(message);
      }
      return true;
   }
   
   private void logErrorAndthrowRuntimeException(String message) {
      logger.debug(message);
      throw new RuntimeException(message);
   }
}