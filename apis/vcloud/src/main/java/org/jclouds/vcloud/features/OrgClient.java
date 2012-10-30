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
package org.jclouds.vcloud.features;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;

/**
 * Provides access to Org functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface OrgClient {

   /**
    * The response to a login request includes a list of the organizations to which the
    * authenticated user has access.
    * 
    * @return organizations indexed by name
    */
   Map<String, ReferenceType> listOrgs();

   Org getOrg(URI orgId);

   /**
    * This call returns a list of all vCloud Data Centers (vdcs), catalogs, and task lists within
    * the organization.
    * 
    * @param name
    *           organization name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org name that isn't present
    */
   Org findOrgNamed(@Nullable String name);

}
