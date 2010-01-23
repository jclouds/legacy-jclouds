package org.jclouds.vcloud.hostingdotcom.compute;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.compute.VCloudComputeService;
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
   HostingDotComVCloudComputeService(VCloudClient client,
            Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Set<? extends Image>> images, Provider<Set<? extends Size>> sizes,
            Predicate<String> successTester, ComputeUtils utils,
            @Named("NOT_FOUND") Predicate<VApp> notFoundTester) {
      super(client, templateBuilderProvider, images, sizes, utils, successTester, notFoundTester);
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