package org.jclouds.virtualbox.functions;

import static org.jclouds.virtualbox.experiment.TestUtils.computeServiceForLocalhostAndGuest;
import static org.jclouds.virtualbox.util.MachineUtils.applyForMachine;

import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.jclouds.virtualbox.domain.ExecutionType;
import org.testng.annotations.Test;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Predicate;

@Test(groups = "live", singleThreaded = true, testName = "IsoToIMachineLiveTest")
public class CheckSshDaemonIsRunningLiveTest extends BaseVirtualBoxClientLiveTest {
	
   private boolean forceOverwrite = true;
   private String vmId = "jclouds-image-iso-1";
   private String osTypeId = "";
   private String controllerIDE = "IDE Controller";
   private String diskFormat = "";
   private String adminDisk = "testadmin.vdi";
   private String guestId = "guest";
   private String hostId = "host";

   private String vmName = "jclouds-image-virtualbox-iso-to-machine-test";
   private String isoName = "ubuntu-11.04-server-i386.iso";
   
  @Test
  public void testCheckSshDaemonIsRunning() {
     VirtualBoxManager manager = (VirtualBoxManager) context.getProviderSpecificContext().getApi();
     ComputeServiceContext localHostContext =
             computeServiceForLocalhostAndGuest(hostId, "localhost", guestId, "localhost", new Credentials("toor", "password"));

     IMachine nodeWithSshDaemonRunning = getNodeWithSshDaemonRunning(manager, localHostContext);
     ensureMachineIsLaunched(vmName);
     new CheckSshDaemonIsRunning(localHostContext, guestId).apply(nodeWithSshDaemonRunning);
  }

  private IMachine getNodeWithSshDaemonRunning(VirtualBoxManager manager, ComputeServiceContext localHostContext) {
     try {
        Predicate<IPSocket> socketTester = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 10, 1, TimeUnit.SECONDS);
        return new IsoToIMachine(manager, adminDisk, diskFormat,
                vmName, osTypeId, vmId, forceOverwrite,
                controllerIDE, localHostContext, hostId, guestId, socketTester,
                "127.0.0.1", 8080).apply(isoName);
     } catch (IllegalStateException e) {
        // already created
        return manager.getVBox().findMachine(vmName);
     }
  }
  
  private void ensureMachineIsLaunched(String vmName) {
     applyForMachine(manager, vmName, new LaunchMachineIfNotAlreadyRunning(manager, ExecutionType.GUI, ""));
  }
  
}