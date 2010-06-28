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

import static org.jclouds.rest.RestContextFactory.getPropertiesFromResource;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.tools.ant.logging.config.AntLoggingModule;

import com.google.common.base.Charsets;
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
            Properties props = getPropertiesFromResource("compute.properties");
            props.putAll(projectProvider.get().getProperties());
            // adding the properties to the factory will allow us to pass alternate endpoints
            String provider = from.getHost();
            Credentials creds = Credentials.parse(from);
            return new ComputeServiceContextFactory(props).createContext(provider,
                     creds.identity, creds.credential, ImmutableSet.of((Module) new AntLoggingModule(
                              projectProvider.get(), ComputeServiceConstants.COMPUTE_LOGGER),
                              new JschSshClientModule()), props);

         }

      });

   }

   static Template createTemplateFromElement(NodeElement nodeElement, ComputeService computeService) {
      TemplateBuilder templateBuilder = computeService.templateBuilder();
      if (nodeElement.getLocation() != null && !"".equals(nodeElement.getLocation()))
         templateBuilder.locationId(nodeElement.getLocation());
      if (nodeElement.getImage() != null && !"".equals(nodeElement.getImage())) {
         final String imageId = nodeElement.getImage();
         try {
            templateBuilder.imageId(imageId);
         } catch (NoSuchElementException e) {
            throw new BuildException("image not found " + nodeElement.getImage());
         }
      } else {
         templateBuilder.osFamily(OsFamily.valueOf(nodeElement.getOs()));
      }
      addSizeFromElementToTemplate(nodeElement, templateBuilder);
      templateBuilder.options(getNodeOptionsFromElement(nodeElement));

      return templateBuilder.build();
   }

   static void addSizeFromElementToTemplate(NodeElement nodeElement, TemplateBuilder template) {
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

   static TemplateOptions getNodeOptionsFromElement(NodeElement nodeElement) {
      TemplateOptions options = new TemplateOptions()
               .inboundPorts(getPortsToOpenFromElement(nodeElement));
      addRunScriptToOptionsIfPresentInNodeElement(nodeElement, options);
      addPrivateKeyToOptionsIfPresentInNodeElement(nodeElement, options);
      addPublicKeyToOptionsIfPresentInNodeElement(nodeElement, options);
      return options;
   }

   static void addRunScriptToOptionsIfPresentInNodeElement(NodeElement nodeElement,
            TemplateOptions options) {
      if (nodeElement.getRunscript() != null)
         try {
            options.runScript(Files.toByteArray(nodeElement.getRunscript()));
         } catch (IOException e) {
            throw new BuildException(e);
         }
   }

   static void addPrivateKeyToOptionsIfPresentInNodeElement(NodeElement nodeElement,
            TemplateOptions options) {
      if (nodeElement.getPrivatekeyfile() != null)
         try {
            options.installPrivateKey(Files.toString(nodeElement.getPrivatekeyfile(),
                     Charsets.UTF_8));
         } catch (IOException e) {
            throw new BuildException(e);
         }
   }

   static void addPublicKeyToOptionsIfPresentInNodeElement(NodeElement nodeElement,
            TemplateOptions options) {
      if (nodeElement.getPrivatekeyfile() != null)
         try {
            options.authorizePublicKey(Files.toString(nodeElement.getPublickeyfile(),
                     Charsets.UTF_8));
         } catch (IOException e) {
            throw new BuildException(e);
         }
   }

   static String ipOrEmptyString(Set<String> set) {
      if (set.size() > 0) {
         return Iterables.get(set, 0);
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
