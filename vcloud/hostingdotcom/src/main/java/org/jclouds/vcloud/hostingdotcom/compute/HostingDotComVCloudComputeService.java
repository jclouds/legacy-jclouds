package org.jclouds.vcloud.hostingdotcom.compute;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Location;
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
   public HostingDotComVCloudComputeService(VCloudClient client,
            Provider<TemplateBuilder> templateBuilderProvider,
            Provider<Map<String, ? extends Image>> images,
            Provider<Map<String, ? extends Size>> sizes,
            Provider<Map<String, ? extends Location>> locations, ComputeUtils utils,
            Predicate<String> successTester, @Named("NOT_FOUND") Predicate<VApp> notFoundTester,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      super(client, templateBuilderProvider, images, sizes, locations, utils, successTester,
               notFoundTester, executor);
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