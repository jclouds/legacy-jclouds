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
import org.jclouds.rest.annotations.EndpointParam;
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
import org.jclouds.vcloud.director.v1_5.functions.href.VAppURNToHref;

/**
 * Provides synchronous access to {@link VApp} objects.
 * 
 * @author grkvlt@apache.org, Adrian Cole
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
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#FAILED_CREATION
    * FAILED_CREATION(-1)} - Transient entity state, e.g., model object is addd but the
    * corresponding VC backing does not exist yet. This is further sub-categorized in the respective
    * entities.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED
    * UNRESOLVED(0)} - Entity is whole, e.g., VM creation is complete and all the required model
    * objects and VC backings are created.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#RESOLVED
    * RESOLVED(1)} - Entity is resolved.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#DEPLOYED
    * DEPLOYED(2)} - Entity is deployed.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#SUSPENDED
    * SUSPENDED(3)} - All VMs of the vApp are suspended.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_ON
    * POWERED_ON(4)} - All VMs of the vApp are powered on.
    * <li>
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#WAITING_FOR_INPUT
    * WAITING_FOR_INPUT(5)} - VM is pending response on a question.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNKNOWN
    * UNKNOWN(6)} - Entity state could not be retrieved from the inventory, e.g., VM power state is
    * null.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRECOGNIZED
    * UNRECOGNIZED(7)} - Entity state was retrieved from the inventory but could not be mapped to an
    * internal state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_OFF
    * POWERED_OFF(8)} - All VMs of the vApp are powered off.
    * <li>
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#INCONSISTENT_STATE
    * INCONSISTENT_STATE(9)} - Apply to VM status, if a vm is {@code POWERED_ON}, or
    * {@code WAITING_FOR_INPUT}, but is undeployed, it is in an inconsistent state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#MIXED MIXED(10)}
    * - vApp status is set to {@code MIXED} when the VMs in the vApp are in different power states
    * </ul>
    * 
    * <pre>
    * GET /vApp/{id}
    * </pre>
    * 
    * @since 0.9
    */
   VApp get(String vAppUrn);

   VApp get(URI vAppHref);

   /**
    * Modifies the name/description of a {@link VApp}.
    * 
    * <pre>
    * PUT /vApp/{id}
    * </pre>
    * 
    * @since 0.9
    */
   Task edit(String vAppUrn, VApp vApp);

   Task edit(URI vAppHref, VApp vApp);

   /**
    * Deletes a {@link VApp}.
    * 
    * <pre>
    * DELETE /vApp/{id}
    * </pre>
    * 
    * @since 0.9
    */
   Task remove(String vAppUrn);

   Task remove(URI vAppHref);

   /**
    * Modifies the control access of a {@link VApp}.
    * 
    * <pre>
    * POST /vApp/{id}/action/controlAccess
    * </pre>
    * 
    * @since 0.9
    */
   ControlAccessParams editControlAccess(String vAppUrn, ControlAccessParams params);

   ControlAccessParams editControlAccess(URI vAppHref, ControlAccessParams params);

   /**
    * Deploys a {@link VApp}.
    * 
    * Deployment means allocation of all resource for a vApp/VM like CPU and memory from a vDC
    * resource pool. Deploying a vApp automatically deploys all of the virtual machines it contains.
    * As of version 1.5 the operation supports force customization passed with
    * {@link DeployVAppParamsType#setForceCustomization(Boolean)} parameter.
    * 
    * <pre>
    * POST /vApp/{id}/action/deploy
    * </pre>
    * 
    * @since 0.9
    */
   Task deploy(String vAppUrn, DeployVAppParams params);

   Task deploy(URI vAppHref, DeployVAppParams params);

   /**
    * Discard suspended state of a {@link VApp}.
    * 
    * Discarding suspended state of a vApp automatically discarded suspended states of all of the
    * virtual machines it contains.
    * 
    * <pre>
    * POST /vApp/{id}/action/discardSuspendedState
    * </pre>
    * 
    * @since 0.9
    */
   Task discardSuspendedState(String vAppUrn);

   Task discardSuspendedState(URI vAppHref);

   /**
    * Place the {@link VApp} into maintenance mode.
    * 
    * While in maintenance mode, a system admin can operate on the vApp as usual, but end users are
    * restricted to read-only operations. Any user-initiated tasks running when the vApp enters
    * maintenance mode will continue.
    * 
    * <pre>
    * POST /vApp/{id}/action/enterMaintenanceMode
    * </pre>
    * 
    * @since 1.5
    */
   void enterMaintenanceMode(String vAppUrn);

   void enterMaintenanceMode(URI vAppHref);

   /**
    * Take the {@link VApp} out of maintenance mode.
    * 
    * <pre>
    * POST /vApp/{id}/action/exitMaintenanceMode
    * </pre>
    * 
    * @since 1.5
    */
   void exitMaintenanceMode(String vAppUrn);

   void exitMaintenanceMode(URI vAppHref);

   /**
    * Recompose a {@link VApp} by removing its own VMs and/or adding new ones from other vApps or
    * vApp templates.
    * 
    * To remove VMs you should put their references in elements. The way you add VMs is the same as
    * described in compose vApp operation
    * {@link VdcApi#composeVApp(String, org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams)}.
    * The status of vApp will be in
    * {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED} until the
    * recompose task is finished.
    * 
    * <pre>
    * POST /vApp/{id}/action/recomposeVApp
    * </pre>
    * 
    * @since 1.0
    */
   Task recompose(String vAppUrn, RecomposeVAppParams params);

   Task recompose(URI vAppHref, RecomposeVAppParams params);

   /**
    * Undeploy a {@link VApp}.
    * 
    * Undeployment means deallocation of all resources for a vApp/VM like CPU and memory from a vDC
    * resource pool. Undeploying a vApp automatically undeploys all of the virtual machines it
    * contains.
    * 
    * <pre>
    * POST /vApp/{id}/action/undeploy
    * </pre>
    * 
    * @since 0.9
    */
   Task undeploy(String vAppUrn, UndeployVAppParams params);

   Task undeploy(URI vAppHref, UndeployVAppParams params);

   /**
    * Retrieves the control access information for a {@link VApp}.
    * 
    * The vApp could be shared to everyone or could be shared to specific user, by editing the
    * control access values.
    * 
    * <pre>
    * GET /vApp/{id}/controlAccess
    * </pre>
    * 
    * @since 0.9
    */
   // TODO: revise
   ControlAccessParams getAccessControl(String vAppUrn);

   ControlAccessParams getAccessControl(URI vAppHref);

   /**
    * Powers off a {@link VApp}.
    * 
    * If the operation is used over a vApp then all VMs are powered off. This operation is allowed
    * only when the vApp/VM is powered on.
    * 
    * <pre>
    * POST /vApp/{id}/power/action/powerOff
    * </pre>
    * 
    * @since 0.9
    */
   Task powerOff(String vAppUrn);

   Task powerOff(URI vAppHref);

   /**
    * Powers on a {@link VApp}.
    * 
    * If the operation is used over a vApp then all VMs are powered on. This operation is allowed
    * only when the vApp/VM is powered off.
    * 
    * <pre>
    * POST /vApp/{id}/power/action/powerOn
    * </pre>
    * 
    * @since 0.9
    */
   Task powerOn(String vAppUrn);

   Task powerOn(URI vAppHref);

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
   Task reboot(String vAppUrn);

   Task reboot(URI vAppHref);

   /**
    * Resets a {@link VApp}.
    * 
    * If the operation is used over a vApp then all VMs are reset. This operation is allowed only
    * when the vApp/VM is powered on.
    * 
    * <pre>
    * POST /vApp/{id}/power/action/reset
    * </pre>
    * 
    * @since 0.9
    */
   Task reset(String vAppUrn);

   Task reset(URI vAppHref);

   /**
    * Shuts down a {@link VApp}.
    * 
    * If the operation is used over a vApp then all VMs are shutdown. This operation is allowed only
    * when the vApp/VM is powered on.
    * 
    * <pre>
    * POST /vApp/{id}/power/action/shutdown
    * </pre>
    * 
    * @since 0.9
    */
   Task shutdown(String vAppUrn);

   Task shutdown(URI vAppHref);

   /**
    * Suspends a {@link VApp}.
    * 
    * If the operation is used over a vApp then all VMs are suspended. This operation is allowed
    * only when the vApp/VM is powered on.
    * 
    * <pre>
    * POST /vApp/{id}/power/action/suspend
    * </pre>
    * 
    * @since 0.9
    */
   Task suspend(String vAppUrn);

   Task suspend(URI vAppHref);

   /**
    * Retrieves the lease settings section of a {@link VApp}.
    * 
    * <pre>
    * GET /vApp/{id}/leaseSettingsSection
    * </pre>
    * 
    * @since 0.9
    */
   LeaseSettingsSection getLeaseSettingsSection(String vAppUrn);

   LeaseSettingsSection getLeaseSettingsSection(URI vAppHref);

   /**
    * Modifies the lease settings section of a {@link VApp}.
    * 
    * <pre>
    * PUT /vApp/{id}/leaseSettingsSection
    * </pre>
    * 
    * @since 0.9
    */
   Task editLeaseSettingsSection(String vAppUrn, LeaseSettingsSection section);

   Task editLeaseSettingsSection(URI vAppHref, LeaseSettingsSection section);

   /**
    * Retrieves the network config section of a {@link VApp}.
    * 
    * <pre>
    * GET /vApp/{id}/networkConfigSection
    * </pre>
    * 
    * @since 0.9
    */
   NetworkConfigSection getNetworkConfigSection(String vAppUrn);

   NetworkConfigSection getNetworkConfigSection(URI vAppHref);

   /**
    * Modifies the network config section of a {@link VApp}.
    * 
    * <pre>
    * PUT /vApp/{id}/networkConfigSection
    * </pre>
    * 
    * @since 0.9
    */
   Task editNetworkConfigSection(String vAppUrn, NetworkConfigSection section);

   Task editNetworkConfigSection(URI vAppHref, NetworkConfigSection section);

   /**
    * Retrieves the network section of a {@link VApp}.
    * 
    * <pre>
    * GET /vApp/{id}/networkSection
    * </pre>
    * 
    * @since 0.9
    */
   NetworkSection getNetworkSection(String vAppUrn);

   NetworkSection getNetworkSection(URI vAppHref);

   /**
    * Retrieves the owner of a {@link VApp}.
    * 
    * <pre>
    * GET /vApp/{id}/owner
    * </pre>
    * 
    * @since 1.5
    */
   Owner getOwner(String vAppUrn);

   Owner getOwner(URI vAppHref);

   /**
    * Changes {@link VApp} owner.
    * 
    * <pre>
    * PUT /vApp/{id}/owner
    * </pre>
    * 
    * @since 1.5
    */
   void editOwner(String vAppUrn, Owner owner);

   void editOwner(URI vAppHref, Owner owner);

   /**
    * Retrieves {@link VApp} product sections.
    * 
    * <pre>
    * GET /vApp/{id}/productSections
    * </pre>
    * 
    * @since 1.5
    */
   ProductSectionList getProductSections(String vAppUrn);

   ProductSectionList getProductSections(URI vAppHref);

   /**
    * Modifies the product section information of a {@link VApp}.
    * 
    * <pre>
    * PUT /vApp/{id}/productSections
    * </pre>
    * 
    * @since 1.5
    */
   Task editProductSections(String vAppUrn, ProductSectionList sectionList);

   Task editProductSections(URI vAppHref, ProductSectionList sectionList);

   /**
    * Retrieves the startup section of a {@link VApp}.
    * 
    * <pre>
    * GET /vApp/{id}/startupSection
    * </pre>
    * 
    * @since 0.9
    */
   StartupSection getStartupSection(String vAppUrn);

   StartupSection getStartupSection(URI vAppHref);

   /**
    * Modifies the startup section of a {@link VApp}.
    * 
    * <pre>
    * PUT /vApp/{id}/startupSection
    * </pre>
    * 
    * @since 0.9
    */
   Task editStartupSection(String vAppUrn, StartupSection section);

   Task editStartupSection(URI vAppHref, StartupSection section);

   /**
    * Synchronous access to {@link VApp} {@link Metadata} features.
    */
   @Delegate
   MetadataApi.Writeable getMetadataApi(@EndpointParam(parser = VAppURNToHref.class) String vAppUrn);

   @Delegate
   MetadataApi.Writeable getMetadataApi(@EndpointParam URI vAppHref);

}
