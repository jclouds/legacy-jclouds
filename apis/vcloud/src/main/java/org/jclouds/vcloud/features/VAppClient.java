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

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.options.CloneVAppOptions;

/**
 * Provides access to VApp functionality in vCloud
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VAppClient {
   VApp findVAppInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String vAppName);

   Task copyVAppToVDCAndName(URI sourceVApp, URI vDC, String newName, CloneVAppOptions... options);

   Task moveVAppToVDCAndRename(URI sourceVApp, URI vDC, String newName, CloneVAppOptions... options);

   VApp getVApp(URI vApp);

   /**
    * To deploy a vApp, the client makes a request to its action/deploy URL. Deploying a vApp
    * automatically deploys all of the virtual machines it contains. To deploy a virtual machine,
    * the client makes a request to its action/deploy URL.
    * <p/>
    * Deploying a Vm implicitly deploys the parent vApp if that vApp is not already deployed.
    */
   Task deployVApp(URI href);

   /**
    * like {@link #deployVApp(URI)}, except deploy transitions to power on state
    * 
    */
   Task deployAndPowerOnVApp(URI href);

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
   Task undeployVApp(URI href);

   /**
    * like {@link #undeployVApp(URI)}, where the undeployed virtual machines are suspended and their
    * suspend state saved
    * 
    */
   Task undeployAndSaveStateOfVApp(URI href);

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
   Task powerOnVApp(URI href);

   /**
    * A powerOff request to a vApp URL powers off all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A powerOff request to a virtual machine URL powers off the specified virtual machine.
    */
   Task powerOffVApp(URI href);

   /**
    * A shutdown request to a vApp URL shuts down all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A shutdown request to a virtual machine URL shuts down the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   void shutdownVApp(URI href);

   /**
    * A reset request to a vApp URL resets all of the virtual machines in the vApp, as specified in
    * its StartupSection field.
    * <p/>
    * A reset request to a virtual machine URL resets the specified virtual machine.
    */
   Task resetVApp(URI href);

   /**
    * A reboot request to a vApp URL reboots all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A reboot request to a virtual machine URL reboots the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4> Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   void rebootVApp(URI href);

   /**
    * A suspend request to a vApp URL suspends all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A suspend request to a virtual machine URL suspends the specified virtual machine.
    */
   Task suspendVApp(URI href);

   /**
    * delete a vAppTemplate, vApp, or media image. You cannot delete an object if it is in use. Any
    * object that is being copied or moved is in use. Other criteria that determine whether an
    * object is in use depend on the object type.
    * <ul>
    * <li>A vApptemplate is in use if it is being instantiated. After instantiation is complete, the
    * template is no longer in use.</li>
    * <li>A vApp is in use if it is deployed.</li>
    * <li>A media image is in use if it is inserted in a Vm.</li>
    * </ul>
    * 
    * @param href
    *           href of the vApp
    * @return task of the operation in progress
    */
   Task deleteVApp(URI href);
}
