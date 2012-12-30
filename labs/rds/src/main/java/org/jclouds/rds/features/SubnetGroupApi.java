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
import org.jclouds.rds.domain.SubnetGroup;
import org.jclouds.rds.options.ListSubnetGroupsOptions;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference" >doc</a>
 * @see SubnetGroupAsyncApi
 * @author Adrian Cole
 */
public interface SubnetGroupApi {

   /**
    * Retrieves information about the specified {@link SubnetGroup}.
    * 
    * @param name
    *           Name of the subnet group to get information about.
    * @return null if not found
    */
   @Nullable
   SubnetGroup get(String name);

   /**
    * Returns a list of {@link SubnetGroup}s.
    * 
    * <br/>
    * You can paginate the results using the {@link ListSubnetGroupsOptions parameter}
    * 
    * @param options
    *           the options describing the subnet groups query
    * 
    * @return the response object
    */
   IterableWithMarker<SubnetGroup> list(ListSubnetGroupsOptions options);

   /**
    * Returns a list of {@link SubnetGroup}s.
    * 
    * @return the response object
    */
   PagedIterable<SubnetGroup> list();

   /**
    * Deletes a DB subnet group.
    * 
    * <h4>Note</h4>
    * 
    * The specified database subnet group must not be associated with any DB instances.
    * 
    * <h4>Note</h4>
    * 
    * By design, if the SubnetGroup does not exist or has already been deleted, DeleteSubnetGroup
    * still succeeds.
    * 
    * 
    * @param name
    *           The name of the database subnet group to delete.
    * 
    *           <h4>Note</h4>
    * 
    *           You cannot delete the default subnet group.
    */
   void delete(String name);

}
