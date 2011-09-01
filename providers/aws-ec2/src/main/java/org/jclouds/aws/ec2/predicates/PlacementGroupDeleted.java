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
package org.jclouds.aws.ec2.predicates;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.PlacementGroup;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class PlacementGroupDeleted implements Predicate<PlacementGroup> {

   private final AWSEC2Client client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public PlacementGroupDeleted(AWSEC2Client client) {
      this.client = client;
   }

   public boolean apply(PlacementGroup group) {
      logger.trace("looking for state on group %s", group);
      try {
         group = refresh(group);
      } catch (NoSuchElementException e) {
         return true;
      }
      logger.trace("%s: looking for group state %s: currently: %s", group.getName(), PlacementGroup.State.DELETED,
               group.getState());
      return group.getState() == PlacementGroup.State.DELETED;
   }

   private PlacementGroup refresh(PlacementGroup group) {
      return Iterables.getOnlyElement(client.getPlacementGroupServices().describePlacementGroupsInRegion(
               group.getRegion(), group.getName()));
   }
}
