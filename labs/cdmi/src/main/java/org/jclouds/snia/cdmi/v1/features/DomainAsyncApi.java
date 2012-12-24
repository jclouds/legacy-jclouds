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
package org.jclouds.snia.cdmi.v1.features;

import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;

/**
 * Domain Object Resource Operations
 * 
 * @see DomainApi
 * @author Adrian Cole
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@RequestFilters({ BasicAuthenticationAndTenantId.class, StripExtraAcceptHeader.class })
@Headers(keys = "X-CDMI-Specification-Version", values = "{jclouds.api-version}")
public interface DomainAsyncApi {

}
