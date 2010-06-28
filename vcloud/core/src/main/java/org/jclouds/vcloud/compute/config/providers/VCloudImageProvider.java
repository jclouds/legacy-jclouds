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
package org.jclouds.vcloud.compute.config.providers;

import static org.jclouds.compute.util.ComputeServiceUtils.parseArchitectureOrNull;
import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.concurrent.ConcurrentUtils;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.compute.functions.FindLocationForResourceInVDC;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VAppTemplate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 */
@Singleton
public class VCloudImageProvider implements Provider<Set<? extends Image>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final VCloudClient client;
   private final FindLocationForResourceInVDC findLocationForResourceInVDC;
   private final PopulateDefaultLoginCredentialsForImageStrategy populateDefaultLoginCredentialsForImageStrategy;
   private final ExecutorService executor;

   @Inject
   protected VCloudImageProvider(
            VCloudClient client,
            FindLocationForResourceInVDC findLocationForResourceInVDC,
            PopulateDefaultLoginCredentialsForImageStrategy populateDefaultLoginCredentialsForImageStrategy,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client = client;
      this.findLocationForResourceInVDC = findLocationForResourceInVDC;
      this.populateDefaultLoginCredentialsForImageStrategy = populateDefaultLoginCredentialsForImageStrategy;
      this.executor = executor;
   }

   @Override
   public Set<? extends Image> get() {
      final Set<Image> images = Sets.newHashSet();
      logger.debug(">> providing vAppTemplates");
      for (final NamedResource vDC : client.getDefaultOrganization().getVDCs().values()) {
         Map<String, NamedResource> resources = client.getVDC(vDC.getId()).getResourceEntities();
         Map<String, ListenableFuture<Void>> responses = Maps.newHashMap();

         for (final NamedResource resource : resources.values()) {
            if (resource.getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
               responses.put(resource.getName(), ConcurrentUtils.makeListenable(executor
                        .submit(new Callable<Void>() {
                           @Override
                           public Void call() throws Exception {
                              OsFamily myOs = parseOsFamilyOrNull(resource.getName());
                              Architecture arch = parseArchitectureOrNull(resource.getName());

                              VAppTemplate template = client.getVAppTemplate(resource.getId());

                              Location location = findLocationForResourceInVDC.apply(resource, vDC
                                       .getId());
                              String name = getName(template.getName());
                              images.add(new ImageImpl(resource.getId(), name, resource.getId(),
                                       location, template.getLocation(), ImmutableMap
                                                .<String, String> of(), template.getDescription(),
                                       "", myOs, name, arch,
                                       populateDefaultLoginCredentialsForImageStrategy
                                                .execute(template)));
                              return null;
                           }
                        }), executor));

            }
         }
         ConcurrentUtils.awaitCompletion(responses, executor, null, logger, "vAppTemplates in "
                  + vDC);
      }
      return images;
   }

   protected String getName(String name) {
      return name;
   }
}