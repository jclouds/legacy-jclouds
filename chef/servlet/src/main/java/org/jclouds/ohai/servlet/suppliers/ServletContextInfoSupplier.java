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

package org.jclouds.ohai.servlet.suppliers;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;

import org.jclouds.chef.servlet.functions.InitParamsToProperties;
import org.jclouds.json.Json;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ServletContextInfoSupplier extends ServletContextSupplier {
   private final InitParamsToProperties converter;
   private final Json json;

   @Inject
   public ServletContextInfoSupplier(ServletContext servletContext, InitParamsToProperties converter, Json json) {
      super(servletContext);
      this.converter = converter;
      this.json = json;
   }

   @Override
   protected String provideJson(ServletContext servletContext) {
      return String.format("{\"server_info\":\"%s\",\"init_params\":%s}", servletContext.getServerInfo(), json
            .toJson(converter.apply(servletContext)));
   }
}