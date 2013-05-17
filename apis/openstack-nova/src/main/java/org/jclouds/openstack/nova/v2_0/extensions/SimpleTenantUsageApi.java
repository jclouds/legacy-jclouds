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
package org.jclouds.openstack.nova.v2_0.extensions;

import org.jclouds.openstack.nova.v2_0.domain.SimpleTenantUsage;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides asynchronous access to Simple Tenant Usage via the REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see SimpleTenantUsageAsyncApi
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.SIMPLE_TENANT_USAGE)
public interface SimpleTenantUsageApi {

   /**
    * Retrieve tenant_usage for all tenants
    *
    * @return the set of TenantUsage reports
    */
   FluentIterable<? extends SimpleTenantUsage> list();

   /**
    * Retrieve tenant_usage for a specified tenant
    *
    * @return the requested tenant usage
    */
   SimpleTenantUsage get(String tenantId);
}
