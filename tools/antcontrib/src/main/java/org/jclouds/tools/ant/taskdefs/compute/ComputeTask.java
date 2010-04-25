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

import static org.jclouds.compute.util.ComputeUtils.isKeyAuth;
import static org.jclouds.tools.ant.taskdefs.compute.ComputeTaskUtils.buildComputeMap;
import static org.jclouds.tools.ant.taskdefs.compute.ComputeTaskUtils.createTemplateFromElement;
import static org.jclouds.tools.ant.taskdefs.compute.ComputeTaskUtils.ipOrEmptyString;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpUtils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Splitter;
import com.google.inject.Provider;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class ComputeTask extends Task {

   private final Map<URI, ComputeServiceContext> computeMap;
   private String provider;
   private String actions;
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
      CREATE, GET, LIST, LIST_DETAILS, DESTROY, LIST_IMAGES, LIST_SIZES, LIST_LOCATIONS
   }

   /**
    * makes a connection to the compute service and invokes
    */
   public void execute() throws BuildException {
      ComputeServiceContext context = computeMap.get(HttpUtils.createUri(provider));

      try {
         for (String action : Splitter.on(',').split(actions)) {
            Action act = Action.valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE,
                     action));
            invokeActionOnService(act, context.getComputeService());
         }
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
         case LIST_IMAGES:
            listImages(computeService);
            break;
         case LIST_SIZES:
            listSizes(computeService);
            break;
         case LIST_LOCATIONS:
            listLocations(computeService);
            break;
         default:
            this.log("bad action: " + action, Project.MSG_ERR);
      }
   }

   private void listDetails(ComputeService computeService) {
      log("list details");
      for (ComputeMetadata node : computeService.getNodes().values()) {// TODO
         // parallel
         logDetails(computeService, node);
      }
   }

   private void listImages(ComputeService computeService) {
      log("list images");
      for (Image image : computeService.getImages().values()) {// TODO
         log(String
                  .format(
                           "   image location=%s, id=%s, name=%s, version=%s, arch=%s, osfam=%s, osdesc=%s, desc=%s",
                           image.getLocation(), image.getId(), image.getName(), image.getVersion(),
                           image.getArchitecture(), image.getOsFamily(), image.getOsDescription(),
                           image.getDescription()));
      }
   }

   private void listSizes(ComputeService computeService) {
      log("list sizes");
      for (Size size : computeService.getSizes().values()) {// TODO
         log(String.format("   size id=%s, cores=%s, ram=%s, disk=%s", size.getId(), size
                  .getCores(), size.getRam(), size.getDisk()));
      }
   }

   private void listLocations(ComputeService computeService) {
      log("list locations");
      for (Location location : computeService.getLocations().values()) {// TODO
         log(String.format("   location id=%s, scope=%s, description=%s, parent=%s", location
                  .getId(), location.getScope(), location.getDescription(), location.getParent()));
      }
   }

   private void list(ComputeService computeService) {
      log("list");
      for (ComputeMetadata node : computeService.getNodes().values()) {
         log(String.format("   location=%s, id=%s, tag=%s", node.getLocation(), node.getId(), node
                  .getName()));
      }
   }

   private void create(ComputeService computeService) {
      String tag = nodeElement.getTag();

      log(String.format("create tag: %s, count: %d, size: %s, os: %s", tag, nodeElement.getCount(),
               nodeElement.getSize(), nodeElement.getOs()));

      Template template = createTemplateFromElement(nodeElement, computeService);

      for (NodeMetadata createdNode : computeService.runNodesWithTag(tag, nodeElement.getCount(),
               template).values()) {
         logDetails(computeService, createdNode);
         addNodeDetailsAsProjectProperties(createdNode);
      }
   }

   private void addNodeDetailsAsProjectProperties(NodeMetadata createdNode) {
      if (nodeElement.getIdproperty() != null)
         getProject().setProperty(nodeElement.getIdproperty(), createdNode.getId());
      if (nodeElement.getHostproperty() != null)
         getProject().setProperty(nodeElement.getHostproperty(),
                  ipOrEmptyString(createdNode.getPublicAddresses()));
      if (nodeElement.getPasswordproperty() != null && !isKeyAuth(createdNode))
         getProject().setProperty(nodeElement.getPasswordproperty(),
                  createdNode.getCredentials().key);
      if (nodeElement.getUsernameproperty() != null)
         getProject().setProperty(nodeElement.getUsernameproperty(),
                  createdNode.getCredentials().account);
   }

   private void destroy(ComputeService computeService) {
      log(String.format("destroy tag: %s", nodeElement.getTag()));
      computeService.destroyNodesWithTag(nodeElement.getTag());
   }

   private void get(ComputeService computeService) {
      log(String.format("get tag: %s", nodeElement.getTag()));
      for (ComputeMetadata node : computeService.getNodesWithTag(nodeElement.getTag()).values()) {
         logDetails(computeService, node);
      }
   }

   private void logDetails(ComputeService computeService, ComputeMetadata node) {
      NodeMetadata metadata = node instanceof NodeMetadata ? NodeMetadata.class.cast(node)
               : computeService.getNodeMetadata(node);
      log(String
               .format(
                        "   node id=%s, name=%s, tag=%s, location=%s, state=%s, publicIp=%s, privateIp=%s, extra=%s",
                        metadata.getId(), metadata.getName(), metadata.getTag(), metadata
                                 .getLocation(), metadata.getState(), ComputeTaskUtils
                                 .ipOrEmptyString(metadata.getPublicAddresses()),
                        ipOrEmptyString(metadata.getPrivateAddresses()), metadata.getExtra()));
   }

   /**
    * @return the configured {@link NodeElement} element
    */
   public final NodeElement createNodes() {
      if (getNodes() == null) {
         this.nodeElement = new NodeElement();
      }
      return this.nodeElement;
   }

   public NodeElement getNodes() {
      return this.nodeElement;
   }

   public String getActions() {
      return actions;
   }

   public void setActions(String actions) {
      this.actions = actions;
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
