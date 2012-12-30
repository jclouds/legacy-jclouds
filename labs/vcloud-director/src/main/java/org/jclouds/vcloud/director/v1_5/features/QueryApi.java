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

import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryList;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.domain.query.VAppReferences;

/**
 * Provides synchronous access to the REST API query interface.
 * 
 * @see QueryAsyncApi
 * @author grkvlt@apache.org
 */
public interface QueryApi {

   // TODO Add a typed object for filter syntax, or at least a fluent builder
   
   /**
    * REST API query {@link Link} list.
    *
    * <pre>
    * GET /query
    * </pre>
    */
   QueryList queryList();

   /**
    * Retrieves a list of entities by using REST API general QueryHandler.
    *
    * If filter is provided it will be applied to the corresponding result set.
    * Format determines the elements representation - references or records.
    * Default format is references.
    *
    * <pre>
    * GET /query
    * </pre>
    *
    * @see #queryList()
    * @see #query(String, String)
    * @see #query(Integer, Integer, String, String, String)
    */
   QueryResultRecords queryAll(String type);

   /** @see #queryAll() */
   QueryResultRecords query(String type, String filter);

   /** @see #queryAll() */
   QueryResultRecords query(Integer page, Integer pageSize, String format, String type, String filter);

   /**
    * Retrieves a list of {@link Catalog}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /catalogs/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords catalogsQueryAll();

   /** @see #queryAll() */
   QueryResultRecords catalogsQuery(String filter);

   /** @see #queryAll() */
   QueryResultRecords catalogsQuery(Integer page, Integer pageSize, String filter);


   /**
    * Retrieves a list of {@link CatalogReference}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /catalogs/query?format=references
    * </pre>
    *
    * @see #queryAll(String)
    */
   CatalogReferences catalogReferencesQueryAll();

   /** @see #catalogReferencesQueryAll() */
   CatalogReferences catalogReferencesQuery(String filter);

   /** @see #catalogReferencesQueryAll() */
   CatalogReferences catalogReferencesQuery(Integer page, Integer pageSize, String filter);

   /**
    * Retrieves a list of {@link VAppTemplate}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /vAppTemplates/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords vAppTemplatesQueryAll();

   /** @see #queryAll() */
   QueryResultRecords vAppTemplatesQuery(String filter);

   /**
    * Retrieves a list of {@link VApp}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /vApps/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords vAppsQueryAll();

   /** @see #queryAll() */
   QueryResultRecords vAppsQuery(String filter);

   /** @see #queryAll() */
   QueryResultRecords vAppsQuery(Integer page, Integer pageSize, String filter);

   /**
    * Retrieves a list of {@link VAppReference}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /vApps/query?format=references
    * </pre>
    *
    * @see #queryAll(String)
    */
   /** @see #queryAll() */
   VAppReferences vAppReferencesQueryAll();

   /** @see #queryAll() */
   VAppReferences vAppReferencesQuery(String filter);

   /** @see #queryAll() */
   VAppReferences vAppReferencesQuery(Integer page, Integer pageSize, String filter);

   /**
    * Retrieves a list of {@link Vm}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /vms/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords vmsQueryAll();

   /** @see #queryAll() */
   QueryResultRecords vmsQuery(String filter);

   /**
    * Retrieves a list of {@link Media}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /mediaList/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords mediaListQueryAll();

   /** @see #queryAll() */
   QueryResultRecords mediaListQuery(String filter);
   
}
