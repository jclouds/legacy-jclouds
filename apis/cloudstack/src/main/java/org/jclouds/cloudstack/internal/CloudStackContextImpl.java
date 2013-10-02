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
import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.CloudStackDomainApi;
import org.jclouds.cloudstack.CloudStackGlobalApi;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.Utils;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.location.Provider;
import org.jclouds.rest.ApiContext;

import com.google.common.reflect.TypeToken;

/**
 * @author Adrian Cole
 */
@Singleton
public class CloudStackContextImpl extends ComputeServiceContextImpl implements CloudStackContext {
   private final CloudStackApi client;
   private final ApiContext<CloudStackDomainApi> domainContext;
   private final ApiContext<CloudStackGlobalApi> globalContext;

   @Inject
   CloudStackContextImpl(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
         ComputeService computeService, Utils utils, CloudStackApi client,
         ApiContext<CloudStackDomainApi> domainContext,
         ApiContext<CloudStackGlobalApi> globalContext) {
      super(backend, backendType, computeService, utils);
      this.client = client;
      this.domainContext = domainContext;
      this.globalContext = globalContext;
   }

   @Override
   public CloudStackApi getApi() {
      return client;
   }

   @Override
   public CloudStackDomainApi getDomainApi() {
      return domainContext.getApi();
   }

   @Override
   public CloudStackGlobalApi getGlobalApi() {
      return globalContext.getApi();
   }

}
