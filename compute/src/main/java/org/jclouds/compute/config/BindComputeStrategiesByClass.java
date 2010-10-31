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

import org.jclouds.compute.strategy.AddNodeWithTagStrategy;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.RunNodesAndAddToSetStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.compute.strategy.impl.EncodeTagIntoNameRunNodesAndAddToSetStrategy;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 * 
 */
public abstract class BindComputeStrategiesByClass extends AbstractModule {
   protected void configure() {
      bindRunNodesAndAddToSetStrategy(defineRunNodesAndAddToSetStrategy());
      bindAddNodeWithTagStrategy(defineAddNodeWithTagStrategy());
      bindListNodesStrategy(defineListNodesStrategy());
      bindGetNodeMetadataStrategy(defineGetNodeMetadataStrategy());
      bindRebootNodeStrategy(defineRebootNodeStrategy());
      bindStartNodeStrategy(defineStartNodeStrategy());
      bindStopNodeStrategy(defineStopNodeStrategy());
      bindDestroyNodeStrategy(defineDestroyNodeStrategy());
   }

   protected void bindRunNodesAndAddToSetStrategy(Class<? extends RunNodesAndAddToSetStrategy> clazz) {
      bind(RunNodesAndAddToSetStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   /**
    * needed, if {@link RunNodesAndAddToSetStrategy} requires it
    */
   protected void bindAddNodeWithTagStrategy(Class<? extends AddNodeWithTagStrategy> clazz) {
      bind(AddNodeWithTagStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindDestroyNodeStrategy(Class<? extends DestroyNodeStrategy> clazz) {
      bind(DestroyNodeStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindRebootNodeStrategy(Class<? extends RebootNodeStrategy> clazz) {
      bind(RebootNodeStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindStartNodeStrategy(Class<? extends ResumeNodeStrategy> clazz) {
      bind(ResumeNodeStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindStopNodeStrategy(Class<? extends SuspendNodeStrategy> clazz) {
      bind(SuspendNodeStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindGetNodeMetadataStrategy(Class<? extends GetNodeMetadataStrategy> clazz) {
      bind(GetNodeMetadataStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   protected void bindListNodesStrategy(Class<? extends ListNodesStrategy> clazz) {
      bind(ListNodesStrategy.class).to(clazz).in(Scopes.SINGLETON);
   }

   protected Class<? extends RunNodesAndAddToSetStrategy> defineRunNodesAndAddToSetStrategy() {
      return EncodeTagIntoNameRunNodesAndAddToSetStrategy.class;
   }

   /**
    * needed, if {@link RunNodesAndAddToSetStrategy} requires it
    */
   protected abstract Class<? extends AddNodeWithTagStrategy> defineAddNodeWithTagStrategy();

   protected abstract Class<? extends DestroyNodeStrategy> defineDestroyNodeStrategy();

   protected abstract Class<? extends RebootNodeStrategy> defineRebootNodeStrategy();

   protected abstract Class<? extends ResumeNodeStrategy> defineStartNodeStrategy();

   protected abstract Class<? extends SuspendNodeStrategy> defineStopNodeStrategy();

   protected abstract Class<? extends GetNodeMetadataStrategy> defineGetNodeMetadataStrategy();

   protected abstract Class<? extends ListNodesStrategy> defineListNodesStrategy();
}