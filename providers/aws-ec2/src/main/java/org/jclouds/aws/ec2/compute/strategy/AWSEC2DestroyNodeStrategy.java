package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.ec2.compute.strategy.EC2DestroyNodeStrategy;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2DestroyNodeStrategy extends EC2DestroyNodeStrategy {

   protected final AWSEC2Client client;

   @Inject
   protected AWSEC2DestroyNodeStrategy(AWSEC2Client client, GetNodeMetadataStrategy getNode) {
      super(client, getNode);
      this.client = checkNotNull(client, "client");
   }

   @Override
   protected void destroyInstanceInRegion(String region, String id) {
      if (id.indexOf("sir-") != 0) {
         super.destroyInstanceInRegion(region, id);
      } else {
         client.getSpotInstanceServices().cancelSpotInstanceRequestsInRegion(region, id);
      }
   }
}
