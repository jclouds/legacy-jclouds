package org.jclouds.vcloud.hostingdotcom.compute;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeState;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.BaseVCloudComputeClient;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class HostingDotComVCloudComputeClient extends BaseVCloudComputeClient {

   @Inject
   protected HostingDotComVCloudComputeClient(VCloudClient client,
            Predicate<String> successTester, @Named("NOT_FOUND") Predicate<VApp> notFoundTester,
            Map<VAppStatus, NodeState> vAppStatusToNodeState) {
      super(client, successTester, notFoundTester, vAppStatusToNodeState);
   }

   @Override
   protected Map<String, String> parseResponse(VApp vAppResponse) {
      checkState(vAppResponse instanceof HostingDotComVApp,
               "bad configuration, vApp should be an instance of "
                        + HostingDotComVApp.class.getName());
      HostingDotComVApp hVApp = HostingDotComVApp.class.cast(vAppResponse);
      return ImmutableMap.<String, String> of("id", vAppResponse.getId(), "username", hVApp
               .getUsername(), "password", hVApp.getPassword());
   }

}