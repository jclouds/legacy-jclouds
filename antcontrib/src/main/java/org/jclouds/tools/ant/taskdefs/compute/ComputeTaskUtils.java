/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.tools.ant.taskdefs.compute;

import java.io.IOException;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.tools.ant.logging.config.AntLoggingModule;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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
   static LoadingCache<URI, ComputeServiceContext> buildComputeMap(final Provider<Project> projectProvider) {
      return CacheBuilder.newBuilder().build(new CacheLoader<URI, ComputeServiceContext>() {

         @SuppressWarnings("unchecked")
         @Override
         public ComputeServiceContext load(URI from) {
            Properties props = new Properties();
            props.putAll(projectProvider.get().getProperties());
            Set<Module> modules = ImmutableSet.<Module> of(new AntLoggingModule(projectProvider.get(),
                     ComputeServiceConstants.COMPUTE_LOGGER), new JschSshClientModule());
            // adding the properties to the factory will allow us to pass
            // alternate endpoints
            String provider = from.getHost();
            Credentials creds = Credentials.parse(from);
            return ContextBuilder.newBuilder(provider)
                                 .credentials(creds.identity, creds.credential)
                                 .modules(modules)
                                 .overrides(props).buildView(ComputeServiceContext.class);
         }

      });

   }

   static Template createTemplateFromElement(NodeElement nodeElement, ComputeService computeService) throws IOException {
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
      addHardwareFromElementToTemplate(nodeElement, templateBuilder);
      templateBuilder.options(getNodeOptionsFromElement(nodeElement));

      return templateBuilder.build();
   }

   static void addHardwareFromElementToTemplate(NodeElement nodeElement, TemplateBuilder template) {
      if (nodeElement.getHardware().equalsIgnoreCase("smallest")) {
         template.smallest();
      } else if (nodeElement.getHardware().equalsIgnoreCase("fastest")) {
         template.fastest();
      } else if (nodeElement.getHardware().equalsIgnoreCase("biggest")) {
         template.biggest();
      } else {
         throw new BuildException("size: " + nodeElement.getHardware()
                  + " not supported.  valid sizes are smallest, fastest, biggest");
      }
   }

   static TemplateOptions getNodeOptionsFromElement(NodeElement nodeElement) throws IOException {
      TemplateOptions options = new TemplateOptions().inboundPorts(getPortsToOpenFromElement(nodeElement));
      addRunScriptToOptionsIfPresentInNodeElement(nodeElement, options);
      addPrivateKeyToOptionsIfPresentInNodeElement(nodeElement, options);
      addPublicKeyToOptionsIfPresentInNodeElement(nodeElement, options);
      return options;
   }

   static void addRunScriptToOptionsIfPresentInNodeElement(NodeElement nodeElement, TemplateOptions options) throws IOException {
      if (nodeElement.getRunscript() != null)
         options.runScript(Statements.exec(Files.toString(nodeElement.getRunscript(), Charsets.UTF_8)));
   }

   static void addPrivateKeyToOptionsIfPresentInNodeElement(NodeElement nodeElement, TemplateOptions options)
            throws IOException {
      if (nodeElement.getPrivatekeyfile() != null)
         options.installPrivateKey(Files.toString(nodeElement.getPrivatekeyfile(), Charsets.UTF_8));
   }

   static void addPublicKeyToOptionsIfPresentInNodeElement(NodeElement nodeElement, TemplateOptions options) throws IOException {
      if (nodeElement.getPrivatekeyfile() != null)
         options.authorizePublicKey(Files.toString(nodeElement.getPublickeyfile(), Charsets.UTF_8));
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
