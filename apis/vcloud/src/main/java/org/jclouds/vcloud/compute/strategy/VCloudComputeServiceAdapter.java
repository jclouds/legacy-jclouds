/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vcloud.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.ovf.Envelope;
import org.jclouds.util.Throwables2;
import org.jclouds.vcloud.TaskInErrorStateException;
import org.jclouds.vcloud.TaskStillRunningException;
import org.jclouds.vcloud.VCloudApi;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.suppliers.VAppTemplatesSupplier;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * defines the connection between the {@link org.jclouds.vcloud.VCloudApi} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class VCloudComputeServiceAdapter implements ComputeServiceAdapter<VApp, VAppTemplate, VAppTemplate, Location> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   protected final VCloudApi client;
   protected final Predicate<URI> successTester;
   protected final InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn booter;
   protected final Supplier<Map<String, Org>> nameToOrg;
   protected final Supplier<Set<VAppTemplate>> templates;
   protected final Function<VAppTemplate, Envelope> templateToEnvelope;

   @Inject
   protected VCloudComputeServiceAdapter(VCloudApi client, Predicate<URI> successTester,
            InstantiateVAppTemplateWithGroupEncodedIntoNameThenCustomizeDeployAndPowerOn booter,
            Supplier<Map<String, Org>> nameToOrg, VAppTemplatesSupplier templates,
            Function<VAppTemplate, Envelope> templateToEnvelope) {
      this.client = checkNotNull(client, "client");
      this.successTester = checkNotNull(successTester, "successTester");
      this.booter = checkNotNull(booter, "booter");
      this.nameToOrg = checkNotNull(nameToOrg, "nameToOrg");
      this.templates = checkNotNull(templates, "templates");
      this.templateToEnvelope = checkNotNull(templateToEnvelope, "templateToEnvelope");
   }

   @Override
   public NodeAndInitialCredentials<VApp> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {
      return booter.createNodeWithGroupEncodedIntoName(group, name, template);
   }

   @Override
   public Iterable<VAppTemplate> listHardwareProfiles() {
      return supportedTemplates();
   }

   private Iterable<VAppTemplate> supportedTemplates() {
      return filter(templates.get(), new Predicate<VAppTemplate>() {

         @Override
         public boolean apply(VAppTemplate from) {
            try {
               templateToEnvelope.apply(from);
            } catch (IllegalArgumentException e) {
               logger.warn("Unsupported: " + e.getMessage());
               return false;
            } catch (RuntimeException e) {
               IllegalArgumentException e2 = Throwables2.getFirstThrowableOfType(e, IllegalArgumentException.class);
               if (e2 != null) {
                  logger.warn("Unsupported: " + e2.getMessage());
                  return false;
               } else {
                  throw e;
               }
            }
            return true;
         }

      });
   }

   @Override
   public Iterable<VAppTemplate> listImages() {
      return supportedTemplates();
   }

   @Override
   public Iterable<VApp> listNodes() {
      // TODO: parallel or cache
      Builder<VApp> nodes = ImmutableSet.builder();
      for (Org org : nameToOrg.get().values()) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            for (ReferenceType resource : client.getVDCApi().getVDC(vdc.getHref()).getResourceEntities().values()) {
               if (resource.getType().equals(VCloudMediaType.VAPP_XML)) {
                  addVAppToSetRetryingIfNotYetPresent(nodes, vdc, resource);
               }
            }
         }
      }
      return nodes.build();
   }

   @Override
   public Iterable<VApp> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<VApp>() {

            @Override
            public boolean apply(VApp vm) {
               return contains(ids, vm.getHref().toASCIIString());
            }
         });
   }

   @VisibleForTesting
   void addVAppToSetRetryingIfNotYetPresent(Builder<VApp> nodes, ReferenceType vdc, ReferenceType resource) {
      VApp node = null;
      int i = 0;
      while (node == null && i++ < 3) {
         try {
            node = client.getVAppApi().getVApp(resource.getHref());
            nodes.add(node);
         } catch (NullPointerException e) {
            logger.warn("vApp %s not yet present in vdc %s", resource.getName(), vdc.getName());
         }
      }
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location>of();
   }

   @Override
   public VApp getNode(String in) {
      URI id = URI.create(in);
      return client.getVAppApi().getVApp(id);
   }
   
   @Override
   public VAppTemplate getImage(String in) {
      URI id = URI.create(in);
      return client.getVAppTemplateApi().getVAppTemplate(id);
   }
   
   @Override
   public void destroyNode(String id) {
      URI vappId = URI.create(checkNotNull(id, "node.id"));
      VApp vApp = cancelAnyRunningTasks(vappId);
      if (vApp.getStatus() != Status.OFF) {
         logger.debug(">> powering off VApp vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
         try {
            waitForTask(client.getVAppApi().powerOffVApp(vApp.getHref()));
            vApp = client.getVAppApi().getVApp(vApp.getHref());
            logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
         } catch (IllegalStateException e) {
            logger.warn(e, "<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
         }
         logger.debug(">> undeploying vApp(%s), current status: %s", vApp.getName(), vApp.getStatus());
         try {
            waitForTask(client.getVAppApi().undeployVApp(vApp.getHref()));
            vApp = client.getVAppApi().getVApp(vApp.getHref());
            logger.debug("<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
         } catch (IllegalStateException e) {
            logger.warn(e, "<< %s vApp(%s)", vApp.getStatus(), vApp.getName());
         }
      }
      logger.debug(">> deleting vApp(%s)", vApp.getHref());
      waitForTask(client.getVAppApi().deleteVApp(vApp.getHref()));
      logger.debug("<< deleted vApp(%s)", vApp.getHref());
   }

   VApp waitForPendingTasksToComplete(URI vappId) {
      VApp vApp = client.getVAppApi().getVApp(vappId);
      if (vApp.getTasks().size() == 0)
         return vApp;
      for (Task task : vApp.getTasks())
         waitForTask(task);
      return client.getVAppApi().getVApp(vappId);
   }

   VApp cancelAnyRunningTasks(URI vappId) {
      VApp vApp = client.getVAppApi().getVApp(vappId);
      if (vApp.getTasks().size() == 0)
         return vApp;
      for (Task task : vApp.getTasks()) {
         try {
            client.getTaskApi().cancelTask(task.getHref());
            waitForTask(task);
         } catch (TaskInErrorStateException e) {
         }
      }
      return client.getVAppApi().getVApp(vappId);

   }

   public void waitForTask(Task task) {
      if (!successTester.apply(task.getHref())) 
         throw new TaskStillRunningException(task);
   }

   @Override
   public void rebootNode(String in) {
      URI id = URI.create(checkNotNull(in, "node.id"));
      waitForTask(client.getVAppApi().resetVApp(id));
   }

   @Override
   public void resumeNode(String in) {
      URI id = URI.create(checkNotNull(in, "node.id"));
      waitForTask(client.getVAppApi().powerOnVApp(id));
   }

   @Override
   public void suspendNode(String in) {
      URI id = URI.create(checkNotNull(in, "node.id"));
      waitForTask(client.getVAppApi().powerOffVApp(id));
   }
}
