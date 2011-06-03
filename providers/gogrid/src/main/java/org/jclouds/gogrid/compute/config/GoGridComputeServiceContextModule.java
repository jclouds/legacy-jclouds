/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.compute.config;

import static org.jclouds.compute.domain.OsFamily.UBUNTU;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.TemplateBuilder;

import com.google.inject.Injector;

/**
 * @author Oleksiy Yarmula
 * @author Adrian Cole
 */
public class GoGridComputeServiceContextModule extends BaseComputeServiceContextModule {

   @Override
   protected void configure() {
      install(new GoGridComputeServiceDependenciesModule());
      install(new GoGridBindComputeStrategiesByClass());
      install(new GoGridBindComputeSuppliersByClass());
      super.configure();
   }

   @Override
   protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
      return template.osFamily(UBUNTU).osVersionMatches("1[10].[10][04]").imageNameMatches(".*w/ None.*");
   }
}
