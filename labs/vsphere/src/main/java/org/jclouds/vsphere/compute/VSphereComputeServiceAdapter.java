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
import static com.google.common.base.Throwables.propagate;

import static org.jclouds.vsphere.config.VSphereConstants.VSPHERE_SEPARATOR;
import static org.jclouds.vsphere.config.VSphereConstants.VSPHERE_PREFIX;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Set;

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
import org.jclouds.vsphere.functions.VirtualMachineToImage;
import org.jclouds.vsphere.predicates.IsTemplatePredicate;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

@Singleton
public class VSphereComputeServiceAdapter implements
         ComputeServiceAdapter<VirtualMachine, Hardware, Image, Location> {
   
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   
   private final Supplier<ServiceInstance> serviceInstance;
   private final VirtualMachineToImage virtualMachineToImage;
   private Folder rootFolder;
   
   @Inject
   public VSphereComputeServiceAdapter(Supplier<ServiceInstance> serviceInstance, VirtualMachineToImage virtualMachineToImage) {
      this.serviceInstance = checkNotNull(serviceInstance, "serviceInstance");
      this.virtualMachineToImage = virtualMachineToImage;
      this.rootFolder = serviceInstance.get().getRootFolder();
   }

   @Override
   public NodeAndInitialCredentials<VirtualMachine> createNodeWithGroupEncodedIntoName(String tag, String name,
            Template template) {

      VirtualMachine master = getVMwareTemplate(template.getImage().getId(), rootFolder);
      VirtualMachineCloneSpec cloneSpec = configureVirtualMachineCloneSpec();
      VirtualMachine cloned = cloneMaster(master, tag, name, cloneSpec);
      
      NodeAndInitialCredentials<VirtualMachine> nodeAndInitialCredentials = new NodeAndInitialCredentials<VirtualMachine>(cloned, cloned.getName(), LoginCredentials
               .builder().user("toor").password("password").authenticateSudo(true).build());
      return nodeAndInitialCredentials;
   }

   @Override
   public Iterable<VirtualMachine> listNodes() {
      Folder nodesFolder = serviceInstance.get().getRootFolder();
      Iterable<VirtualMachine> vms = ImmutableSet.<VirtualMachine> of();
      try {
         ManagedEntity[] managedEntities =  new InventoryNavigator(nodesFolder).searchManagedEntities("VirtualMachine");
         vms =  Iterables.transform(Arrays.asList(managedEntities), new Function<ManagedEntity, VirtualMachine>() {
            public VirtualMachine apply(ManagedEntity input) {
               return (VirtualMachine) input;
            }
         });
         return vms;
      } catch (Exception e) {
         logger.error("Can't find vm", e);
         propagate(e);
      }     
      return vms;
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
      Iterable<VirtualMachine> nodes = listNodes();
      Iterable<VirtualMachine> templates = Iterables.filter(nodes, new IsTemplatePredicate());
      return Iterables.transform(templates, virtualMachineToImage);
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location> of();
   }

   @Override
   public VirtualMachine getNode(String vmName) {
      return getVM(vmName, rootFolder);
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
         propagate(e);
      } 
  
   }

   @Override
   public void rebootNode(String vmName) {
      VirtualMachine virtualMachine = getNode(vmName); 

      try {
         virtualMachine.rebootGuest();
      } catch (Exception e) {
         logger.error("Can't reboot vm " + vmName, e);
         propagate(e);
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
            propagate(e);
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
         propagate(e);
      }
   }

   @Override
   public Image getImage(String imageName) {
      return virtualMachineToImage.apply(getVMwareTemplate(imageName, rootFolder));
   }

   private VirtualMachine cloneMaster(VirtualMachine master, String tag, String name, VirtualMachineCloneSpec cloneSpec) {
      VirtualMachine cloned = null;
      try {
         String clonedName = VSPHERE_PREFIX + tag + VSPHERE_SEPARATOR + name;
         Task task = master.cloneVM_Task((Folder) master.getParent(), clonedName, cloneSpec);
         String result = task.waitForTask();
         if (result.equals(Task.SUCCESS))
            cloned = (VirtualMachine) new InventoryNavigator((Folder) master.getParent()).searchManagedEntity("VirtualMachine", clonedName);
         else {
            String errorMessage = task.getTaskInfo().getError().getLocalizedMessage();
            logger.error(errorMessage);
         }
      } catch (Exception e) {
         logger.error("Can't clone vm", e);
         propagate(e);
      } 
      return checkNotNull(cloned, "cloned");
   }
   
   private VirtualMachineCloneSpec configureVirtualMachineCloneSpec() {
      Datastore datastore = null;

      try {
         ManagedEntity[] datacenterEntities = new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
         Iterable<Datacenter> datacenters =  Iterables.transform(Arrays.asList(datacenterEntities), new Function<ManagedEntity, Datacenter>() {
            public Datacenter apply(ManagedEntity input) {
               return (Datacenter) input;
            }
         });
         for (Datacenter datacenter : datacenters) {
            for (Datastore d : datacenter.getDatastores()) {
               long max = d.getSummary().getCapacity();
               datastore = d;
               if(d.getSummary().getCapacity() > max) {
                  max = d.getSummary().getCapacity();
                  datastore = d;
               }
            }
         }
      } catch (InvalidProperty e) {
         logger.error("Can't find any datacenter", e);
         propagate(e);
      } catch (RuntimeFault e) {
         logger.error("Can't find any datacenter", e);
         propagate(e);
      } catch (RemoteException e) {
         logger.error("Can't find any datacenter", e);
         propagate(e);
      }
      checkNotNull(datastore, "datastore");
      
      ManagedEntity[] resourcePoolEntities = null;
      try {
         resourcePoolEntities = new InventoryNavigator(rootFolder).searchManagedEntities("ResourcePool");
      } catch (Exception e) {
         logger.error("Can't find any resource pool", e);
         propagate(e);
      }
      checkNotNull(resourcePoolEntities[0], "resourcePool");

      VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
      VirtualMachineRelocateSpec virtualMachineRelocateSpec = new VirtualMachineRelocateSpec();
      virtualMachineRelocateSpec.setDatastore(datastore.getMOR());
      virtualMachineRelocateSpec.setPool(resourcePoolEntities[0].getMOR());
      cloneSpec.setLocation(virtualMachineRelocateSpec);
      cloneSpec.setPowerOn(true);
      cloneSpec.setTemplate(false);
      return cloneSpec;
   }

   private VirtualMachine getVM(String vmName, Folder nodesFolder) {
      VirtualMachine vm = null;
      try {
         vm = (VirtualMachine) new InventoryNavigator(nodesFolder).searchManagedEntity("VirtualMachine", vmName);
      } catch (Exception e) {
         logger.error("Can't find vm", e);
         propagate(e);
      }
      return vm;
   }
   
   private VirtualMachine getVMwareTemplate(String imageName, Folder rootFolder) {
      VirtualMachine image = null;
      try {
         VirtualMachine node = getNode(imageName);
         if(new IsTemplatePredicate().apply(node))
            image = node;
      } catch(NullPointerException e) {
         logger.error("cannot find an image called " + imageName, e);
      }
      return checkNotNull(image, "image");
   }   

}