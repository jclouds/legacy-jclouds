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
import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ${package}.${providerName}Client;
import ${package}.${providerName}AsyncClient;
import ${package}.config.${providerName}ContextModule;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.LoadBalancerService;
import org.jclouds.compute.config.ComputeServiceTimeoutsModule;
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
 * @author ${author}
 */
public class ${providerName}ComputeServiceContextModule extends ${providerName}ContextModule {

   private final String providerName;
   
   public ${providerName}ComputeServiceContextModule(String providerName){
      super(providerName);
      this.providerName=providerName;
   }
   
   @Override
   protected void configure() {
      super.configure();
      install(new ComputeServiceTimeoutsModule());
      bind(new TypeLiteral<ComputeServiceContext>() {
      })
               .to(
                        new TypeLiteral<ComputeServiceContextImpl<${providerName}Client, ${providerName}AsyncClient>>() {
                        }).in(Scopes.SINGLETON);
      bind(AddNodeWithTagStrategy.class).to(${providerName}AddNodeWithTagStrategy.class);
      bind(ListNodesStrategy.class).to(${providerName}ListNodesStrategy.class);
      bind(GetNodeMetadataStrategy.class).to(${providerName}GetNodeMetadataStrategy.class);
      bind(RebootNodeStrategy.class).to(${providerName}RebootNodeStrategy.class);
      bind(DestroyNodeStrategy.class).to(${providerName}DestroyNodeStrategy.class);
      bind(LoadBalancerService.class).toProvider(Providers.<LoadBalancerService> of(null));
   }
   
   /**
    * tested known configuration
    */
   @Provides
   @Named("DEFAULT")
   protected TemplateBuilder provideTemplate(TemplateBuilder template) {
      return template.osFamily(UBUNTU);
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
      public boolean execute(String id) {
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
   public static class ${providerName}GetNodeMetadataStrategy implements GetNodeMetadataStrategy {

      @Inject
      protected ${providerName}GetNodeMetadataStrategy() {
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
   public static class ${providerName}DestroyNodeStrategy implements DestroyNodeStrategy {

      @Inject
      protected ${providerName}DestroyNodeStrategy() {
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
   Location getDefaultLocation(Set<? extends Location> locations) {

      /*
       * TODO: implement
       */
      
      return null;
   }

   @Provides
   @Singleton
   Set<? extends Location> getAssignableLocations(${providerName}Client sync, LogHolder holder ) {
      final Set<Location> assignableLocations = Sets.newHashSet();
      holder.logger.debug(">> providing locations");
      Location parent = new LocationImpl(LocationScope.PROVIDER, providerName, providerName, null);
      /*
       * TODO: add children with parent to locations.  Note do not add parent to assignablelocations 
       * directly
       */
      
      holder.logger.debug("<< locations(%d)", assignableLocations.size());
      return assignableLocations;
   }

   @Provides
   @Singleton
   protected Set<? extends Size> provideSizes(${providerName}Client sync,
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
   protected Set<? extends Image> provideImages(final ${providerName}Client sync, LogHolder holder,
            Location location) {
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
