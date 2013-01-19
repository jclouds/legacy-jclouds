/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.cloudstack.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.CloudStackDomainAsyncClient;
import org.jclouds.cloudstack.CloudStackDomainClient;
import org.jclouds.cloudstack.CloudStackGlobalAsyncClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.Utils;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.location.Provider;
import org.jclouds.rest.RestContext;

import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudStackContextImpl extends ComputeServiceContextImpl implements CloudStackContext {
   private final RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> domainContext;
   private final RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> globalContext;

   @Inject
   public CloudStackContextImpl(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
            ComputeService computeService, Utils utils,
            RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> domainContext,
            RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> globalContext) {
      super(backend, backendType, computeService, utils);
      this.domainContext = domainContext;
      this.globalContext = globalContext;
   }

   @Override
   public RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> getDomainContext() {
      return domainContext;
   }

   @Override
   public RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> getGlobalContext() {
      return globalContext;
   }
}
