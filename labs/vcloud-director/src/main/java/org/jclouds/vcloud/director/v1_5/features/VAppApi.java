/*
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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;

/**
 * Provides synchronous access to {@link VApp} objects.
 *
 * @author grkvlt@apache.org
 * @see VAppAsyncApi
 * @version 1.5
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VAppApi {

   /**
    * Retrieves a {@link VApp}.
    *
    * The {@link VApp} could be in one of these statuses:
    * <ul>
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#FAILED_CREATION FAILED_CREATION(-1)} -
    *    Transient entity state, e.g., model object is created but the corresponding VC backing does not
    *		exist yet. This is further sub-categorized in the respective entities.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED UNRESOLVED(0)} -
    *		Entity is whole, e.g., VM creation is complete and all the required model objects and VC backings are
    *		created.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#RESOLVED RESOLVED(1)} -
    *		Entity is resolved.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#DEPLOYED DEPLOYED(2)} -
    *		Entity is deployed.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#SUSPENDED SUSPENDED(3)} -
    *		All VMs of the vApp are suspended.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_ON POWERED_ON(4)} -
    *		All VMs of the vApp are powered on.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#WAITING_FOR_INPUT WAITING_FOR_INPUT(5)} -
    *		VM is pending response on a question.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNKNOWN UNKNOWN(6)} -
    *		Entity state could not be retrieved from the inventory, e.g., VM power state is null.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRECOGNIZED UNRECOGNIZED(7)} -
    *		Entity state was retrieved from the inventory but could not be mapped to an internal state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_OFF POWERED_OFF(8)} -
    *		All VMs of the vApp are powered off.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#INCONSISTENT_STATE INCONSISTENT_STATE(9)} -
    *		Apply to VM status, if a vm is {@code POWERED_ON}, or {@code WAITING_FOR_INPUT}, but is
    *    undeployed, it is in an inconsistent state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#MIXED MIXED(10)} -
    *		vApp status is set to {@code MIXED} when the VMs in the vApp are in different power states
    * </ul>
    *
    * <pre>
    * GET /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   VApp getVApp(URI vAppURI);

   /**
    * Modifies the name/description of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVApp(URI vAppURI, VApp vApp);

   /**
    * Deletes a {@link VApp}.
    *
    * <pre>
    * DELETE /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   Task deleteVApp(URI vAppURI);

   /**
    * Modifies the control access of a {@link VApp}.
    *
    * <pre>
    * POST /vApp/{id}/action/controlAccess
    * </pre>
    *
    * @since 0.9
    */
   ControlAccessParams modifyControlAccess(URI vAppURI, ControlAccessParams params);

   /**
    * Deploys a {@link VApp}.
    *
    * Deployment means allocation of all resource for a vApp/VM like CPU and memory
    * from a vDC resource pool. Deploying a vApp automatically deploys all of the
    * virtual machines it contains. As of version 1.5 the operation supports force
    * customization passed with {@link DeployVAppParamsType#setForceCustomization(Boolean)}
    * parameter.
    *
    * <pre>
    * POST /vApp/{id}/action/deploy
    * </pre>
    *
    * @since 0.9
    */
   Task deploy(URI vAppURI, DeployVAppParams params);

   /**
    * Discard suspended state of a {@link VApp}.
    *
    * Discarding suspended state of a vApp automatically discarded suspended
    * states of all of the virtual machines it contains.
    *
    * <pre>
    * POST /vApp/{id}/action/discardSuspendedState
    * </pre>
    *
    * @since 0.9
    */
   Task discardSuspendedState(URI vAppURI);

   /**
    * Place the {@link VApp} into maintenance mode.
    *
    * While in maintenance mode, a system admin can operate on the vApp as
    * usual, but end users are restricted to read-only operations. Any
    * user-initiated tasks running when the vApp enters maintenance mode will
    * continue.
    *
    * <pre>
    * POST /vApp/{id}/action/enterMaintenanceMode
    * </pre>
    *
    * @since 1.5
    */
   void enterMaintenanceMode(URI vAppURI);

   /**
    * Take the {@link VApp} out of maintenance mode.
    *
    * <pre>
    * POST /vApp/{id}/action/exitMaintenanceMode
    * </pre>
    *
    * @since 1.5
    */
   void exitMaintenanceMode(URI vAppURI);

   /**
    * Recompose a {@link VApp} by removing its own VMs and/or adding new ones from other
    * vApps or vApp templates.
    *
    * To remove VMs you should put their references in elements. The way you add
    * VMs is the same as described in compose vApp operation
    * {@link VdcApi#composeVApp(URI, org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams)}.
    * The status of vApp will be in {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED}
    * until the recompose task is finished.
    *
    * <pre>
    * POST /vApp/{id}/action/recomposeVApp
    * </pre>
    *
    * @since 1.0
    */
   Task recompose(URI vAppURI, RecomposeVAppParams params);

   /**
    * Undeploy a {@link VApp}.
    *
    * Undeployment means deallocation of all resources for a vApp/VM like CPU
    * and memory from a vDC resource pool. Undeploying a vApp automatically
    * undeploys all of the virtual machines it contains.
    *
    * <pre>
    * POST /vApp/{id}/action/undeploy
    * </pre>
    *
    * @since 0.9
    */
   Task undeploy(URI vAppURI, UndeployVAppParams params);

   /**
    * Retrieves the control access information for a {@link VApp}.
    *
    * The vApp could be shared to everyone or could be shared to specific user,
    * by modifying the control access values.
    *
    * <pre>
    * GET /vApp/{id}/controlAccess
    * </pre>
    *
    * @since 0.9
    */
   ControlAccessParams getControlAccess(URI vAppURI);

   /**
    * Powers off a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are powered off. This operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/powerOff
    * </pre>
    *
    * @since 0.9
    */
   Task powerOff(URI vAppURI);

   /**
    * Powers on a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are powered on. This
    * operation is allowed only when the vApp/VM is powered off.
    *
    * <pre>
    * POST /vApp/{id}/power/action/powerOn
    * </pre>
    *
    * @since 0.9
    */
   Task powerOn(URI vAppURI);

   /**
    * Reboots a {@link VApp}.
    *
    * The vApp/VM should be started in order to reboot it.
    *
    * <pre>
    * POST /vApp/{id}/power/action/reboot
    * </pre>
    *
    * @since 0.9
    */
   Task reboot(URI vAppURI);

   /**
    * Resets a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are reset. This
    * operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/reset
    * </pre>
    *
    * @since 0.9
    */
   Task reset(URI vAppURI);

   /**
    * Shuts down a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are shutdown. This
    * operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/shutdown
    * </pre>
    *
    * @since 0.9
    */
   Task shutdown(URI vAppURI);

   /**
    * Suspends a {@link VApp}.
    *
    * If the operation is used over a vApp then all VMs are suspended. This
    * operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/suspend
    * </pre>
    *
    * @since 0.9
    */
   Task suspend(URI vAppURI);

   /**
    * Retrieves the lease settings section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/leaseSettingsSection
    * </pre>
    *
    * @since 0.9
    */
   LeaseSettingsSection getLeaseSettingsSection(URI vAppURI);

   /**
    * Modifies the lease settings section of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/leaseSettingsSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyLeaseSettingsSection(URI vAppURI, LeaseSettingsSection section);

   /**
    * Retrieves the network config section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/networkConfigSection
    * </pre>
    *
    * @since 0.9
    */
   NetworkConfigSection getNetworkConfigSection(URI vAppURI);

   /**
    * Modifies the network config section of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/networkConfigSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyNetworkConfigSection(URI vAppURI, NetworkConfigSection section);

   /**
    * Retrieves the network section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/networkSection
    * </pre>
    *
    * @since 0.9
    */
   NetworkSection getNetworkSection(URI vAppURI);

   /**
    * Retrieves the owner of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/owner
    * </pre>
    *
    * @since 1.5
    */
   Owner getOwner(URI vAppURI);

   /**
    * Changes {@link VApp} owner.
    *
    * <pre>
    * PUT /vApp/{id}/owner
    * </pre>
    *
    * @since 1.5
    */
   void modifyOwner(URI vAppURI, Owner owner);

   /**
    * Retrieves {@link VApp} product sections.
    *
    * <pre>
    * GET /vApp/{id}/productSections
    * </pre>
    *
    * @since 1.5
    */
   ProductSectionList getProductSections(URI vAppURI);

   /**
    * Modifies the product section information of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/productSections
    * </pre>
    *
    * @since 1.5
    */
   Task modifyProductSections(URI vAppURI, ProductSectionList sectionList);

   /**
    * Retrieves the startup section of a {@link VApp}.
    *
    * <pre>
    * GET /vApp/{id}/startupSection
    * </pre>
    *
    * @since 0.9
    */
   StartupSection getStartupSection(URI vAppURI);

   /**
    * Modifies the startup section of a {@link VApp}.
    *
    * <pre>
    * PUT /vApp/{id}/startupSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyStartupSection(URI vAppURI, StartupSection section);

   /**
    * Synchronous access to {@link VApp} {@link Metadata} features.
    */
   @Delegate
   MetadataApi.Writeable getMetadataApi();
}
