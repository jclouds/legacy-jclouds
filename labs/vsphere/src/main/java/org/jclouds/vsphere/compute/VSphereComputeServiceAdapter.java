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

import com.google.common.base.Function;
import com.google.common.base.Optional;
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
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SnapshotFault;
import com.vmware.vim25.TaskInProgress;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachinePowerState;
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
      String templateNameOrId = template.getImage().getId();
      VirtualMachine master = getMaster(templateNameOrId);
      
      
      Optional<ResourcePool> optionalResourcePool = tryFindResourcePool(rootFolder);
      Optional<HostSystem> optionalHost = tryFindHost(rootFolder);
      
      checkState(optionalHost.isPresent(), " there is not an available host on this rootFolder " + rootFolder);
      HostSystem host = optionalHost.get();

      checkState(optionalHost.isPresent(), " there is not an available host on this rootFolder " + rootFolder);
      ResourcePool resourcePool = optionalResourcePool.get();

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
      try {
         cloned = cloneMaster(master, tag, name, cloneSpec);
      } catch (Exception e) {
         logger.error("Can't clone vm " + master.getName(), e);
         throw propagate(e);
      }
      
      try {
         markVirtualMachineAsTemplate(master);
      } catch (Exception e) {
         logger.error(String.format("Can't mark vm %s as template", master.getName(), e));
         throw propagate(e);
      }
      
      NodeAndInitialCredentials<VirtualMachine> nodeAndInitialCredentials = 
            new NodeAndInitialCredentials<VirtualMachine>(cloned, cloned.getName(), 
            LoginCredentials.builder().user("toor").password("password").authenticateSudo(true).build());
      return nodeAndInitialCredentials;
   }

   private VirtualMachine getMaster(String templateNameOrId) {
      Optional<VirtualMachine> optionalVm = tryFindVm(templateNameOrId, rootFolder);
      checkState(optionalVm.isPresent(), 
            String.format("there is not an available vm '%s' on rootFolder '%s'", templateNameOrId, rootFolder));
      checkState(isVMwareTemplate(optionalVm.get()), 
            String.format("there is not an available vm '%s' on rootFolder '%s'", templateNameOrId, rootFolder));
      return optionalVm.get();
   }

   private Datastore getDatastoreByPolicy(Set<Datastore> availableDatastores, Ordering<Datastore> policy) {
      Datastore datastore = policy.max(availableDatastores);
      return datastore;
   }

   @Override
   public FluentIterable<VirtualMachine> listNodes() {
      Folder nodesFolder = serviceInstance.get().getRootFolder();
      ManagedEntity[] managedEntities = null;
      try {
         managedEntities = new InventoryNavigator(nodesFolder).searchManagedEntities("VirtualMachine");
      } catch (Exception e) {
         logger.error("Can't find vm", e);
         throw propagate(e);
      }
      return FluentIterable.from(Arrays.asList(managedEntities)).transform(
            new Function<ManagedEntity, VirtualMachine>() {
               public VirtualMachine apply(ManagedEntity input) {
                  return (VirtualMachine) input;
               }
            });
      }
      
//      Iterable<VirtualMachine> vms = ImmutableSet.<VirtualMachine> of();
//      try {
//         ManagedEntity[] managedEntities =  new InventoryNavigator(nodesFolder).searchManagedEntities("VirtualMachine");
//         vms =  Iterables.transform(Arrays.asList(managedEntities), new Function<ManagedEntity, VirtualMachine>() {
//            public VirtualMachine apply(ManagedEntity input) {
//               return (VirtualMachine) input;
//            }
//         });
//         return vms;
//      } catch (Exception e) {
//         logger.error("Can't find vm", e);
//         propagate(e);
//      }     
//      return vms;
//   }   

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
//      Iterable<VirtualMachine> nodes = listNodes();
//      Iterable<VirtualMachine> templates = Iterables.filter(nodes, new IsTemplatePredicate());
//      return Iterables.transform(templates, virtualMachineToImage);
      return listNodes().filter(new IsTemplatePredicate()).transform(virtualMachineToImage);
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location> of();
   }

   @Override
   public VirtualMachine getNode(String vmName) {
      Optional<VirtualMachine> optionalVm = tryFindVm(vmName, rootFolder);
      checkState(optionalVm.isPresent(), 
            String.format("there is not an available vm %s on this rootFolder %s", vmName, rootFolder));
      return optionalVm.get();
   }

   @Override
   public void destroyNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName); 
      try {
         Task powerOffTask = virtualMachine.powerOffVM_Task();
         if (powerOffTask.waitForTask().equals(Task.SUCCESS))
            logger.debug(String.format("VM %s powered off", vmName));
         else 
            logger.debug(String.format("VM %s could not be powered off", vmName));
         
         Task destroyTask = virtualMachine.destroy_Task();
         if (destroyTask.waitForTask().equals(Task.SUCCESS))
            logger.debug(String.format("VM %s destroyed", vmName));
         else 
            logger.debug(String.format("VM %s could not be destroyed", vmName));
      } catch (Exception e) {
         logger.error("Can't destroy vm " + vmName, e);
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

      if(virtualMachine.getRuntime().getPowerState().equals(VirtualMachinePowerState.poweredOff)) {
         try {
            Task task = virtualMachine.powerOnVM_Task(null);
            if (task.waitForTask().equals(Task.SUCCESS))
               logger.debug(virtualMachine.getName() + " resumed");
         } catch (Exception e) {
            logger.error("Can't resume vm " + vmName, e);
            throw propagate(e);
         }

      } else
         logger.debug(vmName + " can't be resumed");
   }

   @Override
   public void suspendNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName); 

      try {
         Task task = virtualMachine.suspendVM_Task();
         if (task.waitForTask().equals(Task.SUCCESS))
            logger.debug(vmName + " suspended");
         else
            logger.debug(vmName + " can't be suspended");
      } catch (Exception e) {
         logger.error("Can't suspend vm " + vmName, e);
         throw propagate(e);
      }
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
         String result = task.waitForTask();
         if (result.equals(Task.SUCCESS)) {
            cloned = (VirtualMachine) new InventoryNavigator((Folder) master.getParent()).searchManagedEntity("VirtualMachine", clonedName);
         } else {
            String errorMessage = task.getTaskInfo().getError().getLocalizedMessage();
            logger.error(errorMessage);
         }
      } catch (Exception e) {
         logger.error("Can't clone vm", e);
         throw propagate(e);
      } 
      return checkNotNull(cloned, "cloned");
   }

   private String createName(String tag, String name) {
      String clonedName = VSPHERE_PREFIX + tag + VSPHERE_SEPARATOR + name;
      return clonedName;
   }

   private Optional<HostSystem> tryFindHost(Folder folder) {
      Iterable<HostSystem> hosts = ImmutableSet.<HostSystem> of();
      try {
         ManagedEntity[] hostEntities = new InventoryNavigator(folder).searchManagedEntities("HostSystem");
         hosts =  Iterables.transform(Arrays.asList(hostEntities), new Function<ManagedEntity, HostSystem>() {
            public HostSystem apply(ManagedEntity input) {
               return (HostSystem) input;
            }
         });
      } catch (Exception e) {
         logger.error("Problem in finding a valid host", e);
         throw propagate(e);
      }
      return Iterables.tryFind(hosts, Predicates.notNull());
   }

   private Optional<ResourcePool> tryFindResourcePool(Folder folder) {
      Iterable<ResourcePool> resourcePools = ImmutableSet.<ResourcePool> of();
      try {
         ManagedEntity[] resourcePoolEntities = new InventoryNavigator(folder).searchManagedEntities("ResourcePool");
         resourcePools =  Iterables.transform(Arrays.asList(resourcePoolEntities), new Function<ManagedEntity, ResourcePool>() {
            public ResourcePool apply(ManagedEntity input) {
               return (ResourcePool) input;
            }
         });
      } catch (Exception e) {
         logger.error("Problem in finding a valid resource pool", e);
         throw propagate(e);
      }
      return Iterables.tryFind(resourcePools, Predicates.notNull());
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
            Datastore[] datastoresArray = datacenter.getDatastores();
            datastores.addAll(Arrays.asList(datastoresArray));
         }
         return datastores;
      } catch (Exception e) {
         logger.error("Problem in finding a datastore", e);
         throw propagate(e);
      }
   }

//      try {
//         ManagedEntity[] datacenterEntities = new InventoryNavigator(folder).searchManagedEntities("Datacenter");
//         Iterable<Datacenter> datacenters =  Iterables.transform(Arrays.asList(datacenterEntities), new Function<ManagedEntity, Datacenter>() {
//            public Datacenter apply(ManagedEntity input) {
//               return (Datacenter) input;
//            }
//         });
//         for (Datacenter datacenter : datacenters) {
//            for (Datastore d : datacenter.getDatastores()) {
//               long max = d.getSummary().getCapacity();
//               datastore = d;
//               if(d.getSummary().getCapacity() > max) {
//                  max = d.getSummary().getCapacity();
//                  datastore = d;
//               }
//            }
//         }
//      } catch (Exception e) {
//         logger.error("Problem in finding a datastore", e);
//         throw propagate(e);
//      }
//      checkNotNull(datastore, "datastore");
//      return datastore;
//   }

   private Optional<VirtualMachine> tryFindVm(String vmName, Folder nodesFolder) {
      try {
         return Optional.of(((VirtualMachine) new InventoryNavigator(nodesFolder).searchManagedEntity("VirtualMachine", vmName)));
      } catch (Exception e) {
         logger.error("Can't find vm", e);
         throw propagate(e);
      }
   }
   
   private boolean isVMwareTemplate(VirtualMachine vm) {
      return vm.getConfig().isTemplate();
   }   

   private void markVirtualMachineAsTemplate(VirtualMachine vm) throws VmConfigFault, InvalidState, RuntimeFault,
            RemoteException {
      // during createNodeWithGroup, a thread can change from vm -> template and another thread can find a wrong status
      // the vm
      lock.lock();
      try {
         if (!isVMwareTemplate(vm))
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

}