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

import java.util.Set;
import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.options.ListDomainChildrenOptions;
import org.jclouds.cloudstack.options.ListDomainsOptions;

/**
 * Provides synchronous access to CloudStack Domain features available to Domain
 * Admin users.
 * 
 * @author Andrei Savu
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
public interface DomainDomainClient {

   /**
    * List domains with detailed information
    *
    * @param options
    *          list filtering optional arguments
    * @return
    *          set of domain instances or empty
    */
   Set<Domain> listDomains(ListDomainsOptions... options);

   /**
    * Get a domain by ID
    *
    * @param domainId
    *          domain ID
    * @return
    *          domain instance or null
    */
   Domain getDomainById(String domainId);

   /**
    * Lists all children domains belonging to a specified domain
    *
    * @param options
    *          list filtering optional arguments
    * @return
    *          set of domain instances or empty
    */
   Set<Domain> listDomainChildren(ListDomainChildrenOptions... options);
}
