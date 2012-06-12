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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.Quotas;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * The quotas extension enables limiters placed on the resources used per tenant (project) for virtual instances. It is
 * used with the OpenStack Compute API 1.1 for administrators who need to control the amount of volumes, memory, floating
 * IP addresses, instances, or cores allowed within a defined tenant or project.
 * <p/>
 * To use this extension, you need to have administrative rights to the tenants upon which you are placing quotas.
 *
 * @author Adam Lowe
 * @see QuotaAsyncClient
 * @see <a href="http://nova.openstack.org/api_ext/ext_quotas.html"/>
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.QUOTAS)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
public interface QuotaClient {

   /**
    * @return the quota settings for the tenant
    */
   Quotas getQuotasForTenant(String tenantId);

   /**
    * Update the quotas for a given tenant
    *
    * @return true if successful
    */
   Boolean updateQuotasForTenant(String tenantId, Quotas quotas);

   /**
    * @return the set of default quotas for the tenant
    */
   Quotas getDefaultQuotasForTenant(String tenantId);

}