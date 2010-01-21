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
package org.jclouds.tools.ant.taskdefs.compute;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.RunNodeOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.tools.ant.logging.config.AntLoggingModule;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.io.Files;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * 
 * @author Adrian Cole
 */
public class ComputeTaskUtils {

   /**
    * 
    * Creates a Map that associates a uri with a live connection to the compute provider. This is
    * done on-demand.
    * 
    * @param projectProvider
    *           allows access to the ant project to retrieve default properties needed for compute
    *           providers.
    */
   static Map<URI, ComputeServiceContext> buildComputeMap(final Provider<Project> projectProvider) {
      return new MapMaker().makeComputingMap(new Function<URI, ComputeServiceContext>() {

         @SuppressWarnings("unchecked")
         @Override
         public ComputeServiceContext apply(URI from) {
            try {
               Properties props = new Properties();
               props.putAll(projectProvider.get().getProperties());
               return new ComputeServiceContextFactory().createContext(from, ImmutableSet.of(
                        (Module) new AntLoggingModule(projectProvider.get(),
                                          ComputeServiceConstants.COMPUTE_LOGGER),
                                          new JschSshClientModule()),
                        props);
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
         }

      });

   }

   static Template createTemplateFromElement(NodeElement nodeElement, ComputeService computeService) {
      Template template = computeService.createTemplateInLocation(nodeElement.getLocation());
      template.os(OperatingSystem.valueOf(nodeElement.getOs()));
      addSizeFromElementToTemplate(nodeElement, template);
      return template;
   }

   static void addSizeFromElementToTemplate(NodeElement nodeElement, Template template) {
      if (nodeElement.getSize().equalsIgnoreCase("smallest")) {
         template.smallest();
      } else if (nodeElement.getSize().equalsIgnoreCase("fastest")) {
         template.fastest();
      } else if (nodeElement.getSize().equalsIgnoreCase("biggest")) {
         template.biggest();
      } else {
         throw new BuildException("size: " + nodeElement.getSize()
                  + " not supported.  valid sizes are smallest, fastest, biggest");
      }
   }

   static RunNodeOptions getNodeOptionsFromElement(NodeElement nodeElement) {
      RunNodeOptions options = new RunNodeOptions()
               .openPorts(getPortsToOpenFromElement(nodeElement));
      addRunScriptToOptionsIfPresentInNodeElement(nodeElement, options);
      return options;
   }

   static void addRunScriptToOptionsIfPresentInNodeElement(NodeElement nodeElement,
            RunNodeOptions options) {
      if (nodeElement.getRunscript() != null)
         try {
            options.runScript(Files.toByteArray(nodeElement.getRunscript()));
         } catch (IOException e) {
            throw new BuildException(e);
         }
   }

   static String ipOrEmptyString(SortedSet<InetAddress> set) {
      if (set.size() > 0) {
         return set.last().getHostAddress();
      } else {
         return "";
      }
   }

   static int[] getPortsToOpenFromElement(NodeElement nodeElement) {
      Iterable<String> portStrings = Splitter.on(',').split(nodeElement.getOpenports());
      int[] ports = new int[Iterables.size(portStrings)];
      int i = 0;
      for (String port : portStrings) {
         ports[i++] = Integer.parseInt(port);
      }
      return ports;
   }
}
