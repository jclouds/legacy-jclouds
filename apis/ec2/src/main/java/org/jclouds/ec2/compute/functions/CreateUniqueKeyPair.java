/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.ec2.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Supplier;

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
   protected Supplier<String> randomSuffix;

   @Inject
   public CreateUniqueKeyPair(EC2Client ec2Client, Supplier<String> randomSuffix) {
      this.ec2Client = ec2Client;
      this.randomSuffix = randomSuffix;
   }

   @Override
   public KeyPair apply(RegionAndName from) {
      return createNewKeyPairInRegion(from.getRegion(), from.getName());
   }

   @VisibleForTesting
   KeyPair createNewKeyPairInRegion(String region, String tag) {
      checkNotNull(region, "region");
      checkNotNull(tag, "tag");
      logger.debug(">> creating keyPair region(%s) tag(%s)", region, tag);
      KeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, getNextName(region, tag));
            logger.debug("<< created keyPair(%s)", keyPair.getKeyName());
         } catch (IllegalStateException e) {

         }
      }
      return keyPair;
   }

   private String getNextName(String region, String tag) {
      return String.format("jclouds#%s#%s#%s", tag, region, randomSuffix.get());
   }
}
