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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rds.domain.Instance;
import org.jclouds.rds.domain.InstanceRequest;
import org.jclouds.rds.options.ListInstancesOptions;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference" >doc</a>
 * @see InstanceAsyncApi
 * @author Adrian Cole
 */
public interface InstanceApi {
   /**
    * Creates a new DB instance in a random, system-chosen Availability Zone in the endpoint's
    * region.
    * 
    * @param id
    *           unique id of the new instance
    * @param instanceRequest
    *           parameters to create the instance with
    * @return new instance being created
    */
   Instance create(String id, InstanceRequest instanceRequest);

   /**
    * Creates a new DB instance in the specified {@code availabilityZone}
    * 
    * @param id
    *           unique id of the new instance
    * @param instanceRequest
    *           parameters to create the instance with
    * @param availabilityZone
    *           The EC2 Availability Zone that the database instance will be created in
    * @return new instance being created
    */
   Instance createInAvailabilityZone(String id, InstanceRequest instanceRequest, String availabilityZone);

   /**
    * Creates a Multi-AZ deployment. This is not compatible with Microsoft SQL Server.
    * 
    * @param id
    *           unique id of the new instance
    * @param instanceRequest
    *           parameters to create the instance with
    * @return new instance being created
    */
   Instance createMultiAZ(String id, InstanceRequest instanceRequest);

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
   IterableWithMarker<Instance> list(ListInstancesOptions options);

   /**
    * Returns information about provisioned RDS instances.
    * 
    * @return the response object
    */
   PagedIterable<Instance> list();

   /**
    * Deletes the specified Instance, skipping final snapshot.
    * 
    * <p/>
    * The DeleteDBInstance API deletes a previously provisioned RDS instance. A successful response
    * from the web service indicates the request was received correctly. This cannot be canceled or
    * reverted once submitted.
    * 
    * 
    * @param id
    *           The DB Instance identifier for the DB Instance to be deleted. This parameter isn't
    *           case sensitive.
    * @return final state of instance or null if not found
    */
   Instance delete(String id);

   /**
    * Deletes the specified Instance.
    * 
    * <p/>
    * The DeleteDBInstance API deletes a previously provisioned RDS instance. A successful response
    * from the web service indicates the request was received correctly. The status of the RDS
    * instance will be "deleting" until the DBSnapshot is created. DescribeDBInstance is used to
    * monitor the status of this operation. This cannot be canceled or reverted once submitted.
    * 
    * 
    * @param id
    *           The DB Instance identifier for the DB Instance to be deleted. This parameter isn't
    *           case sensitive.
    * @param snapshotId
    *           The DBSnapshotIdentifier of the new DBSnapshot created when SkipFinalSnapshot is set
    *           to false.
    * @return final state of instance or null if not found
    */
   Instance deleteAndSaveSnapshot(String id, String snapshotId);
}
