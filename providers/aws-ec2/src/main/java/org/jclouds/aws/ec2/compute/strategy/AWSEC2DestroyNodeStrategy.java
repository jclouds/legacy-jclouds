package org.jclouds.aws.ec2.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.Map;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.ec2.compute.strategy.EC2DestroyNodeStrategy;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AWSEC2DestroyNodeStrategy extends EC2DestroyNodeStrategy {

   protected final AWSEC2Client client;
   protected final Map<String, Credentials> credentialStore;

   @Inject
   protected AWSEC2DestroyNodeStrategy(AWSEC2Client client, GetNodeMetadataStrategy getNode,
            Map<String, Credentials> credentialStore) {
      super(client, getNode);
      this.client = checkNotNull(client, "client");
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
   }

   @Override
   protected void destroyInstanceInRegion(String region, String id) {
      String spotId = id;
      if (id.indexOf("sir-") != 0) {
         try {
            spotId = getOnlyElement(
                     Iterables.concat(client.getInstanceServices().describeInstancesInRegion(region, id)))
                     .getSpotInstanceRequestId();
            credentialStore.remove("node#" + region + "/" + spotId);
         } catch (NoSuchElementException e) {
         }
         super.destroyInstanceInRegion(region, id);
      } else {
         client.getSpotInstanceServices().cancelSpotInstanceRequestsInRegion(region, spotId);
         credentialStore.remove("node#" + region + "/" + id);
      }

   }
}
