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

package org.jclouds.opscodeplatform.internal;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.opscodeplatform.OpscodePlatformAsyncClient;
import org.jclouds.opscodeplatform.OpscodePlatformClient;
import org.jclouds.opscodeplatform.OpscodePlatformContext;
import org.jclouds.rest.Utils;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.annotations.Provider;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Singleton
public class OpscodePlatformContextImpl extends RestContextImpl<OpscodePlatformClient, OpscodePlatformAsyncClient>
      implements OpscodePlatformContext {

   @Inject
   protected OpscodePlatformContextImpl(Closer closer, Utils utils, Injector injector,
         TypeLiteral<OpscodePlatformClient> syncApi, TypeLiteral<OpscodePlatformAsyncClient> asyncApi,
         @Provider URI endpoint, @Provider String provider, @Identity String identity, @ApiVersion String apiVersion) {
      super(closer, utils, injector, syncApi, asyncApi, endpoint, provider, identity, apiVersion);
   }

}
