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
package org.jclouds.tools.ant.taskdefs.compute;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Profile;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.http.HttpUtils;
import org.jclouds.tools.ant.logging.config.AntLoggingModule;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.io.Files;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class ComputeTask extends Task {

   private final Map<URI, ComputeServiceContext<?, ?>> computeMap;
   private static Project project;
   /**
    * we don't have a reference to the project during the constructor, so we need to defer expansion
    * with a Provider.
    */
   private static Provider<Iterable<? extends Module>> defaultModulesProvider = new Provider<Iterable<? extends Module>>() {

      @Override
      public Iterable<Module> get() {
         return ImmutableSet.of((Module) new AntLoggingModule(project,
                  ComputeServiceConstants.COMPUTE_LOGGER));
      }

   };

   /**
    * we don't have a reference to the project during the constructor, so we need to defer expansion
    * with a Provider.
    */
   private static Provider<Properties> defaultPropertiesProvider = new Provider<Properties>() {

      @SuppressWarnings("unchecked")
      @Override
      public Properties get() {
         Properties props = new Properties();
         props.putAll(project.getProperties());
         return props;
      }

   };

   public ComputeTask(Map<URI, ComputeServiceContext<?, ?>> computeMap) {
      this.computeMap = computeMap;
   }

   public ComputeTask() throws IOException {
      this(buildComputeMap());
   }

   static Map<URI, ComputeServiceContext<?, ?>> buildComputeMap() {
      return new MapMaker().makeComputingMap(new Function<URI, ComputeServiceContext<?, ?>>() {

         @Override
         public ComputeServiceContext<?, ?> apply(URI from) {
            try {
               return new ComputeServiceContextFactory().createContext(from, defaultModulesProvider
                        .get(), defaultPropertiesProvider.get());
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }

      });

   }

   public static enum Action {
      CREATE, GET, LIST, LIST_DETAILS, DESTROY
   }

   private String provider;
   private String action;
   private NodeElement nodeElement;

   /**
    * @return the configured {@link NodeElement} element
    */
   public final NodeElement createNode() {
      if (getNode() == null) {
         this.nodeElement = new NodeElement();
      }

      return this.nodeElement;
   }

   public NodeElement getNode() {
      return this.nodeElement;
   }

   public void execute() throws BuildException {
      ComputeTask.project = getProject();
      Action action = Action.valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE,
               this.action));
      ComputeServiceContext<?, ?> context = computeMap.get(HttpUtils.createUri(provider));
      try {
         ComputeService computeService = context.getComputeService();
         switch (action) {
            case CREATE:
            case GET:
            case DESTROY:
               if (nodeElement != null) {
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
                  this.log("missing node element for action: " + action, Project.MSG_ERR);
               }
               break;
            case LIST:
               log("list");
               for (ComputeMetadata node : computeService.listNodes()) {
                  log(String.format("   location=%s, id=%s, name=%s", node.getLocation(), node
                           .getId(), node.getName()));
               }
               break;
            case LIST_DETAILS:
               log("list details");
               for (ComputeMetadata node : computeService.listNodes()) {// TODO
                  // parallel
                  logDetails(computeService, node);
               }
               break;
            default:
               this.log("bad action: " + action, Project.MSG_ERR);
         }
      } finally {
         context.close();
      }
   }

   private void create(ComputeService computeService) {
      log(String.format("create name: %s, profile: %s, image: %s", nodeElement.getName(),
               nodeElement.getProfile(), nodeElement.getImage()));
      CreateNodeResponse createdNode = computeService.startNodeInLocation(
               nodeElement.getLocation(), nodeElement.getName(), Profile.valueOf(nodeElement
                        .getProfile().toUpperCase()), Image.valueOf(nodeElement.getImage()
                        .toUpperCase()));
      log(String.format("   id=%s, name=%s, connection=%s://%s:%s@%s:%d", createdNode.getId(),
               createdNode.getName(), createdNode.getLoginType().toString().toLowerCase(),
               createdNode.getCredentials().account, createdNode.getCredentials().key, createdNode
                        .getPublicAddresses().first().getHostAddress(), createdNode.getLoginPort()));
      if (nodeElement.getIdproperty() != null)
         getProject().setProperty(nodeElement.getIdproperty(), createdNode.getId());
      if (nodeElement.getHostproperty() != null)
         getProject().setProperty(nodeElement.getHostproperty(),
                  createdNode.getPublicAddresses().first().getHostAddress());
      if (nodeElement.getKeyfile() != null
               && createdNode.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----"))
         try {
            Files.write(createdNode.getCredentials().key, new File(nodeElement.getKeyfile()),
                     Charset.defaultCharset());
         } catch (IOException e) {
            throw new BuildException(e);
         }
      if (nodeElement.getPasswordproperty() != null
               && !createdNode.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----"))
         getProject().setProperty(nodeElement.getPasswordproperty(),
                  createdNode.getCredentials().key);
      if (nodeElement.getUsernameproperty() != null)
         getProject().setProperty(nodeElement.getUsernameproperty(),
                  createdNode.getCredentials().account);
   }

   private void destroy(ComputeService computeService) {
      log(String.format("destroy name: %s", nodeElement.getName()));
      Iterable<ComputeMetadata> nodesThatMatch = ComputeUtils.filterByName(computeService
               .listNodes(), nodeElement.getName());
      for (ComputeMetadata node : nodesThatMatch) {
         log(String.format("   destroying id=%s, name=%s", node.getId(), node.getName()));
         computeService.destroyNode(node);
      }
   }

   private void get(ComputeService computeService) {
      log(String.format("get name: %s", nodeElement.getName()));
      Iterable<ComputeMetadata> nodesThatMatch = ComputeUtils.filterByName(computeService
               .listNodes(), nodeElement.getName());
      for (ComputeMetadata node : nodesThatMatch) {
         logDetails(computeService, node);
      }
   }

   private void logDetails(ComputeService computeService, ComputeMetadata node) {
      NodeMetadata metadata = computeService.getNodeMetadata(node);
      log(String
               .format(
                        "   node id=%s, name=%s, location=%s, state=%s, publicIp=%s, privateIp=%s, extra=%s",
                        metadata.getId(), node.getName(), node.getLocation(), metadata.getState(),
                        ipOrEmptyString(metadata.getPublicAddresses()), ipOrEmptyString(metadata
                                 .getPrivateAddresses()), metadata.getExtra()));
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

   public NodeElement getNodeElement() {
      return nodeElement;
   }

   public void setNodeElement(NodeElement nodeElement) {
      this.nodeElement = nodeElement;
   }

   public void setProvider(String provider) {
      this.provider = provider;
   }

   public String getProvider() {
      return provider;
   }
}
