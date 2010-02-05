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
package org.jclouds.http.apachehc.config;

import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.apachehc.ApacheHCHttpCommandExecutorService;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Configures {@link ApacheHCHttpCommandExecutorService}.
 * 
 * Note that this uses threads
 * 
 * @author Sam Tunnicliffe
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
public class ApacheHCHttpCommandExecutorServiceModule extends AbstractModule {

   @Override
   protected void configure() {
      bindClient();
   }

   protected void bindClient() {
      bind(HttpCommandExecutorService.class).to(ApacheHCHttpCommandExecutorService.class).in(
               Scopes.SINGLETON);

      bind(TransformingHttpCommandExecutorService.class).to(
               TransformingHttpCommandExecutorServiceImpl.class).in(Scopes.SINGLETON);
   }

}