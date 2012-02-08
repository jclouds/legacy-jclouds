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
package org.jclouds.vcloud.director.v1_5.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;

/**
 * Provides synchronous access to Org.
 * <p/>
 * 
 * @see OrgAsyncClient
 * @see <a href= "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID=" />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface OrgClient {

   /**
    * Retrieves a list of organizations.
    * 
    * @return a list of organizations
    */
   OrgList getOrgList();

   /**
    * Retrieves an organization.
    * 
    * @return the org or null if not found
    */
   Org getOrg(String orgId);
   
   /**
    * Retrieves an list of the organization's metadata
    * 
    * @return a list of metadata
    */
   Metadata getMetadata(String orgId);

   /**
    * Retrieves a metadata
    * 
    * @return the metadata or null if not found
    */
   MetadataEntry getMetadataEntry(String orgId, String key);
}
