package org.jclouds.aws.ec2.compute.functions;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.KeyPairCredentials;
import org.jclouds.aws.ec2.compute.domain.RegionTag;
import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

@Singleton
public class CreateKeyPairIfNeeded implements Function<RegionTag, KeyPairCredentials> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;

   @Inject
   public CreateKeyPairIfNeeded(EC2Client ec2Client) {
      this.ec2Client = ec2Client;
   }

   @Override
   public KeyPairCredentials apply(RegionTag from) {
      return new KeyPairCredentials("root", createKeyPairInRegion(from.getRegion(), from.getTag()));
   }

   private KeyPair createKeyPairInRegion(Region region, String name) {
      logger.debug(">> creating keyPair region(%s) name(%s)", region, name);
      KeyPair keyPair;
      try {
         keyPair = ec2Client.getKeyPairServices().createKeyPairInRegion(region, name);
         logger.debug("<< created keyPair(%s)", keyPair.getKeyName());
      } catch (AWSResponseException e) {
         if (e.getError().getCode().equals("InvalidKeyPair.Duplicate")) {
            keyPair = Iterables.getLast(ec2Client.getKeyPairServices().describeKeyPairsInRegion(
                     region, name));
            logger.debug("<< reused keyPair(%s)", keyPair.getKeyName());
         } else {
            throw e;
         }
      }
      return keyPair;
   }
}
