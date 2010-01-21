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

import static org.jclouds.compute.util.ComputeUtils.filterByName;
import static org.jclouds.compute.util.ComputeUtils.isKeyAuth;
import static org.jclouds.tools.ant.taskdefs.compute.ComputeTaskUtils.buildComputeMap;
import static org.jclouds.tools.ant.taskdefs.compute.ComputeTaskUtils.createTemplateFromElement;
import static org.jclouds.tools.ant.taskdefs.compute.ComputeTaskUtils.getNodeOptionsFromElement;
import static org.jclouds.tools.ant.taskdefs.compute.ComputeTaskUtils.ipOrEmptyString;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.RunNodeOptions;
import org.jclouds.http.HttpUtils;

import com.google.common.base.CaseFormat;
import com.google.common.io.Files;
import com.google.inject.Provider;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class ComputeTask extends Task {

   private final Map<URI, ComputeServiceContext> computeMap;
   private String provider;
   private String action;
   private NodeElement nodeElement;

   /**
    * we don't have a reference to the project during the constructor, so we need to defer expansion
    * with a Provider.
    */
   private final Provider<Project> projectProvider = new Provider<Project>() {
      @Override
      public Project get() {
         return getProject();
      }
   };

   public ComputeTask(@Nullable Map<URI, ComputeServiceContext> computeMap) {
      this.computeMap = computeMap != null ? computeMap : buildComputeMap(projectProvider);
   }

   public ComputeTask() throws IOException {
      this(null);
   }

   public static enum Action {
      CREATE, GET, LIST, LIST_DETAILS, DESTROY
   }

   /**
    * makes a connection to the compute service and invokes
    */
   public void execute() throws BuildException {
      ComputeServiceContext context = computeMap.get(HttpUtils.createUri(provider));
      Action action = Action.valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE,
               this.action));
      try {
         invokeActionOnService(action, context.getComputeService());
      } finally {
         context.close();
      }
   }

   private void invokeActionOnService(Action action, ComputeService computeService) {
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
            list(computeService);
            break;
         case LIST_DETAILS:
            listDetails(computeService);
            break;
         default:
            this.log("bad action: " + action, Project.MSG_ERR);
      }
   }

   private void listDetails(ComputeService computeService) {
      log("list details");
      for (ComputeMetadata node : computeService.listNodes()) {// TODO
         // parallel
         logDetails(computeService, node);
      }
   }

   private void list(ComputeService computeService) {
      log("list");
      for (ComputeMetadata node : computeService.listNodes()) {
         log(String.format("   location=%s, id=%s, name=%s", node.getLocation(), node.getId(), node
                  .getName()));
      }
   }

   private void create(ComputeService computeService) {
      String name = nodeElement.getName();

      log(String.format("create name: %s, size: %s, os: %s", name, nodeElement.getSize(),
               nodeElement.getOs()));

      Template template = createTemplateFromElement(nodeElement, computeService);

      RunNodeOptions options = getNodeOptionsFromElement(nodeElement);

      CreateNodeResponse createdNode = computeService.runNode(name, template, options);

      logNodeDetails(createdNode);

      addNodeDetailsAsProjectProperties(createdNode);
   }

   private void logNodeDetails(CreateNodeResponse createdNode) {
      log(String.format("   id=%s, name=%s, connection=%s://%s:%s@%s:%d", createdNode.getId(),
               createdNode.getName(), createdNode.getLoginType().toString().toLowerCase(),
               createdNode.getCredentials().account, createdNode.getCredentials().key, createdNode
                        .getPublicAddresses().first().getHostAddress(), createdNode.getLoginPort()));
   }

   private void addNodeDetailsAsProjectProperties(CreateNodeResponse createdNode) {
      if (nodeElement.getIdproperty() != null)
         getProject().setProperty(nodeElement.getIdproperty(), createdNode.getId());
      if (nodeElement.getHostproperty() != null)
         getProject().setProperty(nodeElement.getHostproperty(),
                  createdNode.getPublicAddresses().first().getHostAddress());
      if (nodeElement.getKeyfile() != null && isKeyAuth(createdNode))
         try {
            Files.write(createdNode.getCredentials().key, new File(nodeElement.getKeyfile()),
                     Charset.defaultCharset());
         } catch (IOException e) {
            throw new BuildException(e);
         }
      if (nodeElement.getPasswordproperty() != null && !isKeyAuth(createdNode))
         getProject().setProperty(nodeElement.getPasswordproperty(),
                  createdNode.getCredentials().key);
      if (nodeElement.getUsernameproperty() != null)
         getProject().setProperty(nodeElement.getUsernameproperty(),
                  createdNode.getCredentials().account);
   }

   private void destroy(ComputeService computeService) {
      log(String.format("destroy name: %s", nodeElement.getName()));
      Iterable<? extends ComputeMetadata> nodesThatMatch = filterByName(computeService.listNodes(),
               nodeElement.getName());
      for (ComputeMetadata node : nodesThatMatch) {
         log(String.format("   destroying id=%s, name=%s", node.getId(), node.getName()));
         computeService.destroyNode(node);
      }
   }

   private void get(ComputeService computeService) {
      log(String.format("get name: %s", nodeElement.getName()));
      Iterable<? extends ComputeMetadata> nodesThatMatch = filterByName(computeService.listNodes(),
               nodeElement.getName());
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
