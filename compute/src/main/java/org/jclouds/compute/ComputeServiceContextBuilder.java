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
package org.jclouds.compute;

import java.util.Properties;

import org.jclouds.compute.config.ResolvesImages;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.rest.RestContextBuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * @author Adrian Cole
 */
public abstract class ComputeServiceContextBuilder<A, S> extends RestContextBuilder<A, S> {

   public ComputeServiceContextBuilder(TypeLiteral<A> asyncClientType, TypeLiteral<S> syncClientType) {
      this(asyncClientType, syncClientType, new Properties());
   }

   public ComputeServiceContextBuilder(TypeLiteral<A> asyncClientType,
            TypeLiteral<S> syncClientType, Properties properties) {
      super(asyncClientType, syncClientType, properties);

   }

   @Override
   public Injector buildInjector() {
      addImageResolutionModuleIfNotPresent();
      return super.buildInjector();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ComputeServiceContextBuilder<A, S> withModules(Module... modules) {
      return (ComputeServiceContextBuilder<A, S>) super.withModules(modules);
   }

   public ComputeServiceContext buildComputeServiceContext() {
      // need the generic type information
      return (ComputeServiceContext) buildInjector().getInstance(
               Key.get(Types.newParameterizedType(ComputeServiceContextImpl.class, asyncClientType
                        .getType(), syncClientType.getType())));
   }

   protected void addImageResolutionModuleIfNotPresent() {
      if (!Iterables.any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ResolvesImages.class);
         }

      })) {
         addImageResolutionModule();
      }
   }

   protected void addImageResolutionModule() {
      // do nothing;
      // this is to be overridden when needed
   }
}