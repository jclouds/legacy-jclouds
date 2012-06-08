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
import org.jclouds.openstack.nova.v2_0.domain.QuotaClass;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * Provides synchronous access to Quota Classes via the REST API.
 * <p/>
 * To use this extension, you need to have administrative rights to the tenants upon which you are placing quotas.
 *
 * @author Adam Lowe
 * @see QuotaClassAsyncClient
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.quota_classes.html"/>
 * @see <a href="http://wiki.openstack.org/QuotaClass"/>
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.QUOTA_CLASSES)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
public interface QuotaClassClient {

   /**
    * @return the quota settings for the tenant
    */
   QuotaClass getQuotaClass(String id);

   /**
    * Update the quotas for a given tenant
    *
    * @return true if successful
    */
   Boolean updateQuotaClass(String id, QuotaClass quotas);

}