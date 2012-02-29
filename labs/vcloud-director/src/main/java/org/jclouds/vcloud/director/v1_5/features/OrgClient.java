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
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.domain.URISupplier;

/**
 * Provides synchronous access to Org.
 * <p/>
 * 
 * @see OrgAsyncClient
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface OrgClient {

   /**
    * Retrieves a list of organizations.
    *
    * <pre>
    * GET /org
    * </pre>
    * 
    * @return a list of organizations
    */
   OrgList getOrgList();

   /**
    * Retrieves an organization.
    *
    * <pre>
    * GET /org/{id}
    * </pre>
    * 
    * @return the org or null if not found
    */
   Org getOrg(URISupplier orgRef);
   
   /**
    * Retrieves an list of the organization's metadata
    *
    * <pre>
    * GET /org/{id}/metadata
    * </pre>
    * 
    * @return a list of metadata
    */
   Metadata getOrgMetadata(URISupplier orgRef);

   /**
    * Retrieves a metadata entry.
    *
    * <pre>
    * GET /org/{id}/metadata{key}
    * </pre>
    * 
    * @return the metadata entry or null if not found
    */
   MetadataValue getOrgMetadataValue(URISupplier orgRef, String key);
}
