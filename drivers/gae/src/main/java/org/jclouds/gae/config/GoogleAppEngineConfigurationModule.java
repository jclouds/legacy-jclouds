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

package org.jclouds.gae.config;

import javax.inject.Singleton;

import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.gae.GaeHttpCommandExecutorService;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandExecutorServiceImpl;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.Injector;
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
      super(MoreExecutors.sameThreadExecutor(), MoreExecutors.sameThreadExecutor());
   }

   @Override
   protected void configure() {
      super.configure();
      bind(TransformingHttpCommandExecutorService.class).to(TransformingHttpCommandExecutorServiceImpl.class);
   }

   @Singleton
   @Provides
   protected HttpCommandExecutorService providerHttpCommandExecutorService(Injector injector) {
      return injector.getInstance(GaeHttpCommandExecutorService.class);
   }

   @Provides
   URLFetchService provideURLFetchService() {
      return URLFetchServiceFactory.getURLFetchService();
   }
}
