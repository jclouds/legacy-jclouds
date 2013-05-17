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

import org.jclouds.openstack.nova.v2_0.domain.ServerWithSecurityGroups;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;

/**
 * Provides synchronous access to Server details including security group, referred to as the CREATESERVEREXT extension
 * in the nova documentation
 * <p/>
 * NOTE: the equivalent to listServersInDetail() isn't available at the other end, so not extending ServerApi at this
 * time.
 *
 * @author Adam Lowe
 * @see org.jclouds.openstack.nova.v2_0.features.ServerApi
 * @see ServerWithSecurityGroupsAsyncApi
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.createserverext.html"/>
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.CREATESERVEREXT)
public interface ServerWithSecurityGroupsApi {

   /**
    * Retrieve details of the specified server, including security groups
    *
    * @param id id of the server
    * @return server or null if not found
    */
   ServerWithSecurityGroups get(String id);

}
