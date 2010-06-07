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
package org.jclouds.ibmdev.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero;
import org.jclouds.compute.predicates.ScriptStatusReturnsZero.CommandUsingClient;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.config.IBMDeveloperCloudContextModule;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;

/**
 * @author Adrian Cole
 */
public class IBMDeveloperCloudComputeServiceContextModule extends IBMDeveloperCloudContextModule {

   private final String providerName;

   public IBMDeveloperCloudComputeServiceContextModule(String providerName) {
      super(providerName);
      this.providerName = providerName;
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceContext>() {
      })
               .to(
                        new TypeLiteral<ComputeServiceContextImpl<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient>>() {
                        }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(IBMDeveloperCloudAddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(IBMDeveloperCloudListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(IBMDeveloperCloudGetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(IBMDeveloperCloudRebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(IBMDeveloperCloudDestroyNodeStrategy.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
   }

   @Provides
   @Singleton
   protected Predicate<IPSocket> socketTester(SocketOpen open) {
      return new RetryablePredicate<IPSocket>(open, 130, 1, TimeUnit.SECONDS);
   }

   /**
    * tested known configuration
    */
   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(UBUNTU);
   }

   @Provides
   @Named("NAMING_CONVENTION")
   @Singleton
   String provideNamingConvention() {
      return "%s-%d";
   }

   @Singleton
   public static class IBMDeveloperCloudAddNodeWithTagStrategy implements AddNodeWithTagStrategy {

      @Inject
      protected IBMDeveloperCloudAddNodeWithTagStrategy() {
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
   public static class IBMDeveloperCloudRebootNodeStrategy implements RebootNodeStrategy {

      @Inject
      protected IBMDeveloperCloudRebootNodeStrategy() {
      }

      @Override
      public boolean execute(String id) {
         /*
          * TODO: implement
          */
         return false;
      }
   }

   @Singleton
   public static class IBMDeveloperCloudListNodesStrategy implements ListNodesStrategy {

      @Inject
      protected IBMDeveloperCloudListNodesStrategy() {
      }

      @Override
      public Iterable<? extends ComputeMetadata> list() {
         /*
          * TODO: implement
          */return null;
      }

      @Override
      public Iterable<? extends NodeMetadata> listDetailsOnNodesMatching(
               Predicate<ComputeMetadata> filter) {
         /*
          * TODO: implement
          */
         return null;
      }

   }

   @Singleton
   public static class IBMDeveloperCloudGetNodeMetadataStrategy implements GetNodeMetadataStrategy {

      @Inject
      protected IBMDeveloperCloudGetNodeMetadataStrategy() {
      }

      @Override
      public NodeMetadata execute(String id) {
         /*
          * TODO: implement
          */
         return null;
      }
   }

   @Singleton
   public static class IBMDeveloperCloudDestroyNodeStrategy implements DestroyNodeStrategy {

      @Inject
      protected IBMDeveloperCloudDestroyNodeStrategy() {
      }

      @Override
      public boolean execute(String id) {
         /*
          * TODO: implement
          */
         return false;
      }

   }

   @Provides
   @Singleton
   @Named("NOT_RUNNING")
   protected Predicate<CommandUsingClient> runScriptRunning(ScriptStatusReturnsZero stateRunning) {
      return new RetryablePredicate<CommandUsingClient>(Predicates.not(stateRunning), 600, 3,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   Location getDefaultLocation(Set<? extends Location> locations) {

      /*
       * TODO: implement
       */

      return null;
   }

   @Provides
   @Singleton
   Set<? extends Location> getAssignableLocations(IBMDeveloperCloudClient sync, LogHolder holder) {
      final Set<Location> assignableLocations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      Location parent = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      /*
       * TODO: add children with parent to locations. Note do not add parent to assignablelocations
       * directly
       */

      holder.logger.debug("<< locations(%d)", assignableLocations.size());
      return assignableLocations;
   }

   @Provides
   @Singleton
   protected Set<? extends Size> provideSizes(IBMDeveloperCloudClient sync,
            Set<? extends Image> images, LogHolder holder) {
      final Set<Size> sizes = Sets.newHashSet();
      holder.logger.debug(">> providing sizes");

      /*
       * TODO: implement
       */

      holder.logger.debug("<< sizes(%d)", sizes.size());
      return sizes;
   }

   @Provides
   @Singleton
   protected Set<? extends Image> provideImages(final IBMDeveloperCloudClient sync,
            LogHolder holder, Location location) {
      final Set<Image> images = Sets.newHashSet();
      holder.logger.debug(">> providing images");

      /*
       * TODO: implement
       */

      holder.logger.debug("<< images(%d)", images.size());
      return images;
   }

   @Singleton
   private static class LogHolder {
      @Resource
      @Named(ComputeServiceConstants.COMPUTE_LOGGER)
      protected Logger logger = Logger.NULL;
   }
}