/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.predicates;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.features.ElasticBlockStoreApi;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * 
 * Tests to see if a volume is completed.
 * 
 * @author Adrian Cole
 */
@Singleton
public class VolumeAvailable implements Predicate<Volume> {

   private final ElasticBlockStoreApi client;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VolumeAvailable(ElasticBlockStoreApi client) {
      this.client = client;
   }

   public boolean apply(Volume volume) {
      logger.trace("looking for status on volume %s", volume.getId());
      volume = Iterables.getOnlyElement(client.describeVolumesInRegion(volume.getRegion(), volume
               .getId()));
      logger.trace("%s: looking for status %s: currently: %s", volume, Volume.Status.AVAILABLE,
               volume.getStatus());
      return volume.getStatus() == Volume.Status.AVAILABLE;
   }

}
