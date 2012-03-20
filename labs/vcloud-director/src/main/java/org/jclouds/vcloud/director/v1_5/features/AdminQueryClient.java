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

import javax.management.relation.Role;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;

/**
 * Provides synchronous access to {@link AdminQuery} objects.
 * 
 * @see AdminQueryAsyncClient
 * @author Aled Sage
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminQueryClient extends QueryClient {
   
   /**
    * Retrieves a list of {@link Group}s for organization the org admin belongs to by using REST API general QueryHandler
    * 
    * <pre>
    * GET /admin/groups/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords groupsQueryAll();

   /** @see #queryAll() */
   QueryResultRecords groupsQuery(String filter);

   /**
    * Retrieves a list of {@link Org}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/orgs/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords orgsQueryAll();

   /** @see #queryAll() */
   QueryResultRecords orgsQuery(String filter);
   
   /**
    * Retrieves a list of {@link Right}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/rights/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords rightsQueryAll();

   /** @see #queryAll() */
   QueryResultRecords rightsQuery(String filter);
   
   /**
    * Retrieves a list of {@link Role}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/roles/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords rolesQueryAll();

   /** @see #queryAll() */
   QueryResultRecords rolesQuery(String filter);
   
   /**
    * Retrieves a list of {@link User}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/strandedUsers/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords strandedUsersQueryAll();

   /** @see #queryAll() */
   QueryResultRecords strandedUsersQuery(String filter);
   
   /**
    * Retrieves a list of {@link User}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/users/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords usersQueryAll();

   /** @see #queryAll() */
   QueryResultRecords usersQuery(String filter);
   
   /**
    * Retrieves a list of {@link Vdc}s by using REST API general QueryHandler.
    *
    * <pre>
    * GET /admin/vdcs/query
    * </pre>
    *
    * @see #queryAll(String)
    */
   QueryResultRecords vdcsQueryAll();

   /** @see #queryAll() */
   QueryResultRecords vdcsQuery(String filter);
}
