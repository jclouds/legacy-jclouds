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

package org.jclouds.compute.config;

import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public abstract class StandaloneComputeServiceContextModule extends BaseComputeServiceContextModule {
   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<ComputeServiceContext>() {
      }).to(new TypeLiteral<ComputeServiceContextImpl<ComputeService, ComputeService>>() {
      }).in(Scopes.SINGLETON);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Set<Module> modules = Sets.newLinkedHashSet();
      private Class<? extends AddNodeWithTagStrategy> addNodeWithTagStrategy;
      private Class<? extends DestroyNodeStrategy> destroyNodeStrategy;
      private Class<? extends GetNodeMetadataStrategy> getNodeMetadataStrategy;
      private Class<? extends ListNodesStrategy> listNodesStrategy;
      private Class<? extends RebootNodeStrategy> rebootNodeStrategy;
      private Class<? extends Supplier<Set<? extends Hardware>>> hardwareSupplier;
      private Class<? extends Supplier<Set<? extends Image>>> imageSupplier;

      public Builder install(Module module) {
         this.modules.add(module);
         return this;
      }

      public Builder defineAddNodeWithTagStrategy(Class<? extends AddNodeWithTagStrategy> addNodeWithTagStrategy) {
         this.addNodeWithTagStrategy = addNodeWithTagStrategy;
         return this;
      }

      public Builder defineDestroyNodeStrategy(Class<? extends DestroyNodeStrategy> destroyNodeStrategy) {
         this.destroyNodeStrategy = destroyNodeStrategy;
         return this;
      }

      public Builder defineGetNodeMetadataStrategy(Class<? extends GetNodeMetadataStrategy> getNodeMetadataStrategy) {
         this.getNodeMetadataStrategy = getNodeMetadataStrategy;
         return this;
      }

      public Builder defineListNodesStrategy(Class<? extends ListNodesStrategy> listNodesStrategy) {
         this.listNodesStrategy = listNodesStrategy;
         return this;
      }

      public Builder defineRebootNodeStrategy(Class<? extends RebootNodeStrategy> rebootNodeStrategy) {
         this.rebootNodeStrategy = rebootNodeStrategy;
         return this;
      }

      public Builder defineHardwareSupplier(Class<? extends Supplier<Set<? extends Hardware>>> hardwareSupplier) {
         this.hardwareSupplier = hardwareSupplier;
         return this;
      }

      public Builder defineImageSupplier(Class<? extends Supplier<Set<? extends Image>>> imageSupplier) {
         this.imageSupplier = imageSupplier;
         return this;
      }

      public StandaloneComputeServiceContextModule build() {
         return new StandaloneComputeServiceContextModule() {

            @Override
            protected Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy() {
               return addNodeWithTagStrategy;
            }

            @Override
            protected Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy() {
               return destroyNodeStrategy;
            }

            @Override
            protected Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy() {
               return getNodeMetadataStrategy;
            }

            @Override
            protected Class<? extends Supplier<Set<? extends Hardware>>> defineHardwareSupplier() {
               return hardwareSupplier;
            }

            @Override
            protected Class<? extends Supplier<Set<? extends Image>>> defineImageSupplier() {
               return imageSupplier;
            }

            @Override
            protected Class<? extends ListNodesStrategy> defineListNodesStrategy() {
               return listNodesStrategy;
            }

            @Override
            protected Class<? extends RebootNodeStrategy> defineRebootNodeStrategy() {
               return rebootNodeStrategy;
            }

            @Override
            protected void configure() {
               for (Module module : modules)
                  install(module);
               super.configure();
            }

         };
      }
   }
}