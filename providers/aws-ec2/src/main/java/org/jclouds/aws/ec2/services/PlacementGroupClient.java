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
package org.jclouds.aws.ec2.services;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides Placement Group services for EC2. For more information, refer to the Amazon EC2
 * Developer Guide.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 45, timeUnit = TimeUnit.SECONDS)
public interface PlacementGroupClient {

   /**
    * Creates a placement group that you launch cluster compute instances into. You must give the
    * group a name unique within the scope of your account.
    * 
    * @param region
    *           Region to create the placement group in.
    * @param name
    *           The name of the placement group..
    * @param strategy
    *           The placement group strategy.
    * @see #describePlacementGroupsInRegion
    * @see #deletePlacementGroupInRegion
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreatePlacementGroup.html"
    *      />
    */
   void createPlacementGroupInRegion(@Nullable String region, String name, String strategy);

   /**
    * like {@link #createPlacementGroupInRegion(String,String,String) except that the strategy is default: "cluster".
    */
   void createPlacementGroupInRegion(@Nullable String region, String name);
   
   /**
    * Deletes a placement group from your account. You must terminate all instances in the placement group before deleting it.
    * 
    * @param region
    *           Region to delete the placement from from
    * @param name
    *           Name of the security group to delete.
    * 
    * @see #describePlacementGroupsInRegion
    * @see #createPlacementGroupInRegion
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DeletePlacementGroup.html"
    *      />
    */
   void deletePlacementGroupInRegion(@Nullable String region, String name);

   /**
    * 
    * Returns information about one or more placement groups in your account.
    * 
    * @param region
    *           The bundleTask ID is tied to the Region.
    * @param groupNames
    *           The name of the placement group. You can specify more than one in the request, or
    *           omit the parameter if you want information about all your placement groups. By
    *           default, all placement groups are described
    * 
    * @see #deletePlacementGroupInRegion
    * @see #createPlacementGroupInRegion
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribePlacementGroups.html"
    *      />
    */
   Set<PlacementGroup> describePlacementGroupsInRegion(@Nullable String region,
            String... groupNames);
}
