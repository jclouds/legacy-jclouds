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

import static com.google.common.collect.Iterables.getLast;

import javax.annotation.Resource;

import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Tests to see if a volume is detached.
 *
 * @author Karthik Arunachalam
 */
@Singleton
public class VolumeDetached implements Predicate<Attachment> {

   private final ElasticBlockStoreClient client;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VolumeDetached(ElasticBlockStoreClient client) {
      this.client = client;
   }

   public boolean apply(Attachment attachment) {
      logger.trace("looking for volume %s", attachment.getVolumeId());
      Volume volume = Iterables.getOnlyElement(client.describeVolumesInRegion(attachment
              .getRegion(), attachment.getVolumeId()));

      /*If attachment size is 0 volume is detached for sure.*/
      if (volume.getAttachments().size() == 0) {
         return true;
      }

      /* But if attachment size is > 0, then the attachment could be in any state.
         * So we need to check if the status is DETACHED (return true) or not (return false).
         */
      Attachment lastAttachment = getLast(volume.getAttachments());
      logger.trace("%s: looking for status %s: currently: %s", lastAttachment,
              Attachment.Status.DETACHED, lastAttachment.getStatus());
      return lastAttachment.getStatus() == Attachment.Status.DETACHED;
   }
}

