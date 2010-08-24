/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.options.CloneVAppOptions;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VCloudClient extends CommonVCloudClient {

   VApp instantiateVAppTemplateInVDC(URI vDC, URI template, String appName, InstantiateVAppTemplateOptions... options);

   Task cloneVAppInVDC(URI vDC, URI toClone, String newName, CloneVAppOptions... options);

   VAppTemplate getVAppTemplate(URI vAppTemplate);

   /**
    * returns the vapp template corresponding to a catalog item in the catalog associated with the
    * specified name. Note that the org and catalog parameters can be null to choose default.
    * 
    * @param orgName
    *           organization name, or null for the default
    * @param catalogName
    *           catalog name, or null for the default
    * @param itemName
    *           item you wish to lookup
    * 
    * @throws NoSuchElementException
    *            if you specified an org, catalog, or catalog item name that isn't present
    */
   VAppTemplate findVAppTemplateInOrgCatalogNamed(@Nullable String orgName, @Nullable String catalogName,
            String itemName);

   VApp findVAppInOrgVDCNamed(@Nullable String orgName, @Nullable String catalogName, String vAppName);

   VApp getVApp(URI vApp);

   /**
    * To deploy a vApp, the client makes a request to its action/deploy URL. Deploying a vApp
    * automatically deploys all of the virtual machines it contains. To deploy a virtual machine,
    * the client makes a request to its action/deploy URL.
    * <p/>
    * Deploying a Vm implicitly deploys the parent vApp if that vApp is not already deployed.
    */
   Task deployVAppOrVm(URI vAppOrVmId);

   /**
    * like {@link #deployVAppOrVm(URI)}, except deploy transistions to power on state
    * 
    */
   Task deployAndPowerOnVAppOrVm(URI vAppOrVmId);

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
    * {@link #undeployAndSaveStateOfVAppOrVm}
    * 
    */
   Task undeployVAppOrVm(URI vAppOrVmId);

   /**
    * like {@link #undeployVAppOrVm(URI)}, where the undeployed virtual machines are suspended and
    * their suspend state saved
    * 
    */
   Task undeployAndSaveStateOfVAppOrVm(URI vAppOrVmId);

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
   Task powerOnVAppOrVm(URI vAppOrVmId);

   /**
    * A powerOff request to a vApp URL powers off all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A powerOff request to a virtual machine URL powers off the specified virtual machine.
    */
   Task powerOffVAppOrVm(URI vAppOrVmId);

   /**
    * A shutdown request to a vApp URL shuts down all of the virtual machines in the vApp, as
    * specified in its StartupSection field.
    * <p/>
    * A shutdown request to a virtual machine URL shuts down the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   void shutdownVAppOrVm(URI vAppOrVmId);

   /**
    * A reset request to a vApp URL resets all of the virtual machines in the vApp, as specified in
    * its StartupSection field.
    * <p/>
    * A reset request to a virtual machine URL resets the specified virtual machine.
    */
   Task resetVAppOrVm(URI vAppOrVmId);

   /**
    * A reboot request to a vApp URL reboots all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A reboot request to a virtual machine URL reboots the specified virtual machine.
    * <p/>
    * <h4>NOTE</h4> Because this request sends a signal to the guest OS, the vCloud API cannot track
    * the progress or verify the result of the requested operation. Hence, void is returned
    */
   void rebootVAppOrVm(URI vAppOrVmId);

   /**
    * A suspend request to a vApp URL suspends all of the virtual machines in the vApp, as specified
    * in its StartupSection field.
    * <p/>
    * A suspend request to a virtual machine URL suspends the specified virtual machine.
    */
   Task suspendVAppOrVm(URI vAppOrVmId);
}
