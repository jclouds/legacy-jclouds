/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.logging.Logger;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshClient.Factory;
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

   private final Predicate<InetSocketAddress> socketTester;
   private final Predicate<URI> taskTester;
   private final VCloudClient tmClient;

   @Inject
   public VCloudComputeClient(VCloudClient tmClient, Factory sshFactory,
            Predicate<InetSocketAddress> socketTester, Predicate<URI> successTester) {
      this.tmClient = tmClient;
      this.sshFactory = sshFactory;
      this.socketTester = socketTester;
      this.taskTester = successTester;
   }

   private final Factory sshFactory;

   public enum Image {
      CENTOS_53, RHEL_53, UMBUNTU_90, UMBUNTU_JEOS
   }

   private Map<Image, String> imageCatalogIdMap = ImmutableMap.<Image, String> builder().put(
            Image.CENTOS_53, "3").put(Image.RHEL_53, "8").put(Image.UMBUNTU_90, "10").put(
            Image.UMBUNTU_JEOS, "11").build();

   private String username;

   private String password;

   public String start(String name, int minCores, int minMegs, Image image) {
      checkArgument(imageCatalogIdMap.containsKey(image), "image not configured: " + image);
      String templateId = imageCatalogIdMap.get(image);
      String vDCId = tmClient.getDefaultVDC().getId();
      logger.debug(">> instantiating vApp name(%s) minCores(%d) minMegs(%d) template(%s) vDC(%s)",
               name, minCores, minMegs, templateId, vDCId);
      VApp vAppResponse = tmClient.instantiateVAppTemplateInVDC(name, templateId, vDCId,
               InstantiateVAppTemplateOptions.Builder.processorCount(minCores).memory(minMegs));
      tmClient.getVApp(vAppResponse.getId());
      logger.debug("<< instantiated VApp(%s)", vAppResponse.getId());

      logger.debug(">> deploying vApp(%s)", vAppResponse.getId());

      VApp vApp = blockUntilVAppStatusOrThrowException(vAppResponse, tmClient
               .deployVApp(vAppResponse.getId()), "deploy", VAppStatus.OFF);
      logger.debug("<< deployed vApp(%s)", vApp.getId());

      logger.debug(">> powering vApp(%s)", vApp.getId());
      vApp = blockUntilVAppStatusOrThrowException(vApp, tmClient.powerOnVApp(vApp.getId()),
               "powerOn", VAppStatus.ON);
      logger.debug("<< on vApp(%s)", vApp.getId());

      return vApp.getId();
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

   public ExecResponse exec(InetAddress address, String command) {
      InetSocketAddress sshSocket = new InetSocketAddress(address, 22);
      logger.debug(">> exec ssh://%s@%s/%s", username, sshSocket, command);
      ExecResponse exec = exec(sshSocket, username, password, command);
      logger.debug("<< output(%s) error(%s)", exec.getOutput(), exec.getError());
      return exec;
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
         logger.debug(">> powering off vApp(%s)", vApp.getId());
         blockUntilVAppStatusOrThrowException(vApp, tmClient.powerOffVApp(vApp.getId()),
                  "powerOff", VAppStatus.OFF);
         logger.debug("<< off vApp(%s)", vApp.getId());
      }
      logger.debug(">> deleting vApp(%s)", vApp.getId());
      tmClient.deleteVApp(id);
      logger.debug("<< deleted vApp(%s)", vApp.getId());
   }

   private ExecResponse exec(InetSocketAddress socket, String username, String password,
            String command) {
      if (!socketTester.apply(socket)) {
         throw new SocketNotOpenException(socket);
      }
      SshClient connection = sshFactory.create(socket, username, password);
      try {
         connection.connect();
         return connection.exec(command);
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   private VApp blockUntilVAppStatusOrThrowException(VApp vApp, Task deployTask, String taskType,
            VAppStatus expectedStatus) {
      if (!taskTester.apply(deployTask.getLocation())) {
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

   public static class SocketNotOpenException extends RuntimeException {

      private final InetSocketAddress socket;
      /** The serialVersionUID */
      private static final long serialVersionUID = 251801929573211256L;

      public SocketNotOpenException(InetSocketAddress socket) {
         super("socket not open: " + socket);
         this.socket = socket;
      }

      public InetSocketAddress getSocket() {
         return socket;
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
