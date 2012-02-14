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
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;

/**
 * Provides synchronous access to The REST API query interface.
 * 
 * @see TaskAsyncClient
 * @author grkvlt@apache.org
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface QueryClient {

   /**
    * REST API General queries handler.
    *
    * <pre>
    * GET /query
    * </pre>
    */
   QueryResultRecords query(String type, String filter);

   QueryResultRecords query(Integer page, Integer pageSize, String format, String type, String filter);

   /**
    * Retrieves a list of Catalogs by using REST API general QueryHandler.
    *
    * If filter is provided it will be applied to the corresponding result set.
    * Format determines the elements representation - references or records.
    * Default format is references.
    *
    * <pre>
    * GET /catalogs/query
    * </pre>
    */
   QueryResultRecords catalogsQuery(String filter);

   QueryResultRecords catalogsQuery(Integer page, Integer pageSize, String format, String filter);

   CatalogReferences catalogReferencesQuery(String filter);

   CatalogReferences catalogReferencesQuery(Integer page, Integer pageSize, String filter);
   
}
