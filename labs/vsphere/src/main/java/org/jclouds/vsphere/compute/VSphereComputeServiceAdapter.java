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
import static org.jclouds.vsphere.config.VSphereConstants.CLONING;
import static org.jclouds.vsphere.config.VSphereConstants.VSPHERE_PREFIX;
import static org.jclouds.vsphere.config.VSphereConstants.VSPHERE_SEPARATOR;

import java.rmi.RemoteException;
import java.util.Arrays;
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
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;
import org.jclouds.vsphere.VSphereApiMetadata;
import org.jclouds.vsphere.functions.MasterToVirtualMachineCloneSpec;
import org.jclouds.vsphere.functions.VirtualMachineToImage;
import org.jclouds.vsphere.predicates.IsTemplatePredicate;
import org.jclouds.vsphere.utils.VirtualMachines;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

@Singleton
public class VSphereComputeServiceAdapter implements
         ComputeServiceAdapter<VirtualMachine, Hardware, Image, Location> {
   
   private final ReentrantLock lock = new ReentrantLock();

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private Supplier<ServiceInstance> serviceInstance;
   private final VirtualMachineToImage virtualMachineToImage;
   private final Folder rootFolder;
   
   @Inject
   public VSphereComputeServiceAdapter(Supplier<ServiceInstance> serviceInstance, VirtualMachineToImage virtualMachineToImage) {
      this.serviceInstance = checkNotNull(serviceInstance, "serviceInstance");
      this.virtualMachineToImage = virtualMachineToImage;
      this.rootFolder = serviceInstance.get().getRootFolder();
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(String tag, String name,
            Template template) {
      VirtualMachine master = getMaster(template.getImage().getName());
      
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
      logger.debug(">> cloning new vm zone(%s name(%s) image(%s) flavor(%s) options(%s)", zoneId, name, imageId, flavorId, null);
      VirtualMachineCloneSpec cloneSpec = new MasterToVirtualMachineCloneSpec(resourcePool, datastore,  
               VSphereApiMetadata.defaultProperties().getProperty(CLONING)).apply(master);
      
      VirtualMachine cloned = null;
      cloned = cloneMaster(master, tag, name, cloneSpec);
      
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

   private VirtualMachine getMaster(String templateName) {
      Optional<VirtualMachine> optionalVm = tryFindVmByName(rootFolder, templateName);
      checkState(optionalVm.isPresent(), 
            String.format("there is not an available vm '%s' on rootFolder '%s'", templateName, rootFolder));
      checkState(VirtualMachines.isTemplate(optionalVm.get()), 
            String.format("there is not an available vm '%s' on rootFolder '%s'", templateName, rootFolder));
      return optionalVm.get();
   }

   private Datastore getDatastoreByPolicy(Set<Datastore> availableDatastores, Ordering<Datastore> policy) {
      Datastore datastore = policy.max(availableDatastores);
      return datastore;
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
      return FluentIterable.from(listNodes()).filter(new IsTemplatePredicate()).transform(virtualMachineToImage);
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location> of();
   }

   @Override
   public VirtualMachine getNode(String vmName) {
      Optional<VirtualMachine> optionalVm = tryFindVmByName(rootFolder, vmName);
      checkState(optionalVm.isPresent(), 
            String.format("there is not an available vm %s on this rootFolder %s", vmName, rootFolder));
      return optionalVm.get();
   }

   @Override
   public void destroyNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName);
      try {
         Task powerOffTask = virtualMachine.powerOffVM_Task();
         if (manageTask(powerOffTask))
            logger.debug(String.format("VM %s powered off", vmName));
      } catch (RuntimeFault e) {
         throw propagate(e);
      } catch (RemoteException e) {
         throw propagate(e);
      } catch (InterruptedException e) {
         throw propagate(e);
      }

      try {
         Task destroyTask = virtualMachine.destroy_Task();
         if (manageTask(destroyTask))
            logger.debug(String.format("VM %s destroyed", vmName));
      } catch (VimFault e) {
         throw propagate(e);
      } catch (RuntimeFault e) {
         throw propagate(e);
      } catch (RemoteException e) {
         throw propagate(e);
      } catch (InterruptedException e) {
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
         if(manageTask(task))
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
         if(manageTask(task))
            logger.debug(vmName + " suspended");
      } catch (Exception e) {
         logger.error("Can't suspend vm " + vmName, e);
         throw propagate(e);
      }
   }

   private void logErrorAndthrowRuntimeException(String message) {
      logger.debug(message);
      throw new RuntimeException(message);
   }

   @Override
   public Image getImage(String imageName) {
      VirtualMachine node = getNode(imageName);
      checkState(new IsTemplatePredicate().apply(node), "cannot find an image called " + imageName);
      return virtualMachineToImage.apply(node);
   }

   private VirtualMachine cloneMaster(VirtualMachine master, String tag, String name, VirtualMachineCloneSpec cloneSpec) {
      VirtualMachine cloned = null;
      try {
         String clonedName = createName(tag, name);
         Task task = master.cloneVM_Task((Folder) master.getParent(), clonedName, cloneSpec);
         if(manageTask(task))
            cloned = (VirtualMachine) new InventoryNavigator((Folder) master.getParent()).searchManagedEntity("VirtualMachine", clonedName);
      } catch (Exception e) {
         throw propagate(e);
      } 
      return checkNotNull(cloned, "cloned");
   }

   private String createName(String tag, String name) {
      return VSPHERE_PREFIX + tag + VSPHERE_SEPARATOR + name;
   }
   
   private Set<Datastore> fetchDatastores(Folder folder) {
      try {
         ManagedEntity[] datacenterEntities = new InventoryNavigator(folder).searchManagedEntities("Datacenter");
         FluentIterable<Datacenter> datacenters = FluentIterable.from(Arrays.asList(datacenterEntities)).transform(
               new Function<ManagedEntity, Datacenter>() {
                  public Datacenter apply(ManagedEntity input) {
                     return (Datacenter) input;
                  }
               });
          
         Set<Datastore> datastores = Sets.newLinkedHashSet();
         for (Datacenter datacenter : datacenters) {
            Iterables.addAll(datastores, Arrays.asList(datacenter.getDatastores()));
         }
         return datastores;
      } catch (Exception e) {
         logger.error("Problem in finding a datastore", e);
         throw propagate(e);
      }
   }

   private Optional<VirtualMachine> tryFindVmByName(Folder folder, final String vmName) {
      return FluentIterable.from(listManagedEntities(folder, VirtualMachine.class)).filter(new Predicate<VirtualMachine>() {

         @Override
         public boolean apply(VirtualMachine input) {
            return input.getName().equals(vmName);
         }}).first();
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

   private <T> Optional<T> tryFindManagedEntity(Folder folder, final Class<T> managedEntityClass) {
      Iterable<T> managedEntities = listManagedEntities(folder, managedEntityClass);
      return Iterables.tryFind(managedEntities, Predicates.notNull());
   }

   private <T> Iterable<T> listManagedEntities(Folder folder, final Class<T> managedEntityClass) {
      Iterable<T> managedEntities = ImmutableSet.<T> of();
      String managedEntityName = managedEntityClass.getSimpleName();
         try {
            managedEntities =  
                  Iterables.transform(
                        Arrays.asList(new InventoryNavigator(folder).searchManagedEntities(managedEntityName)), 
                        new Function<ManagedEntity, T>() {
                           public T apply(ManagedEntity input) {
                              return managedEntityClass.cast(input);
                           }
                        });
         } catch (InvalidProperty e) {
            logger.error(String.format("Problem in finding a valid %s", managedEntityName), e);
            throw propagate(e);
         } catch (RuntimeFault e) {
            logger.error(String.format("Problem in finding a valid %s", managedEntityName), e);
            throw propagate(e);
         } catch (RemoteException e) {
            logger.error(String.format("Problem in finding a valid %s", managedEntityName), e);
            throw propagate(e);
         }
      return managedEntities;
   }
   
   private boolean manageTask(Task task)
         throws RuntimeFault, RemoteException, InterruptedException, InvalidProperty {
      if (!task.waitForTask().equals(Task.SUCCESS)) {
         String message = task.getTaskInfo().getError().getLocalizedMessage();
         logErrorAndthrowRuntimeException(message);
      }
      return true;
   }
}