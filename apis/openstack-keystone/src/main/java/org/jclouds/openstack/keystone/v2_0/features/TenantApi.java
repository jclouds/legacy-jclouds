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
package org.jclouds.openstack.keystone.v2_0.features;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * Provides synchronous access to the KeyStone Tenant API.
 * <p/>
 * 
 * @author Adam Lowe
 * @see TenantAsyncApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-identity-service/2.0/content/Tenant_Operations.html"
 *      />
 */
public interface TenantApi {

   /**
    * The operation returns a list of tenants which the current token provides access to.
    */
   PagedIterable<? extends Tenant> list();

   PaginatedCollection<? extends Tenant> list(PaginationOptions options);

   /**
    * Retrieve information about a tenant, by tenant ID
    * 
    * @return the information about the tenant
    */
   Tenant get(String tenantId);

   /**
    * Retrieve information about a tenant, by tenant name
    * <p/>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/956687 )
    * 
    * @return the information about the tenant
    */
   Tenant getByName(String tenantName);
}
