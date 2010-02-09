/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.processorCount;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
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
public class BaseVCloudComputeClient implements VCloudComputeClient {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final VCloudClient client;
   protected final Predicate<String> taskTester;
   protected final Predicate<VApp> notFoundTester;
   protected final Map<VAppStatus, NodeState> vAppStatusToNodeState;

   @Inject
   public BaseVCloudComputeClient(VCloudClient client, Predicate<String> successTester,
            @Named("NOT_FOUND") Predicate<VApp> notFoundTester,
            Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      this.client = client;
      this.taskTester = successTester;
      this.notFoundTester = notFoundTester;
      this.vAppStatusToNodeState = vAppStatusToNodeState;
   }

   public Map<String, String> start(String vDCId, String name, String templateId, int minCores,
            int minMegs, Long diskSize, Map<String, String> properties, int... portsToOpen) {
      logger
               .debug(
                        ">> instantiating vApp vDC(%s) name(%s) template(%s)  minCores(%d) minMegs(%d) diskSize(%d) properties(%s) ",
                        vDCId, name, templateId, minCores, minMegs, diskSize, properties);
      InstantiateVAppTemplateOptions options = processorCount(minCores).memory(minMegs)
               .productProperties(properties);
      if (diskSize != null)
         options.disk(diskSize);
      VApp vAppResponse = client.instantiateVAppTemplateInVDC(vDCId, name, templateId, options);
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getId());

      logger.debug(">> deploying vApp(%s)", vAppResponse.getId());

      Task task = client.deployVApp(vAppResponse.getId());
      if (!taskTester.apply(task.getId())) {
         throw new TaskException("deploy", vAppResponse, task);
      }

      logger.debug("<< deployed vApp(%s)", vAppResponse.getId());

      logger.debug(">> powering vApp(%s)", vAppResponse.getId());
      task = client.powerOnVApp(vAppResponse.getId());
      if (!taskTester.apply(task.getId())) {
         throw new TaskException("powerOn", vAppResponse, task);
      }
      logger.debug("<< on vApp(%s)", vAppResponse.getId());

      Map<String, String> response = parseResponse(vAppResponse);
      checkState(response.containsKey("id"), "bad configuration: [id] should be in response");
      checkState(response.containsKey("username"),
               "bad configuration: [username] should be in response");
      checkState(response.containsKey("password"),
               "bad configuration: [password] should be in response");
      return response;
   }

   protected Map<String, String> parseResponse(VApp vAppResponse) {
      Map<String, String> config = Maps.newLinkedHashMap();// Allows nulls
      config.put("id", vAppResponse.getId());
      config.put("username", null);
      config.put("password", null);
      return config;
   }

   public void reboot(String id) {
      VApp vApp = client.getVApp(id);
      logger.debug(">> resetting vApp(%s)", vApp.getId());
      Task task = client.resetVApp(vApp.getId());
      if (!taskTester.apply(task.getId())) {
         throw new TaskException("resetVApp", vApp, task);
      }
      logger.debug("<< on vApp(%s)", vApp.getId());
   }

   public void stop(String id) {
      VApp vApp = client.getVApp(id);
      if (vApp.getStatus() != VAppStatus.OFF) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getId(), vApp
                  .getStatus());
         Task task = client.powerOffVApp(vApp.getId());
         if (!taskTester.apply(task.getId())) {
            throw new TaskException("powerOff", vApp, task);
         }
         logger.debug("<< off vApp(%s)", vApp.getId());
      }
      logger.debug(">> deleting vApp(%s)", vApp.getId());
      client.deleteVApp(id);
      boolean successful = notFoundTester.apply(vApp);
      logger.debug("<< deleted vApp(%s) completed(%s)", vApp.getId(), successful);
   }

   public static class TaskException extends VAppException {

      private final Task task;
      /** The serialVersionUID */
      private static final long serialVersionUID = 251801929573211256L;

      public TaskException(String type, VApp vApp, Task task) {
         super(String.format("failed to %s vApp %s status %s;task %s status %s", type,
                  vApp.getId(), vApp.getStatus(), task.getLocation(), task.getStatus()), vApp);
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
   public Set<InetAddress> getPrivateAddresses(String id) {
      return ImmutableSet.of();
   }

   @Override
   public Set<InetAddress> getPublicAddresses(String id) {
      VApp vApp = client.getVApp(id);
      return Sets.newHashSet(vApp.getNetworkToAddresses().values());
   }

}