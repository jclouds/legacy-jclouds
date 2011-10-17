/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.virtualbox.experiment;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.virtualbox.config.VirtualBoxConstants;
import org.jclouds.virtualbox.functions.StartVBoxIfNotAlreadyRunning;
import org.jclouds.virtualbox.functions.admin.StartJettyIfNotAlreadyRunning;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeSuite;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class SetupVirtualBoxForLiveTest {

   private String provider = "virtualbox";
   private URI endpoint;
   private String apiVersion;

   private String workingDir;

   private URI gaIsoUrl;

   private String gaIsoName;
   private URI distroIsoUrl;
   private String distroIsoName;

   private ComputeServiceContext context;
   private String hostId = "host";
   private String guestId = "guest";
   private String majorVersion;
   private URI vboxDmg;
   private String vboxVersionName;
   private String basebaseResource;
   private String port;
  
   private String vboxWebServerCredential;
   private String vboxWebServerIdentity;

   public void setupCredentials() {
      endpoint = URI.create(System.getProperty("test." + provider + ".endpoint", "http://localhost:18083/"));
      apiVersion = System.getProperty("test." + provider + ".apiversion", "4.1.2r73507");
      majorVersion = Iterables.get(Splitter.on('r').split(apiVersion), 0);
   }

   public Logger logger() {
      return context.utils().loggerFactory().getLogger("jclouds.compute");
   }

   public void setupConfigurationProperties() {
      workingDir = System.getProperty("user.home") + File.separator
               + System.getProperty("test." + provider + ".workingDir", "jclouds-virtualbox-test");
      if (new File(workingDir).mkdir())
         ;
      gaIsoName = System.getProperty("test." + provider + ".gaIsoName", "VBoxGuestAdditions_" + majorVersion + ".iso");
      gaIsoUrl = URI.create(System.getProperty("test." + provider + ".gaIsoUrl",
               "http://download.virtualbox.org/virtualbox/" + majorVersion + "/" + gaIsoName));

      distroIsoName = System.getProperty("test." + provider + ".distroIsoName", "ubuntu-11.04-server-i386.iso");
      distroIsoUrl = URI.create(System.getProperty("test." + provider + ".distroIsoUrl",
               "http://releases.ubuntu.com/11.04/ubuntu-11.04-server-i386.iso"));
      vboxDmg = URI.create(System.getProperty("test." + provider + ".vboxDmg",
               "http://download.virtualbox.org/virtualbox/4.1.2/VirtualBox-4.1.2-73507-OSX.dmg"));
      vboxVersionName = System.getProperty("test" + provider + ".vboxVersionName", "VirtualBox-4.1.2-73507-OSX.dmg");
      basebaseResource = System.getProperty(VirtualBoxConstants.VIRTUALBOX_JETTY_BASE_RESOURCE, ".");
      port = System.getProperty(VirtualBoxConstants.VIRTUALBOX_JETTY_PORT, "8080");
      vboxWebServerIdentity = System.getProperty(VirtualBoxConstants.VIRTUALBOX_WEBSERVER_IDENTITY, "toor");
      vboxWebServerCredential = System.getProperty(VirtualBoxConstants.VIRTUALBOX_WEBSERVER_CREDENTIAL, "12345");
   }

   @BeforeSuite
   public void setupClient() throws Exception {
      context = TestUtils.computeServiceForLocalhostAndGuest();
      setupCredentials();
      setupConfigurationProperties();
      downloadFileUnlessPresent(distroIsoUrl, workingDir, distroIsoName);
      downloadFileUnlessPresent(gaIsoUrl, workingDir, gaIsoName);

      installVbox();
      checkVboxVersionExpected();
      new StartVBoxIfNotAlreadyRunning(context, hostId, new Credentials(vboxWebServerIdentity, vboxWebServerCredential));
      new StartJettyIfNotAlreadyRunning(port).apply(basebaseResource);
   }
   
   @AfterSuite
   public void stopVboxWebServer() throws IOException {
      runScriptOnNode(hostId, "pidof vboxwebsrv | xargs kill");
   }

   public void installVbox() throws Exception {
      if (runScriptOnNode(hostId, "VBoxManage --version", runAsRoot(false).wrapInInitScript(false)).getExitCode() != 0) {
         logger().debug("installing virtualbox");
         if (isOSX(hostId)) {
            downloadFileUnlessPresent(vboxDmg, workingDir, vboxVersionName);
            runScriptOnNode(hostId, "hdiutil attach " + workingDir + "/" + vboxVersionName);
            runScriptOnNode(hostId,
                     "installer -pkg /Volumes/VirtualBox/VirtualBox.mpkg -target /Volumes/Macintosh\\ HD");
         } else {
            // TODO other platforms
            runScriptOnNode(hostId, "cat > /etc/apt/sources.list.d/TODO");
            runScriptOnNode(hostId,
                     "wget -q http://download.virtualbox.org/virtualbox/debian/oracle_vbox.asc -O- | apt-key add -");
            runScriptOnNode(hostId, "apt-get update");
            runScriptOnNode(hostId, "apt-get --yes install virtualbox-4.1");
         }
      }
   }

   public  void checkVboxVersionExpected() throws IOException, InterruptedException {
      logger().debug("checking virtualbox version");
      assertEquals(runScriptOnNode(hostId, "VBoxManage -version").getOutput().trim(), apiVersion);
   }

   public boolean isOSX(String id) {
      return context.getComputeService().getNodeMetadata(hostId).getOperatingSystem().getDescription().equals(
               "Mac OS X");
   }

   public  File downloadFileUnlessPresent(URI sourceURL, String destinationDir, String filename) throws Exception {

      File iso = new File(destinationDir, filename);

      if (!iso.exists()) {
         InputStream is = context.utils().http().get(sourceURL);
         checkNotNull(is, "%s not found", sourceURL);
         try {
            ByteStreams.copy(is, new FileOutputStream(iso));
         } finally {
            Closeables.closeQuietly(is);
         }
      }
      return iso;
   }

   public ExecResponse runScriptOnNode(String nodeId, String command, RunScriptOptions options) {
      ExecResponse toReturn = context.getComputeService().runScriptOnNode(nodeId, command, options);
      assert toReturn.getExitCode() == 0 : toReturn;
      return toReturn;
   }

   public ExecResponse runScriptOnNode(String nodeId, String command) {
      return runScriptOnNode(nodeId, command, wrapInInitScript(false));
   }

}