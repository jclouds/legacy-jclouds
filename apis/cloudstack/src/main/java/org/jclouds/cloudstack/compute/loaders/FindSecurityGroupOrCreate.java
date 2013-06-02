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
package org.jclouds.cloudstack.compute.loaders;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.SecurityGroup;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.domain.ZoneAndName;
import org.jclouds.cloudstack.domain.ZoneSecurityGroupNamePortsCidrs;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.Atomics;

/**
 * 
 * @author Adrian Cole
 * @author Andrew Bayer
 */
public class FindSecurityGroupOrCreate extends CacheLoader<ZoneAndName, SecurityGroup> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final CloudStackClient client;
   protected final Function<ZoneSecurityGroupNamePortsCidrs, SecurityGroup> groupCreator;

   @Inject
   public FindSecurityGroupOrCreate(CloudStackClient client,
                                    Function<ZoneSecurityGroupNamePortsCidrs, SecurityGroup> groupCreator) {
      this.client = checkNotNull(client, "client");
      this.groupCreator = checkNotNull(groupCreator, "groupCreator");
   }

   @Override
   public SecurityGroup load(ZoneAndName in) {
      SecurityGroup group = client.getSecurityGroupClient().getSecurityGroupByName(in.getName());
      if (group != null) {
         return group;
      } else {
         return createNewSecurityGroup(in);
      }
   }

   private SecurityGroup createNewSecurityGroup(ZoneAndName in) {
      checkState(
               checkNotNull(in, "ZoneSecurityGrioupNamePortsCidrs") instanceof ZoneSecurityGroupNamePortsCidrs,
               "programming error: when issuing get to this cacheloader, you need to pass an instance of ZoneSecurityGroupNamePortsCidrs, not %s",
               in);
      ZoneSecurityGroupNamePortsCidrs zoneSecurityGroupNamePortsCidrs = ZoneSecurityGroupNamePortsCidrs.class.cast(in);
      return groupCreator.apply(zoneSecurityGroupNamePortsCidrs);
   }

   @Override
   public String toString() {
      return "returnExistingSecurityGroupInZoneOrCreateAsNeeded()";
   }

}
