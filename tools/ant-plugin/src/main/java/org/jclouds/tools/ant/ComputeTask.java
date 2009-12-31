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
package org.jclouds.tools.ant;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceFactory;
import org.jclouds.compute.domain.CreateServerResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.domain.ServerIdentity;
import org.jclouds.compute.domain.ServerMetadata;
import org.jclouds.http.HttpUtils;
import org.jclouds.tools.ant.logging.config.AntLoggingModule;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.io.Resources;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * @author Ivan Meredith
 */
public class ComputeTask extends Task {
   private final Map<URI, ComputeService> computeMap;
   private static Project project;
   /**
    * we don't have a reference to the project during the constructor, so we need to defer expansion
    * with a Provider.
    */
   private static Provider<Module[]> defaultModulesProvider = new Provider<Module[]>() {

      @Override
      public Module[] get() {
         return new Module[] { new AntLoggingModule(project) };
      }

   };

   public ComputeTask(Map<URI, ComputeService> computeMap) {
      this.computeMap = computeMap;
   }

   public ComputeTask() throws IOException {
      this(buildComputeMap(loadDefaultProperties()));
   }

   static Properties loadDefaultProperties() throws IOException {
      Properties properties = new Properties();
      properties.load(Resources.newInputStreamSupplier(Resources.getResource("compute.properties"))
               .getInput());
      return properties;
   }

   static Map<URI, ComputeService> buildComputeMap(final Properties props) {
      return new MapMaker().makeComputingMap(new Function<URI, ComputeService>() {

         @Override
         public ComputeService apply(URI from) {
            return new ComputeServiceFactory(props).create(from, defaultModulesProvider.get());
         }

      });

   }

   public static enum Action {
      CREATE, GET, LIST, LIST_DETAILS, DESTROY
   }

   private String provider;
   private String action;
   private ServerElement serverElement;

   /**
    * @return the configured {@link ServerElement} element
    */
   public final ServerElement createServer() {
      if (getServer() == null) {
         this.serverElement = new ServerElement();
      }

      return this.serverElement;
   }

   public ServerElement getServer() {
      return this.serverElement;
   }

   public void execute() throws BuildException {
      ComputeTask.project = getProject();
      Action action = Action.valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE,
               this.action));
      ComputeService computeService = computeMap.get(HttpUtils.createUri(provider));
      switch (action) {
         case CREATE:
         case GET:
         case DESTROY:
            if (serverElement != null) {
               switch (action) {
                  case CREATE:
                     create(computeService);
                     break;
                  case GET:
                     get(computeService);
                     break;
                  case DESTROY:
                     destroy(computeService);
                     break;
               }
            } else {
               this.log("missing server element for action: " + action, Project.MSG_ERR);
            }
            break;
         case LIST:
            log("list");
            for (ServerIdentity server : computeService.listServers()) {
               log(String.format("   id=%s, name=%s", server.getId(), server.getName()));
            }
            break;
         case LIST_DETAILS:
            log("list details");
            for (ServerIdentity server : computeService.listServers()) {// TODO parallel
               logDetails(computeService, server);
            }
            break;
         default:
            this.log("bad action: " + action, Project.MSG_ERR);
      }
   }

   private void create(ComputeService computeService) {
      log(String.format("create name: %s, profile: %s, image: %s", serverElement.getName(),
               serverElement.getProfile(), serverElement.getImage()));
      CreateServerResponse createdServer = computeService.createServer(serverElement.getName(),
               Profile.valueOf(serverElement.getProfile().toUpperCase()), Image
                        .valueOf(serverElement.getImage().toUpperCase()));
      log(String.format("   id=%s, name=%s, connection=%s://%s:%s@%s:%d", createdServer.getId(),
               createdServer.getName(), createdServer.getLoginType().toString().toLowerCase(),
               createdServer.getCredentials().account, createdServer.getCredentials().key,
               createdServer.getPublicAddresses().first().getHostAddress(), createdServer
                        .getLoginPort()));
   }

   private void destroy(ComputeService computeService) {
      log(String.format("destroy name: %s", serverElement.getName()));
      SortedSet<ServerIdentity> serversThatMatch = computeService.getServerByName(serverElement
               .getName());
      if (serversThatMatch.size() > 0) {
         for (ServerIdentity server : serversThatMatch) {
            log(String.format("   destroying id=%s, name=%s", server.getId(), server.getName()));
            computeService.destroyServer(server.getId());
         }
      }
   }

   private void get(ComputeService computeService) {
      log(String.format("get name: %s", serverElement.getName()));
      SortedSet<ServerIdentity> serversThatMatch = computeService.getServerByName(serverElement
               .getName());
      if (serversThatMatch.size() > 0) {
         for (ServerIdentity server : serversThatMatch) {
            logDetails(computeService, server);
         }
      }
   }

   private void logDetails(ComputeService computeService, ServerIdentity server) {
      ServerMetadata metadata = computeService.getServerMetadata(server.getId());
      log(String.format("   server id=%s, name=%s, state=%s, publicIp=%s, privateIp=%s", metadata
               .getId(), server.getName(), metadata.getState(), ipOrEmptyString(metadata
               .getPublicAddresses()), ipOrEmptyString(metadata.getPrivateAddresses())));
   }

   public static String ipOrEmptyString(SortedSet<InetAddress> set) {
      if (set.size() > 0) {
         return set.last().getHostAddress();
      } else {
         return "";
      }
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }                                                                                                                     

   public ServerElement getServerElement() {
      return serverElement;
   }

   public void setServerElement(ServerElement serverElement) {
      this.serverElement = serverElement;
   }

   public void setProvider(String provider) {
      this.provider = provider;
   }

   public String getProvider() {
      return provider;
   }
}
