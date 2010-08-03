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
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;
import static org.jclouds.chef.reference.ChefConstants.CHEF_NODE;
import static org.jclouds.chef.reference.ChefConstants.CHEF_ROLE;
import static org.jclouds.chef.reference.ChefConstants.CHEF_SERVICE_CLIENT;

import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.servlet.functions.InitParamsToProperties;
import org.jclouds.logging.Logger;
import org.jclouds.logging.jdk.JDKLogger;
import org.jclouds.rest.RestContextFactory;

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

   @Override
   public void contextInitialized(ServletContextEvent servletContextEvent) {
      try {
         logger.debug("starting initialization");
         Properties overrides = InitParamsToProperties.INSTANCE.apply(servletContextEvent);
         String role = getInitParam(servletContextEvent, CHEF_ROLE);

         logger.trace("creating client connection");

         ChefService client = createService(overrides);
         logger.debug("created client connection");

         String nodeName;
         try {
            while (true) {
               nodeName = findNextNodeName(client, role);
               try {
                  client.createNodeAndPopulateAutomaticAttributes(nodeName, singleton("role[" + role + "]"));
                  break;
               } catch (IllegalStateException ex) {
                  logger.debug("client already exists %s: %s", nodeName, ex.getMessage());
               }
            }
         } finally {
            client.getContext().close();
         }
         servletContextEvent.getServletContext().setAttribute(CHEF_NODE, nodeName);
         servletContextEvent.getServletContext().setAttribute(CHEF_ROLE, role);
         servletContextEvent.getServletContext().setAttribute(CHEF_SERVICE_CLIENT, client);
         logger.debug("initialized");
      } catch (RuntimeException e) {
         logger.error(e, "error registering");
         throw e;
      }
   }

   private String findNextNodeName(ChefService client, String prefix) {
      Set<String> nodes = client.getContext().getApi().listNodes();
      String nodeName;
      Set<String> names = newHashSet(nodes);
      int index = 0;
      while (true) {
         nodeName = prefix + "-" + index++;
         if (!names.contains(nodeName))
            break;
      }
      return nodeName;
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
      ChefService client = getContextAttributeOrNull(servletContextEvent, CHEF_SERVICE_CLIENT);
      String nodename = getContextAttributeOrNull(servletContextEvent, CHEF_NODE);
      if (nodename != null && client != null) {
         client.deleteAllNodesInList(singleton(nodename));
      }
      if (client != null) {
         client.getContext().close();
      }
   }
}