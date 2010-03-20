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
package ${package}.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.RunScriptRunning;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.SshClient;
import ${package}.${providerName}AsyncClient;
import ${package}.${providerName}Client;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provides;

/**
 * @author ${author}
 */
public class ${providerName}ComputeServiceContextModule extends ${providerName}ContextModule {

   @Override
   protected void configure() {
      super.configure();
      bind(AddNodeWithTagStrategy.class).to(${providerName}AddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(${providerName}ListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(${providerName}GetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(${providerName}RebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(${providerName}DestroyNodeStrategy.class);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%d";
   }

   @Singleton
   public static class ${providerName}AddNodeWithTagStrategy implements AddNodeWithTagStrategy {

      @Inject
      protected ${providerName}AddNodeWithTagStrategy() {
      }

      @Override
      public NodeMetadata execute(String tag, String name, Template template) {
          /*
           * TODO: implement
           */
          return null;
      }
   }

   @Singleton
   public static class ${providerName}RebootNodeStrategy implements RebootNodeStrategy {

      @Inject
      protected ${providerName}RebootNodeStrategy() {
      }

      @Override
      public boolean execute(ComputeMetadata node) {
          /*
           * TODO: implement
           */
          return false;
      }
   }

   @Singleton
   public static class ${providerName}ListNodesStrategy implements ListNodesStrategy {

      @Inject
      protected ${providerName}ListNodesStrategy() {
      }

      @Override
      public Iterable<? extends ComputeMetadata> execute() {
          /*
           * TODO: implement
           */
          return null;
      }

   }

   @Singleton
   public static class ${providerName}GetNodeMetadataStrategy implements GetNodeMetadataStrategy {

      @Inject
      protected ${providerName}GetNodeMetadataStrategy() {
      }

      @Override
      public NodeMetadata execute(ComputeMetadata node) {
          /*
           * TODO: implement
           */
          return null;
      }
   }

   @Singleton
   public static class ${providerName}DestroyNodeStrategy implements DestroyNodeStrategy {

      @Inject
      protected ${providerName}DestroyNodeStrategy() {
      }

      @Override
      public boolean execute(ComputeMetadata node) {
          /*
           * TODO: implement
           */
          return false;
      }

   }

   @Provides
   @Singleton
   ComputeServiceContext provideContext(ComputeService computeService,
            RestContext<${providerName}AsyncClient, ${providerName}Client> context) {
      return new ComputeServiceContextImpl<${providerName}AsyncClient, ${providerName}Client>(computeService, context);
   }

   @Provides
   @Singleton
   @Named("NOT_RUNNING")
   protected Predicate<SshClient> runScriptRunning(RunScriptRunning stateRunning) {
      return new RetryablePredicate<SshClient>(Predicates.not(stateRunning), 600, 3,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   Location getDefaultLocation(Map<String, ? extends Location> locations) {
      return locations.get("SANFRANCISCO");
   }

   @Provides
   @Singleton
   Map<String, ? extends Location> getDefaultLocations(${providerName}Client sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer) {
      final Set<Location> locations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      locations.add(new LocationImpl(LocationScope.ZONE, "SANFRANCISCO", "San Francisco, CA", null,
               true));
      holder.logger.debug("<< locations(%d)", locations.size());
      return Maps.uniqueIndex(locations, new Function<Location, String>() {

         @Override
         public String apply(Location from) {
            return from.getId();
         }
      });
   }

   @Provides
   @Singleton
   protected Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getId();
         }
      };
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Size> provideSizes(${providerName}Client sync,
            Map<String, ? extends Image> images, LogHolder holder,
            Function<ComputeMetadata, String> indexer) throws InterruptedException,
            TimeoutException, ExecutionException {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");

      /*
       * TODO: implement
       */
      
      holder.logger.debug("<< sizes(%d)", sizes.size());
      return Maps.uniqueIndex(sizes, indexer);
   }

   private static class LogHolder {
       @Resource
       @Named(ComputeServiceConstants.COMPUTE_LOGGER)
       protected Logger logger = Logger.NULL;
   }

   @Provides
   @Singleton
   protected Map<String, ? extends Image> provideImages(final ${providerName}Client sync, LogHolder holder,
            Function<ComputeMetadata, String> indexer, Location location)
            throws InterruptedException, ExecutionException, TimeoutException {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");

      /*
       * TODO: implement
       */

      holder.logger.debug("<< images(%d)", images.size());
      return Maps.uniqueIndex(images, indexer);
   }
}
