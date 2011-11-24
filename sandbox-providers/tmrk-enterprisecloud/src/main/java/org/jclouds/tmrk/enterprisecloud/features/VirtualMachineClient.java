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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.concurrent.Timeout;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.hardware.HardwareConfiguration;
import org.jclouds.tmrk.enterprisecloud.domain.network.AssignedIpAddresses;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachine;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachineConfigurationOptions;
import org.jclouds.tmrk.enterprisecloud.domain.vm.VirtualMachines;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to VirtualMachine.
 * <p/>
 * 
 * @see VirtualMachineAsyncClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VirtualMachineClient {

    /**
     * returns information regarding virtual machines defined in a compute pool
     * @param uri the uri of the compute pool
     * @return the virtual machines
     */
   VirtualMachines getVirtualMachines(URI uri);

   /**
    * The Get Virtual Machines by ID call returns information regarding a
    * specified virtual machine defined in an environment.
    * @param uri the id of the virtual machine
    * @return the virtual Machine or null if not found
    */
   VirtualMachine getVirtualMachine(URI uri);

    /**
     * The Get Virtual Machines Assigned IP Addresses call returns information
     * regarding the IP addresses assigned to a specified virtual machine in a compute pool.
     * @param uri the assignedIpAddresses call
     * @return the assigned ip addresses
     */
   AssignedIpAddresses getAssignedIpAddresses(URI uri);

   /**
    * The Get Virtual Machines Configuration Options call returns information
    * regarding the configuration options of a specified virtual machine in a compute pool.
    * @param uri the uri for the configuration options. e.g. /cloudapi/ecloud/virtualmachines/{id}/configurationoptions
    * @return the configuration options
    */
   VirtualMachineConfigurationOptions getConfigurationOptions(URI uri);

   /**
    * The Get Virtual Machines Hardware Configuration call returns information
    * regarding the hardware configuration of a specified virtual machine in a compute pool.
    * @param uri the uri for the hardware configuration e.g. /cloudapi/ecloud/virtualmachines/{id}/hardwareconfiguration
    * @return
    */
   HardwareConfiguration getHardwareConfiguration(URI uri);

   /**
    * The Action Virtual Machines Power On call powers on a specified virtual machine.
    * If successful, the call returns the task that powered on the virtual machine.
    * Note: To power on requires a PoweredOn value of false.
    * @param uri the uri of the virtual machine
    * @return Task
    */
   Task powerOn(URI uri);

   /**
    * The Action Virtual Machines Power Off call powers off a specified virtual machine.
    * Power off simply terminates the virtual machine whereas
    * shutdown requests the virtual machine to end all processes and turn itself off
    * when all processes complete.
    * If successful, the call returns the task that powered off the virtual machine.
    * Note: To power off requires a PoweredOn value of true.
    * @param uri the uri of the virtual machine
    * @return Task
    */
   Task powerOff(URI uri);

   /**
    * The Action Virtual Machines Power Reboot call reboots a specified virtual machine.
    * If successful, the call returns the task that rebooted the virtual machine.
    * Note: To reboot requires a ToolsStatus value of Current or OutOfDate and a PoweredOn value of true.
    * @param uri the uri of the virtual machine
    * @return Task
    */
   Task reboot(URI uri);

   /**
    * The Action Virtual Machines Power Shutdown call shuts down a specified virtual machine.
    * Shutdown requests the virtual machine to end all processes and turn itself off when all processes complete whereas power off simply terminates the virtual machine.
    * If successful, the call returns the task that shut down the virtual machine.
    * Note: To shutdown requires a ToolsStatus value of Current or OutOfDate and a PoweredOn value of true.
    * @param uri the uri of the virtual machine
    * @return Task
    */
   Task shutdown(URI uri);

   /**
    * The Action Virtual Machines Tools Mount call mounts the virtual volume
    * for VMware Tools on a specified virtual machine.
    * If successful, the call returns the task that mounted the tools.
    * Note: To mount VMware Tools requires a PoweredOn value of true.
    * @param uri the uri of the virtual machine
    * @return Task
    */
   Task mountTools(URI uri);

   /**
    * The Action Virtual Machines Tools Unmount call unmounts the virtual volume for VMware Tools
    * on a specified virtual machine.
    * If successful, the call returns the task that unmounted the tools.
    * Note: To unmount VMware Tools requires a PoweredOn value of true.
    * @param uri the uri of the virtual machine
    * @return Task
    */
   Task unmountTools(URI uri);

   /**
    *  * The Action Virtual Machines Remove call removes a specified virtual machine from the compute pool.
    * If successful, the call returns the task that removed the virtual machine.
    * Note: To remove a virtual machine requires a Status value of Deployed and a PoweredOn value of false.
    */
   Task remove(URI uri);
}
