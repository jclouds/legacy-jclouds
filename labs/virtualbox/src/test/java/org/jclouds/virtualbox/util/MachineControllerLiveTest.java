package org.jclouds.virtualbox.util;

import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_IMAGE_PREFIX;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_INSTALLATION_KEY_SEQUENCE;
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.jclouds.config.ValueOfConfigurationKeyOrNull;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.HardDisk;
import org.jclouds.virtualbox.domain.IsoSpec;
import org.jclouds.virtualbox.domain.MasterSpec;
import org.jclouds.virtualbox.domain.NetworkAdapter;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;
import org.jclouds.virtualbox.domain.NetworkSpec;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;
import org.jclouds.virtualbox.functions.CreateAndRegisterMachineFromIsoIfNotAlreadyExists;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.virtualbox_4_1.CleanupMode;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.NetworkAttachmentType;
import org.virtualbox_4_1.StorageBus;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

@Test(groups = "live", singleThreaded = true, testName = "MachineControllerLiveTest")
public class MachineControllerLiveTest extends BaseVirtualBoxClientLiveTest {

   private String vmName;
   private MasterSpec vm;
   private VmSpec vmSpec;

   @Override
   @BeforeClass(groups = "live")
   public void setupClient() {
      super.setupClient();
      vmName = VIRTUALBOX_IMAGE_PREFIX + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getClass().getSimpleName());
      String adminDisk = workingDir + File.separator + vmName + ".vdi";

      StorageController ideController = StorageController
            .builder()
            .name("IDE Controller")
            .bus(StorageBus.IDE)
            .attachISO(0, 0, operatingSystemIso)
            .attachHardDisk(
                  HardDisk.builder().diskpath(adminDisk).controllerPort(0).deviceSlot(1).autoDelete(true).build())
            .attachISO(1, 1, guestAdditionsIso).build();

      vmSpec = VmSpec.builder().id(vmName).name(vmName).osTypeId("").memoryMB(512).cleanUpMode(CleanupMode.Full)
            .controller(ideController).forceOverwrite(true).build();
      
      undoVm(vmSpec);

      Injector injector = context.utils().injector();
      Function<String, String> configProperties = injector.getInstance(ValueOfConfigurationKeyOrNull.class);
      IsoSpec isoSpec = IsoSpec
            .builder()
            .sourcePath(operatingSystemIso)
            .installationScript(
                  configProperties.apply(VIRTUALBOX_INSTALLATION_KEY_SEQUENCE).replace("HOSTNAME", vmSpec.getVmName()))
            .build();

      NetworkAdapter networkAdapter = NetworkAdapter.builder().networkAttachmentType(NetworkAttachmentType.NAT).build();
      NetworkInterfaceCard networkInterfaceCard = NetworkInterfaceCard.builder().slot(0L).addNetworkAdapter(networkAdapter)
            .build();

      NetworkSpec networkSpec = NetworkSpec.builder().addNIC(networkInterfaceCard).build();
      vm = MasterSpec.builder().iso(isoSpec).vm(vmSpec).network(networkSpec).build();

      createOrFindMachine();
   }

   @Test
   public void testEnsureMachineIsPoweredOff() {
      machineController.ensureMachineIsRunning(vmName);
      machineController.ensureMachineIsPoweredOff(vmName);
      assertTrue(machineController.isPoweredOff(vm.getVmSpec().getVmName()));
   }

   @Test
   public void testPoweringDownTwice(){
      machineController.ensureMachineIsPoweredOff(vmName);
      machineController.ensureMachineIsPoweredOff(vmName);
      assertTrue(machineController.isPoweredOff(vm.getVmSpec().getVmName()));
   }
   
   private IMachine createOrFindMachine() {
      try {
         Injector injector = context.utils().injector();
         return injector.getInstance(CreateAndRegisterMachineFromIsoIfNotAlreadyExists.class).apply(vm);
      } catch (IllegalStateException e) {
         // already created
         return manager.get().getVBox().findMachine(vm.getVmSpec().getVmId());
      }
   }
   
   @Override
   @AfterClass(groups = "live")
   protected void tearDown() throws Exception {
       for (VmSpec spec : ImmutableSet.of(vmSpec)) {
        undoVm(spec);
       }
       super.tearDown();
   }
}