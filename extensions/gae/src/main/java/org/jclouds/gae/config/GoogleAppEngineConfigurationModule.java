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
package org.jclouds.gae.config;

import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.joda.config.JodaDateServiceModule;
import org.jclouds.gae.GaeHttpCommandExecutorService;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.common.util.concurrent.Executors;
import com.google.inject.Provides;

/**
 * Configures {@link GaeHttpCommandExecutorService}.
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpCommandExecutorService
@ConfiguresExecutorService
@SingleThreaded
public class GoogleAppEngineConfigurationModule extends ExecutorServiceModule {

   public GoogleAppEngineConfigurationModule() {
      super(Executors.sameThreadExecutor(), Executors.sameThreadExecutor());
   }

   @Override
   protected void configure() {
      super.configure();
      install(new JodaDateServiceModule());
      bind(HttpCommandExecutorService.class).to(GaeHttpCommandExecutorService.class);
      bind(TransformingHttpCommandExecutorService.class).to(
               TransformingHttpCommandExecutorServiceImpl.class);
   }

   @Provides
   URLFetchService provideURLFetchService() {
      return URLFetchServiceFactory.getURLFetchService();
   }
}
