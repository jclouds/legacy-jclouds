/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.internal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.CloudStackDomainAsyncClient;
import org.jclouds.cloudstack.CloudStackDomainClient;
import org.jclouds.cloudstack.CloudStackGlobalAsyncClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.Utils;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.location.Provider;

import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudStackContextImpl extends ComputeServiceContextImpl implements CloudStackContext {
   private final CloudStackClient client;
   private final org.jclouds.rest.RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> domainContext;
   private final org.jclouds.rest.RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> globalContext;

   @Inject
   CloudStackContextImpl(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
         ComputeService computeService, Utils utils, CloudStackClient client,
         org.jclouds.rest.RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> domainContext,
         org.jclouds.rest.RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> globalContext) {
      super(backend, backendType, computeService, utils);
      this.client = client;
      this.domainContext = domainContext;
      this.globalContext = globalContext;
   }

   @Override
   public CloudStackClient getApi() {
      return client;
   }

   @Override
   public CloudStackDomainClient getDomainApi() {
      return domainContext.getApi();
   }

   @Override
   public CloudStackGlobalClient getGlobalApi() {
      return globalContext.getApi();
   }

   @Override
   public org.jclouds.rest.RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> getDomainContext() {
      return domainContext;
   }

   @Override
   public org.jclouds.rest.RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> getGlobalContext() {
      return globalContext;
   }
}
