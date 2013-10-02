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

import org.jclouds.openstack.nova.v2_0.domain.QuotaClass;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;

/**
 * Provides synchronous access to Quota Classes via the REST API.
 * <p/>
 * To use this extension, you need to have administrative rights to the tenants upon which you are placing quotas.
 *
 * @author Adam Lowe
 * @see QuotaClassAsyncApi
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.quota_classes.html"/>
 * @see <a href="http://wiki.openstack.org/QuotaClass"/>
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.QUOTA_CLASSES)
public interface QuotaClassApi {

   /**
    * @return the quota settings for the tenant
    */
   QuotaClass get(String id);

   /**
    * Update the quotas for a given tenant
    *
    * @return true if successful
    */
   boolean update(String id, QuotaClass quotas);

}
