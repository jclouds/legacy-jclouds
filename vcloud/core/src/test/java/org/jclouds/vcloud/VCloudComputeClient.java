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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.InetAddress;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class VCloudComputeClient {
   @Resource
   protected Logger logger = Logger.NULL;

   private final Predicate<String> taskTester;
   private final VCloudClient tmClient;

   @Inject
   public VCloudComputeClient(VCloudClient tmClient, Predicate<String> successTester) {
      this.tmClient = tmClient;
      this.taskTester = successTester;
   }

   private Map<OperatingSystem, String> imageCatalogIdMap = ImmutableMap
            .<OperatingSystem, String> builder().put(OperatingSystem.CENTOS, "1").put(
                     OperatingSystem.RHEL, "8").put(OperatingSystem.UBUNTU, "11").build();

   public Map<String, String> start(String name, OperatingSystem image, int minCores, int minMegs,
            long diskSize, Map<String, String> properties) {
      checkArgument(imageCatalogIdMap.containsKey(image), "image not configured: " + image);
      String templateId = imageCatalogIdMap.get(image);
      String vDCId = tmClient.getDefaultVDC().getId();
      logger
               .debug(
                        ">> instantiating vApp vDC(%s) name(%s) template(%s)  minCores(%d) minMegs(%d) diskSize(%d) properties(%s) ",
                        vDCId, name, templateId, minCores, minMegs, diskSize, properties);
      VApp vAppResponse = tmClient.instantiateVAppTemplateInVDC(vDCId, name, templateId,
               InstantiateVAppTemplateOptions.Builder.processorCount(minCores).memory(minMegs)
                        .disk(diskSize).productProperties(properties));
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getId());

      logger.debug(">> deploying vApp(%s)", vAppResponse.getId());
      VApp vApp = blockUntilVAppStatusOrThrowException(vAppResponse, tmClient
               .deployVApp(vAppResponse.getId()), "deploy", VAppStatus.ON);// TODO, I'm not sure
      // this should be on
      // already
      // logger.debug("<< deployed vApp(%s)", vApp.getId());
      //
      // logger.debug(">> powering vApp(%s)", vApp.getId());
      // vApp = blockUntilVAppStatusOrThrowException(vApp, tmClient.powerOnVApp(vApp.getId()),
      // "powerOn", VAppStatus.ON);
      logger.debug("<< on vApp(%s)", vApp.getId());

      return ImmutableMap.<String, String> of("id", vApp.getId(), "username", null, "password",
               null);
   }

   /**
    * 
    * @throws ElementNotFoundException
    *            if no address is configured
    */
   public InetAddress getAnyPrivateAddress(String id) {
      VApp vApp = tmClient.getVApp(id);
      return Iterables.getLast(vApp.getNetworkToAddresses().values());
   }

   public void reboot(String id) {
      VApp vApp = tmClient.getVApp(id);
      logger.debug(">> rebooting vApp(%s)", vApp.getId());
      blockUntilVAppStatusOrThrowException(vApp, tmClient.resetVApp(vApp.getId()), "reset",
               VAppStatus.ON);
      logger.debug("<< on vApp(%s)", vApp.getId());
   }

   public void stop(String id) {
      VApp vApp = tmClient.getVApp(id);
      if (vApp.getStatus() != VAppStatus.OFF) {
         logger.debug(">> powering off vApp(%s), current status: %s", vApp.getId(), vApp
                  .getStatus());
         blockUntilVAppStatusOrThrowException(vApp, tmClient.powerOffVApp(vApp.getId()),
                  "powerOff", VAppStatus.OFF);
         logger.debug("<< off vApp(%s)", vApp.getId());
      }
      logger.debug(">> deleting vApp(%s)", vApp.getId());
      tmClient.deleteVApp(id);
      logger.debug("<< deleted vApp(%s)", vApp.getId());
   }

   private VApp blockUntilVAppStatusOrThrowException(VApp vApp, Task deployTask, String taskType,
            VAppStatus expectedStatus) {
      if (!taskTester.apply(deployTask.getId())) {
         throw new TaskException(taskType, vApp, deployTask);
      }

      vApp = tmClient.getVApp(vApp.getId());
      if (vApp.getStatus() != expectedStatus) {
         throw new VAppException(String.format("vApp %s status %s should be %s after %s", vApp
                  .getId(), vApp.getStatus(), expectedStatus, taskType), vApp);
      }
      return vApp;
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
}
