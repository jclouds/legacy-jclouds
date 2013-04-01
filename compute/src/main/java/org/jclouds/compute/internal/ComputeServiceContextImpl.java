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
package org.jclouds.compute.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.Context;
import org.jclouds.management.annotations.Management;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.Utils;
import org.jclouds.compute.management.ComputeServiceManagement;
import org.jclouds.internal.BaseView;
import org.jclouds.location.Provider;

import com.google.common.reflect.TypeToken;
import org.jclouds.management.ManagementContext;

/**
 * @author Adrian Cole
 */
@Singleton
public class ComputeServiceContextImpl extends BaseView implements ComputeServiceContext {
   private final ComputeService computeService;
   private final Utils utils;
   private final ComputeServiceManagement computeServiceManagement;

   @Inject
   public ComputeServiceContextImpl(@Provider Context backend, @Provider TypeToken<? extends Context> backendType,
            ComputeService computeService, Utils utils) {
      super(backend, backendType);
      this.computeService = checkNotNull(computeService, "computeService");
      this.utils = checkNotNull(utils, "utils");
      this.computeServiceManagement = new ComputeServiceManagement(this);
   }

   @PostConstruct
   public void init() {
      ManagementContext managementContext = unwrap().getManagementContext();
      if (managementContext != null) {
         managementContext.manage(computeServiceManagement);
      }
   }

   @Override
   public ComputeService getComputeService() {
      return computeService;
   }


   @Override
   public void close() {
      delegate().close();
      ManagementContext managementContext = unwrap().getManagementContext();
      if (managementContext != null) {
         managementContext.unmanage(computeServiceManagement);
      }
   }

   @Override
   public Utils getUtils() {
      return utils();
   }

   @Override
   public Utils utils() {
      return utils;
   }
}
