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
package org.jclouds.vcloud.features;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.Vm;

/**
 * Provides access to VM functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VmClient {

   Vm getVm(URI vApp);

   /**
    * To deploy a vApp, the client makes a request to its action/deploy URL. Deploying a vApp
    * automatically deploys all of the virtual machines it contains. To deploy a virtual machine,
    * the client makes a request to its action/deploy URL.
    * <p/>
    * Deploying a Vm implicitly deploys the parent vApp if that vApp is not already deployed.
    */
   Task deployVm(URI href);

   /**
    * like {@link #deploy(URI)}, except deploy transitions to power on state
    * 
    */
   Task deployAndPowerOnVm(URI href);

   /**
    * Undeploying a vApp powers off or suspends any running virtual machines it contains, then frees
    * the resources reserved for the vApp and sets the vApp’s deploy attribute to a value of false
    * to indicate that it is not deployed.
    * <p/>
    * Undeploying a virtual machine powers off or suspends the virtual machine, then frees the
    * resources reserved for it and sets the its deploy attribute to a value of false to indicate
    * that it is not deployed. This operation has no effect on the containing vApp.
    * <h4>NOTE</h4>
    * Using this method will simply power off the vms. In order to save their state, use
    * {@link #undeployAndSaveStateOf}
    * 
    */
   Task undeployVm(URI href);

   /**
    * like {@link #undeploy(URI)}, where the undeployed virtual machines are suspended and their
    * suspend state saved
    * 
    */
   Task undeployAndSaveStateOfVm(URI href);

   /**
    * A powerOn request to a vApp URL powers on all of the virtual machines in the vApp, as
    * specified in the vApp’s StartupSection field.
    * <p/>
    * A powerOn request to a virtual machine URL powers on the specified virtual machine and forces
    * deployment of the parent vApp.
    * <p/>
    * <h4>NOTE</h4> A powerOn request to a vApp or virtual machine that is undeployed forces
    * deployment.
    */
   Task powerOnVm(URI href);

   /**
    * A powerOff request to a vApp URL powers off all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A powerOff request to a virtual machine URL powers off the specified virtual machine.
    */
   Task powerOffVm(URI href);

   /**
    * A shutdown request to a vApp URL shuts down all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A shutdown request to a virtual machine URL shuts down the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   void shutdownVm(URI href);

   /**
    * A reset request to a vApp URL resets all of the virtual machines in the vApp, as specified in
    * its StartupSection field.
    * <p/>
    * A reset request to a virtual machine URL resets the specified virtual machine.
    */
   Task resetVm(URI href);

   /**
    * A reboot request to a vApp URL reboots all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A reboot request to a virtual machine URL reboots the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4> Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   void rebootVm(URI href);

   /**
    * A suspend request to a vApp URL suspends all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A suspend request to a virtual machine URL suspends the specified virtual machine.
    */
   Task suspendVm(URI href);

   /**
    * Get a Screen Thumbnail for a Virtual Machine
    * 
    * @param href
    *           to snapshot
    */
   InputStream getScreenThumbnailForVm(URI href);

   /**
    * Modify the Guest Customization Section of a Virtual Machine
    * 
    * @param href
    *           uri to modify
    * @param updated
    *           guestCustomizationSection
    * @return task in progress
    */
   Task updateGuestCustomizationOfVm(GuestCustomizationSection guestCustomizationSection, URI href);

   /**
    * Modify the Network Connection Section of a Virtual Machine
    * 
    * @param href
    *           uri to modify
    * @param updated
    *           networkConnectionSection
    * @return task in progress
    */
   Task updateNetworkConnectionOfVm(NetworkConnectionSection guestCustomizationSection, URI href);

   /**
    * update the cpuCount of an existing VM
    * 
    * @param href
    *           to update
    * @param cpuCount
    *           count to change the primary cpu to
    */
   Task updateCPUCountOfVm(int cpuCount, URI href);

   /**
    * update the memoryInMB of an existing VM
    * 
    * @param href
    *           to update
    * @param memoryInMB
    *           memory in MB to assign to the VM
    */
   Task updateMemoryMBOfVm(int memoryInMB, URI href);
}
