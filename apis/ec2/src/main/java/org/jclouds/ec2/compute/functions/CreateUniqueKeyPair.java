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
package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateUniqueKeyPair implements Function<RegionAndName, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;
   protected final GroupNamingConvention.Factory namingConvention;

   @Inject
   public CreateUniqueKeyPair(EC2Client ec2Client, GroupNamingConvention.Factory namingConvention) {
      this.ec2Client = ec2Client;
      this.namingConvention = checkNotNull(namingConvention, "namingConvention");
   }

   @Override
   public KeyPair apply(RegionAndName from) {
      return createNewKeyPairInRegion(from.getRegion(), from.getName());
   }

   @VisibleForTesting
   KeyPair createNewKeyPairInRegion(String region, String group) {
      checkNotNull(region, "region");
      checkNotNull(group, "group");
      logger.debug(">> creating keyPair region(%s) group(%s)", region, group);
      KeyPair keyPair = null;
      String prefix = group;
      
      while (keyPair == null) {
         String keyName = namingConvention.create().uniqueNameForGroup(prefix);
         try {
            keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, keyName);
         } catch (IllegalStateException e) {
            logger.trace("   invalid keyname (%s in %s); retrying", keyName, region);
         }
      }
      
      logger.debug("<< created keyPair(%s)", keyPair);
      return keyPair;
   }
}
