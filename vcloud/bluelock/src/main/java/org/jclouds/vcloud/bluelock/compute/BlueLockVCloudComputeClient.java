package org.jclouds.vcloud.bluelock.compute;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class BlueLockVCloudComputeClient extends BaseVCloudComputeClient {
   @Inject
   protected BlueLockVCloudComputeClient(VCloudClient client, Predicate<String> successTester,
            @Named("NOT_FOUND") Predicate<VApp> notFoundTester,
            Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      super(client, successTester, notFoundTester, vAppStatusToNodeState);
   }

   @Override
   protected Map<String, String> parseResponse(VApp vAppResponse) {
      // https://forums.bluelock.com/faq.php?faq=vcloudexpressfaq
      return ImmutableMap.<String, String> of("id", vAppResponse.getId(), "username", (vAppResponse
               .getOperatingSystemDescription().indexOf("buntu") != -1) ? "express" : "root",
               "password", "ExpressPassword#1");
   }
}