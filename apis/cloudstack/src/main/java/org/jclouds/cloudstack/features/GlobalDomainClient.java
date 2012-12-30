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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.options.CreateDomainOptions;
import org.jclouds.cloudstack.options.UpdateDomainOptions;

/**
 * Provides synchronous access to CloudStack Domain features available to Global
 * Admin users.
 * 
 * @author Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
public interface GlobalDomainClient extends DomainDomainClient {

   /**
    * Create new Domain
    *
    * @param name
    *       domain name
    * @param options
    *       optional arguments
    * @return
    *       domain instance
    */
   Domain createDomain(String name, CreateDomainOptions... options);

   /**
    * Update a domain
    *
    * @param domainId
    *       the ID of the domain
    * @param options
    *       optional arguments
    * @return
    *       domain instance
    */
   Domain updateDomain(String domainId, UpdateDomainOptions... options);

   /**
    * Delete domain (without deleting attached resources)
    *
    * @param id
    *    the domain ID
    */
   Void deleteOnlyDomain(String id);

   /**
    * Delete domain and cleanup all attached resources
    *
    * @param id
    *    the domain ID
    */
   Void deleteDomainAndAttachedResources(String id);
}
