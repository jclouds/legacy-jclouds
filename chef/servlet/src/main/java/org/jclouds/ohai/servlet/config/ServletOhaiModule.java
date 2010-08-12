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

package org.jclouds.ohai.servlet.config;


import static org.jclouds.chef.util.ChefUtils.ohaiAutomaticAttributeBinder;

import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.config.multibindings.MapBinder;
import org.jclouds.ohai.servlet.suppliers.ServletContextInfoSupplier;

import com.google.common.base.Supplier;
import com.google.inject.AbstractModule;

/**
 * Wires the components needed to parse ohai data
 * 
 * @author Adrian Cole
 */
public class ServletOhaiModule extends AbstractModule {

   @Override
   protected void configure() {
      MapBinder<String, Supplier<JsonBall>> mapbinder = ohaiAutomaticAttributeBinder(binder());
      mapbinder.addBinding("webapp").to(ServletContextInfoSupplier.class);
   }

}