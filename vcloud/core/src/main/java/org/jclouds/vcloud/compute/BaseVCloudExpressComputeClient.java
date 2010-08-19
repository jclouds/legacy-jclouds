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

package org.jclouds.vcloud.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class BaseVCloudExpressComputeClient implements VCloudExpressComputeClient {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final VCloudExpressClient client;
   protected final Predicate<URI> taskTester;
   protected final Map<VAppStatus, NodeState> vAppStatusToNodeState;

   @Inject
   public BaseVCloudExpressComputeClient(VCloudExpressClient client, Predicate<URI> successTester,
         Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      this.client = client;
      this.taskTester = successTester;
      this.vAppStatusToNodeState = vAppStatusToNodeState;
   }

   @Override
   public Map<String, String> start(@Nullable URI VDC, URI templateId, String name,
         InstantiateVAppTemplateOptions options, int... portsToOpen) {
      checkNotNull(options, "options");
      logger.debug(">> instantiating vApp vDC(%s) template(%s) name(%s) options(%s) ", VDC, templateId, name, options);

      VDC vdc = client.getVDC(VDC);
      VAppTemplate template = client.getVAppTemplate(templateId);

      VApp vAppResponse = client.instantiateVAppTemplateInVDC(vdc.getId(), template.getId(), name, options);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getName());

      logger.debug(">> deploying vApp(%s)", vAppResponse.getName());

      Task task = client.deployVApp(vAppResponse.getId());
      if (options.shouldBlockOnDeploy()) {
         if (!taskTester.apply(task.getLocation())) {
            throw new TaskException("deploy", vAppResponse, task);
         }
         logger.debug("<< deployed vApp(%s)", vAppResponse.getName());

         logger.debug(">> powering vApp(%s)", vAppResponse.getName());
         task = client.powerOnVApp(vAppResponse.getId());
         if (!taskTester.apply(task.getLocation())) {
            throw new TaskException("powerOn", vAppResponse, task);
         }
         logger.debug("<< on vApp(%s)", vAppResponse.getName());
      }
      return parseAndValidateResponse(template, vAppResponse);
   }

   protected Map<String, String> parseAndValidateResponse(VAppTemplate template, VApp vAppResponse) {
      Map<String, String> response = parseResponse(template, vAppResponse);
      checkState(response.containsKey("id"), "bad configuration: [id] should be in response");
      checkState(response.containsKey("username"), "bad configuration: [username] should be in response");
      checkState(response.containsKey("password"), "bad configuration: [password] should be in response");
      return response;
   }

   protected Map<String, String> parseResponse(VAppTemplate template, VApp vAppResponse) {
      Map<String, String> config = Maps.newLinkedHashMap();// Allows nulls
      config.put("id", vAppResponse.getId().toASCIIString());
      config.put("username", null);
      config.put("password", null);
      return config;
   }

   @Override
   public void reboot(URI id) {
      VApp vApp = client.getVApp(id);
      logger.debug(">> resetting vApp(%s)", vApp.getName());
      Task task = client.resetVApp(vApp.getId());
      if (!taskTester.apply(task.getLocation())) {
         throw new TaskException("resetVApp", vApp, task);
      }
      logger.debug("<< on vApp(%s)", vApp.getName());
   }

   @Override
   public void stop(URI id) {
      VApp vApp = client.getVApp(id);
      vApp = powerOffVAppIfDeployed(vApp);
      vApp = undeployVAppIfDeployed(vApp);
      deleteVApp(vApp);
      logger.debug("<< deleted vApp(%s)", vApp.getName());
   }

   private void deleteVApp(VApp vApp) {
      logger.debug(">> deleting vApp(%s)", vApp.getName());
      client.deleteVApp(vApp.getId());
   }

   private VApp undeployVAppIfDeployed(VApp vApp) {
      if (vApp.getStatus().compareTo(VAppStatus.RESOLVED) > 0) {
         logger.debug(">> undeploying vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
         Task task = client.undeployVApp(vApp.getId());
         if (!taskTester.apply(task.getLocation())) {
            throw new TaskException("undeploy", vApp, task);
         }
         vApp = client.getVApp(vApp.getId());
         logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
      }
      return vApp;
   }

   private VApp powerOffVAppIfDeployed(VApp vApp) {
      if (vApp.getStatus().compareTo(VAppStatus.OFF) > 0) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
         Task task = client.powerOffVApp(vApp.getId());
         if (!taskTester.apply(task.getLocation())) {
            throw new TaskException("powerOff", vApp, task);
         }
         vApp = client.getVApp(vApp.getId());
         logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
      }
      return vApp;
   }

   public static class TaskException extends VAppException {

      private final Task task;
      /** The serialVersionUID */
      private static final long serialVersionUID = 251801929573211256L;

      public TaskException(String type, VApp vApp, Task task) {
         super(String.format("failed to %s vApp %s status %s;task %s status %s", type, vApp.getName(),
               vApp.getStatus(), task.getLocation(), task.getStatus()), vApp);
         this.task = task;
      }

      public Task getTask() {
         return task;
      }

   }

   public static class VAppException extends RuntimeException {

      private final VApp vApp;
      /** The serialVersionUID */
      private static final long serialVersionUID = 251801929573211256L;

      public VAppException(String message, VApp vApp) {
         super(message);
         this.vApp = vApp;
      }

      public VApp getvApp() {
         return vApp;
      }

   }

   @Override
   public Set<String> getPrivateAddresses(URI id) {
      return ImmutableSet.of();
   }

   @Override
   public Set<String> getPublicAddresses(URI id) {
      VApp vApp = client.getVApp(id);
      return Sets.newHashSet(vApp.getNetworkToAddresses().values());
   }

}