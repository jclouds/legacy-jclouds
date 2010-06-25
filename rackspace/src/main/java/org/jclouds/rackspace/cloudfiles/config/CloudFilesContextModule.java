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
package org.jclouds.rackspace.cloudfiles.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.rackspace.CloudFiles;
import org.jclouds.rackspace.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.rackspace.cloudfiles.CloudFilesClient;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the {@link CloudFilesContextModule}; requires
 * {@link CloudFilesAsyncClient} bound.
 * 
 * @author Adrian Cole
 */
public class CloudFilesContextModule extends AbstractModule {

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   RestContext<CloudFilesClient, CloudFilesAsyncClient> provideContext(
         Closer closer, HttpClient http, HttpAsyncClient asyncHttp,
         CloudFilesAsyncClient asyncApi, CloudFilesClient defaultApi,
         @CloudFiles URI endPoint,
         @Named(RackspaceConstants.PROPERTY_RACKSPACE_USER) String account) {
      return new RestContextImpl<CloudFilesClient, CloudFilesAsyncClient>(
            closer, http, asyncHttp, defaultApi, asyncApi, endPoint, account);
   }

}
