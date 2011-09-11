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
package org.jclouds.ec2.predicates;

import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a snapshot is completed.
 * 
 * @author Adrian Cole
 */
@Singleton
public class SnapshotCompleted implements Predicate<Snapshot> {

   private final ElasticBlockStoreClient client;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SnapshotCompleted(ElasticBlockStoreClient client) {
      this.client = client;
   }

   public boolean apply(Snapshot snapshot) {
      logger.trace("looking for status on snapshot %s", snapshot.getId());

      snapshot = Iterables.getOnlyElement(client.describeSnapshotsInRegion(snapshot.getRegion(),
               snapshotIds(snapshot.getId())));
      logger.trace("%s: looking for status %s: currently: %s; progress %d/100", snapshot,
               Snapshot.Status.COMPLETED, snapshot.getStatus(), snapshot.getProgress());
      return snapshot.getStatus() == Snapshot.Status.COMPLETED;
   }

}
