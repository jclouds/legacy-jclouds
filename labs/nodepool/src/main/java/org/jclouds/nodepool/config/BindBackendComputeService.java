/*
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
package org.jclouds.nodepool.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.nodepool.config.NodePoolProperties.BACKEND_GROUP;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.Provider;
import org.jclouds.nodepool.Backend;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.util.Predicates2;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Exposed;
import com.google.inject.Module;
import com.google.inject.Provides;

public class BindBackendComputeService extends BindJcloudsModules {

   @Provides
   @Singleton
   @Backend
   protected String provideBackendProvider(@Named(NodePoolProperties.BACKEND_PROVIDER) String provider) {
      return provider;
   }

   // things wrapped in suppliers are intentional. They can cause network i/o
   // and shouldn't be invoked until after the injector is created.

   @Provides
   @Singleton
   @Backend
   @Exposed
   protected Supplier<ComputeService> makeBackendComputeService(@Backend final String provider,
            @Backend final Set<Module> modules, @Provider final Supplier<Credentials> creds,
            @Backend final Supplier<Properties> overrides, final Closer closer) {
      return Suppliers.memoize(new Supplier<ComputeService>() {

         @Override
         public ComputeService get() {
            Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
            ComputeServiceContext ctx = ContextBuilder.newBuilder(provider)
                  .credentials(currentCreds.identity, currentCreds.credential).overrides(overrides.get())
                  .modules(modules).buildView(ComputeServiceContext.class);
            closer.addToClose(ctx);
            return ctx.getComputeService();
         }

      });
   }

   private static final Predicate<String> keys = Predicates.<String>and(
      Predicates.not(Predicates2.startsWith("jclouds.nodepool")),
      Predicates.not(Predicates2.startsWith("nodepool")));

   @Provides
   @Singleton
   @Backend
   protected Supplier<Properties> propertiesFor(final FilterStringsBoundToInjectorByName filterStringsBoundByName,
            @Backend final String provider, @Provider final Supplier<URI> endpoint,
            @ApiVersion final String apiVersion, @BuildVersion final String buildVersion) {
      return Suppliers.memoize(new Supplier<Properties>() {

         @Override
         public Properties get() {
            Properties props = new Properties();
            props.putAll(filterStringsBoundByName.apply(keys));
            props.put(provider + ".endpoint", endpoint.get().toASCIIString());
            props.put(provider + ".api-version", apiVersion);
            props.put(provider + ".build-version", buildVersion);
            return props;
         }

      });
   }

   @Provides
   @Exposed
   @Singleton
   protected TemplateBuilder templateBuilder(@Backend final Supplier<ComputeService> compute,
            @Backend final Supplier<Template> template) {
      try {
         // if the backend cannot provide a decent template we'll have problems with looking for
         // images, just provide a custom templatebuilder that returns our custom template.
         compute.get().templateBuilder().build();
         return compute.get().templateBuilder();
      } catch (Exception e) {
         return new TemplateBuilder() {

            @Override
            public TemplateBuilder smallest() {
               return this;
            }

            @Override
            public TemplateBuilder osVersionMatches(String osVersionRegex) {
               return this;
            }

            @Override
            public TemplateBuilder osNameMatches(String osNameRegex) {
               return this;
            }

            @Override
            public TemplateBuilder osFamily(OsFamily os) {
               return this;
            }

            @Override
            public TemplateBuilder osDescriptionMatches(String osDescriptionRegex) {
               return this;
            }

            @Override
            public TemplateBuilder osArchMatches(String architecture) {
               return this;
            }

            @Override
            public TemplateBuilder os64Bit(boolean is64bit) {
               return this;
            }

            @Override
            public TemplateBuilder options(TemplateOptions options) {
               return this;
            }

            @Override
            public TemplateBuilder minRam(int megabytes) {
               return this;
            }

            @Override
            public TemplateBuilder minDisk(double gigabytes) {
               return this;
            }

            @Override
            public TemplateBuilder minCores(double minCores) {
               return this;
            }

            @Override
            public TemplateBuilder locationId(String locationId) {
               return this;
            }

            @Override
            public TemplateBuilder imageVersionMatches(String imageVersionRegex) {
               return this;
            }

            @Override
            public TemplateBuilder imageNameMatches(String imageNameRegex) {
               return this;
            }

            @Override
            public TemplateBuilder imageMatches(Predicate<Image> condition) {
               return this;
            }

            @Override
            public TemplateBuilder imageId(String imageId) {
               return this;
            }

            @Override
            public TemplateBuilder imageDescriptionMatches(String imageDescriptionRegex) {
               return this;
            }

            @Override
            public TemplateBuilder hypervisorMatches(String hypervisorRegex) {
               return this;
            }

            @Override
            public TemplateBuilder hardwareId(String hardwareId) {
               return this;
            }

            @Override
            public TemplateBuilder fromTemplate(Template image) {
               return this;
            }

            @Override
            public TemplateBuilder fromImage(Image image) {
               return this;
            }

            @Override
            public TemplateBuilder fromHardware(Hardware hardware) {
               return this;
            }

            @Override
            @Beta
            public TemplateBuilder from(String spec) {
               return this;
            }

            @Override
            @Beta
            public TemplateBuilder from(TemplateBuilderSpec spec) {
               return this;
            }

            @Override
            public TemplateBuilder fastest() {
               return this;
            }

            @Override
            public Template build() {
               return template.get();
            }

            @Override
            public TemplateBuilder biggest() {
               return this;
            }

            @Override
            public TemplateBuilder any() {
               return this;
            }
         };
      }
   }

   @Provides
   @Singleton
   @Backend
   @Exposed
   protected Supplier<Template> makeBackendTemplate(@Backend Supplier<ComputeService> compute,
            @Named(BACKEND_GROUP) final String poolGroupPrefix) {
      return Suppliers.memoize(Suppliers.compose(new Function<ComputeService, Template>() {
         @Override
         public Template apply(ComputeService input) {
            try {
               return input.templateBuilder().build();
            } catch (IllegalStateException e) {
               // if there's no template we must be on byon and there must be at least one node in
               // our group
               Set<? extends NodeMetadata> nodes = Sets.filter(input.listNodesDetailsMatching(NodePredicates.all()),
                        NodePredicates.inGroup(poolGroupPrefix));
               checkState(!nodes.isEmpty(), "service provided no template and no node was in this nodepool's group.");
               final NodeMetadata node = Iterables.get(nodes, 0);
               final Image image = new ImageBuilder().id(node.getId()).location(node.getLocation())
                        .operatingSystem(node.getOperatingSystem()).status(Status.AVAILABLE)
                        .description("physical node").build();
               final Hardware hardware = new HardwareBuilder().id(node.getId()).build();
               return new Template() {

                  @Override
                  public Image getImage() {
                     return image;
                  }

                  @Override
                  public Hardware getHardware() {
                     return hardware;
                  }

                  @Override
                  public Location getLocation() {
                     return node.getLocation();
                  }

                  @Override
                  public TemplateOptions getOptions() {
                     return new TemplateOptions();
                  }

                  @Override
                  public Template clone() {
                     return this;
                  }
               };
            }
         }

      }, compute));
   }
}
