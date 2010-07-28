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
package org.jclouds.chef.servlet;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;
import static org.jclouds.chef.reference.ChefConstants.CHEF_NODE;
import static org.jclouds.chef.reference.ChefConstants.CHEF_ROLE;
import static org.jclouds.chef.reference.ChefConstants.CHEF_SERVICE_CLIENT;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.logging.Logger;
import org.jclouds.logging.jdk.JDKLogger;
import org.jclouds.rest.RestContextFactory;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Registers a new node in Chef and binds its name to
 * {@link ChefConstants.CHEF_NODE}, its role to {@link ChefConstants.CHEF_ROLE}
 * and the {@link ChefService} for the client to
 * {@link ChefConstants.CHEF_SERVICE_CLIENT} upon initialized. Deletes the node
 * and client when the context is destroyed.
 * 
 * @author Adrian Cole
 */
public class ChefRegistrationListener implements ServletContextListener {

   private Logger logger = new JDKLogger.JDKLoggerFactory().getLogger(ChefRegistrationListener.class.getName());

   @SuppressWarnings("unchecked")
   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      try {
         logger.debug("starting initialization");
         Properties overrides = new Properties();
         Enumeration<String> e = servletContextEvent.getServletContext().getInitParameterNames();
         while (e.hasMoreElements()) {
            String propertyName = e.nextElement();
            overrides.setProperty(propertyName, servletContextEvent.getServletContext().getInitParameter(propertyName));

         }
         String role = getInitParam(servletContextEvent, CHEF_ROLE);

         logger.trace("creating validator connection");

         ChefService validatorService = createService(overrides);
         logger.debug("created validator connection");

         ChefService clientService = null;
         String nodeName;
         try {
            while (true) {
               nodeName = findNextClientAndNodeName(validatorService, role);
               try {
                  clientService = createClientAndNode(validatorService, role, nodeName, overrides);
                  break;
               } catch (IllegalStateException ex) {
                  logger.debug("node or client already exists %s: %s", nodeName, ex.getMessage());
               }
            }
         } finally {
            validatorService.getContext().close();
         }
         servletContextEvent.getServletContext().setAttribute(CHEF_NODE, nodeName);
         servletContextEvent.getServletContext().setAttribute(CHEF_ROLE, role);
         servletContextEvent.getServletContext().setAttribute(CHEF_SERVICE_CLIENT, clientService);
         logger.debug("initialized");
      } catch (RuntimeException e) {
         logger.error(e, "error registering");
         throw e;
      }
   }

   private String findNextClientAndNodeName(ChefService validatorService, String prefix) {
      ListenableFuture<Set<String>> nodes = validatorService.getContext().getAsyncApi().listNodes();
      ListenableFuture<Set<String>> clients = validatorService.getContext().getAsyncApi().listClients();
      try {
         String nodeName;
         Set<String> names = newHashSet(concat(nodes.get(), clients.get()));
         int index = 0;
         while (true) {
            nodeName = prefix + "-" + index++;
            if (!names.contains(nodeName))
               break;
         }
         logger.trace("nodeName %s not in %s", nodeName, names);
         return nodeName;
      } catch (InterruptedException e) {
         propagate(e);
         return null;
      } catch (ExecutionException e) {
         propagate(e);
         return null;
      }
   }

   private ChefService createClientAndNode(ChefService validatorClient, String role, String id, Properties overrides) {
      logger.trace("attempting to create client %s", id);
      String clientKey = validatorClient.getContext().getApi().createClient(id);
      logger.debug("created client %s", id);
      ChefService clientService = null;
      try {
         Properties clientProperties = new Properties();
         clientProperties.putAll(overrides);
         removeCredentials(clientProperties);
         clientProperties.setProperty("chef.identity", id);
         clientProperties.setProperty("chef.credential", clientKey);
         clientService = createService(clientProperties);
         clientService.createNodeAndPopulateAutomaticAttributes(id, singleton("role[" + role + "]"));
         return clientService;
      } catch (RuntimeException e) {
         logger.error(e, "error creating node %s", id);
         throw e;
      }
   }

   private void removeCredentials(Properties clientProperties) {
      for (Entry<Object, Object> entry : clientProperties.entrySet()) {
         if (entry.getKey().toString().indexOf("credential") != -1)
            clientProperties.remove(entry.getKey());
      }
   }

   private ChefService createService(Properties props) {
      return ((ChefContext) new RestContextFactory().<ChefClient, ChefAsyncClient> createContext("chef", props))
            .getChefService();
   }

   private static String getInitParam(ServletContextEvent servletContextEvent, String name) {
      return checkNotNull(servletContextEvent.getServletContext().getInitParameter(name));
   }

   @SuppressWarnings("unchecked")
   private static <T> T getContextAttributeOrNull(ServletContextEvent servletContextEvent, String name) {
      return (T) servletContextEvent.getServletContext().getAttribute(name);
   }

   /**
    * removes the node and client if found, and closes the client context.
    */
   @Override
   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      ChefService clientService = getContextAttributeOrNull(servletContextEvent, CHEF_SERVICE_CLIENT);
      String nodename = getContextAttributeOrNull(servletContextEvent, CHEF_NODE);
      if (nodename != null && clientService != null) {
         clientService.deleteAllClientsAndNodesInList(singleton(nodename));
      }
      if (clientService != null) {
         clientService.getContext().close();
      }
   }
}