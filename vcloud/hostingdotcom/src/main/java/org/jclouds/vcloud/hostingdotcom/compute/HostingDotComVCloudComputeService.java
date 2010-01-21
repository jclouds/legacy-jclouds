package org.jclouds.vcloud.hostingdotcom.compute;

import static com.google.common.base.Preconditions.checkState;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeService;
import org.jclouds.vcloud.compute.VCloudTemplate;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Singleton
public class HostingDotComVCloudComputeService extends VCloudComputeService {

   @Inject
   public HostingDotComVCloudComputeService(VCloudClient client,
            Provider<Set<? extends Image>> images, Provider<SortedSet<? extends Size>> sizes,
            Provider<Set<? extends VCloudTemplate>> templates, Predicate<String> successTester,
            Predicate<InetSocketAddress> socketTester) {
      super(client, images, sizes, templates, successTester, socketTester);
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