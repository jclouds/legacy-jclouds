package org.jclouds.vcloud.bluelock.compute;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.strategy.PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;

import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlueLockVCloudComputeClient extends BaseVCloudComputeClient {
   private final PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider;

   @Inject
   protected BlueLockVCloudComputeClient(
         PopulateDefaultLoginCredentialsForImageStrategy credentialsProvider,
         VCloudClient client, Predicate<String> successTester,
         Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      super(client, successTester, vAppStatusToNodeState);
      this.credentialsProvider = credentialsProvider;
   }

   @Override
   protected Map<String, String> parseAndValidateResponse(String templateId,
         VApp vAppResponse) {
      Credentials credentials = credentialsProvider.execute(client
            .getVAppTemplate(templateId));
      Map<String, String> toReturn = super.parseResponse(templateId,
            vAppResponse);
      toReturn.put("username", credentials.identity);
      toReturn.put("password", credentials.credential);
      return toReturn;
   }

}