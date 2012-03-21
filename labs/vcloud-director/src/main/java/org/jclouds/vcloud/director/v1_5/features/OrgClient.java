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

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;

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
   Org getOrg(URI orgRef);

   /**
    * Modifies a catalog control access.
    *
    * <pre>
    * POST /org/{id}/catalog/{catalogId}/action/controlAccess
    * </pre>
    *
    * @return the control access information
    */
   ControlAccessParams modifyControlAccess(URI orgRef, String catalogId, ControlAccessParams params);

   /**
    * Retrieves the catalog control access information.
    *
    * <pre>
    * GET /org/{id}/catalog/{catalogId}/controlAccess
    * </pre>
    *
    * @return the control access information
    */
   ControlAccessParams getControlAccess(URI orgRef, String catalogId);
   
   /**
    * @return synchronous access to {@link Metadata.Readable} features
    */
   @Delegate
   MetadataClient.Readable getMetadataClient();
}
