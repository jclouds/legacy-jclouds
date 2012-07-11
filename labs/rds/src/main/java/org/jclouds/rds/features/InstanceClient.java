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
package org.jclouds.rds.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.collect.PaginatedIterable;
import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rds.domain.Instance;
import org.jclouds.rds.options.ListInstancesOptions;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference" >doc</a>
 * @see InstanceAsyncClient
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface InstanceClient {

   /**
    * Retrieves information about the specified instance.
    * 
    * @param id
    *           The user-supplied instance identifier. If this parameter is specified, information
    *           from only the specific DB Instance is returned. This parameter isn't case sensitive.
    * 
    * @return null if not found
    */
   @Nullable
   Instance get(String id);

   /**
    * Returns information about provisioned RDS instances. If there are none, the action returns an
    * empty list.
    * 
    * <br/>
    * You can paginate the results using the {@link ListInstancesOptions parameter}
    * 
    * @param options
    *           the options describing the instances query
    * 
    * @return the response object
    */
   PaginatedIterable<Instance> list(ListInstancesOptions options);

   /**
    * Returns information about provisioned RDS instances.
    * 
    * @return the response object
    */
   PaginatedIterable<Instance> list();

   /**
    * Deletes the specified Instance.
    * 
    * <p/>
    * The DeleteDBInstance API deletes a previously provisioned RDS instance. A successful response
    * from the web service indicates the request was received correctly. If a final DBSnapshot is
    * requested the status of the RDS instance will be "deleting" until the DBSnapshot is created.
    * DescribeDBInstance is used to monitor the status of this operation. This cannot be canceled or
    * reverted once submitted.
    * 
    * 
    * @param id
    *           The DB Instance identifier for the DB Instance to be deleted. This parameter isn't
    *           case sensitive.
    */
   void delete(String id);

}
