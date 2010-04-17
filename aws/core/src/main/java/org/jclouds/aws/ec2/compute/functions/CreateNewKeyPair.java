/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.compute.functions;

import java.security.SecureRandom;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateNewKeyPair implements Function<RegionTag, KeyPair> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;

   @Inject
   public CreateNewKeyPair(EC2Client ec2Client) {
      this.ec2Client = ec2Client;
   }

   @Override
   public KeyPair apply(RegionTag from) {
      return createNewKeyPairInRegion(from.getRegion(), from.getTag());
   }

   private KeyPair createNewKeyPairInRegion(Region region, String tag) {
      logger.debug(">> creating keyPair region(%s) tag(%s)", region, tag);
      KeyPair keyPair = null;
      while (keyPair == null) {
         try {
            keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region,
                     tag + "-" + new SecureRandom().nextInt(100));
            logger.debug("<< created keyPair(%s)", keyPair.getKeyName());
         } catch (AWSResponseException e) {
            if (!e.getError().getCode().equals("InvalidKeyPair.Duplicate")) {
               throw e;
            }
         }
      }
      return keyPair;
   }
}
