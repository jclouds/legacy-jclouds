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
package org.jclouds.aws.ec2.compute.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.predicates.InstancePresent;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2InstancePresent extends InstancePresent {

   private final AWSEC2Client client;

   @Inject
   public AWSEC2InstancePresent(AWSEC2Client client) {
      super(client);
      this.client = checkNotNull(client, "client");
   }

   @Override
   protected void refresh(RegionAndName instance) {
      if (instance.getName().indexOf("sir-") != 0)
         super.refresh(instance);
      else
         Iterables.getOnlyElement(client.getSpotInstanceServices().describeSpotInstanceRequestsInRegion(
                  instance.getRegion(), instance.getName()));
   }
}
