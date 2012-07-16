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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rds.domain.SecurityGroup;
import org.jclouds.rds.options.ListSecurityGroupsOptions;

/**
 * Provides access to Amazon RDS via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference" >doc</a>
 * @see SecurityGroupAsyncApi
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface SecurityGroupApi {

   /**
    * Retrieves information about the specified {@link SecurityGroup}.
    * 
    * @param name
    *           Name of the security group to get information about.
    * @return null if not found
    */
   @Nullable
   SecurityGroup get(String name);

   /**
    * Returns a list of {@link SecurityGroup}s.
    * 
    * <br/>
    * You can paginate the results using the {@link ListSecurityGroupsOptions parameter}
    * 
    * @param options
    *           the options describing the security groups query
    * 
    * @return the response object
    */
   IterableWithMarker<SecurityGroup> list(ListSecurityGroupsOptions options);

   /**
    * Returns a list of {@link SecurityGroup}s.
    * 
    * @return the response object
    */
   PagedIterable<SecurityGroup> list();

   /**
    * Deletes a DB security group.
    * 
    * <h4>Naming Constraints</h4>
    * 
    * <ul>
    * <li>Must be 1 to 255 alphanumeric characters</li>
    * <li>First character must be a letter</li>
    * <li>Cannot end with a hyphen or contain two consecutive hyphens</li>
    * </ul>
    * 
    * @param name
    *           The name of the database security group to delete.
    * 
    *           <h4>Note</h4>
    * 
    *           You cannot delete the default security group.
    */
   void delete(String name);

}
