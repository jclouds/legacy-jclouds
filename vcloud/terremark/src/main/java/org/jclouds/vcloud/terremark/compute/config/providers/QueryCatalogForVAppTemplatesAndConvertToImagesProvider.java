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
package org.jclouds.vcloud.terremark.compute.config.providers;

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
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 */
@Singleton
public class QueryCatalogForVAppTemplatesAndConvertToImagesProvider implements Provider<Set<? extends Image>> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   public Logger logger = Logger.NULL;

   private final VCloudClient client;
   private final FindLocationForResourceInVDC findLocationForResourceInVDC;
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;
   private final ExecutorService executor;

   @Inject
   protected QueryCatalogForVAppTemplatesAndConvertToImagesProvider(VCloudClient client,
            FindLocationForResourceInVDC findLocationForResourceInVDC,
            PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.client = client;
      this.findLocationForResourceInVDC = findLocationForResourceInVDC;
      this.credentialsProvider = credentialsProvider;
      this.executor = executor;
   }

   /**
    * Terremark does not provide vApp templates in the vDC resourceEntity list. Rather, you must
    * query the catalog.
    */
   @Override
   public Set<? extends Image> get() {
      final Set<Image> images = Sets.newHashSet();
      logger.debug(">> providing vAppTemplates");
      final VDC vDC = client.getDefaultVDC();

      Catalog response = client.getDefaultCatalog();
      Map<String, ListenableFuture<Void>> responses = Maps.newHashMap();

      for (final NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
            final CatalogItem item = client.getCatalogItem(resource.getId());
            if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
               responses.put(item.getName(), ConcurrentUtils.makeListenable(executor
                        .submit(new Callable<Void>() {
                           @Override
                           public Void call() throws Exception {
                              OsFamily myOs = null;
                              for (OsFamily os : OsFamily.values()) {
                                 if (resource.getName().toLowerCase().replaceAll("\\s", "")
                                          .indexOf(os.toString()) != -1) {
                                    myOs = os;
                                 }
                              }
                              Architecture arch = resource.getName().indexOf("64") == -1 ? Architecture.X86_32
                                       : Architecture.X86_64;
                              VAppTemplate template = client.getVAppTemplate(item.getEntity()
                                       .getId());

                              Location location = findLocationForResourceInVDC.apply(resource, vDC
                                       .getId());

                              images.add(new ImageImpl(resource.getId(), template.getName(),
                                       resource.getId(), location, template.getLocation(),
                                       ImmutableMap.<String, String> of(), template
                                                .getDescription(), "", myOs, template.getName(),
                                       arch, credentialsProvider.execute(template)));
                              return null;
                           }
                        }), executor));
            }
         }
      }
      ConcurrentUtils.awaitCompletion(responses, executor, null, logger, "vAppTemplates in " + vDC);
      return images;
   }

}