package org.jclouds.ec2.compute;

import static org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService.getJavaArgsForRequestAtIndex;
import static org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService.getJavaMethodForRequest;
import static org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService.getJavaMethodForRequestAtIndex;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.jclouds.compute.BaseTemplateBuilderLiveTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.Template;
import org.jclouds.ec2.options.DescribeAvailabilityZonesOptions;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.ec2.options.DescribeRegionsOptions;
import org.jclouds.ec2.services.AMIAsyncClient;
import org.jclouds.ec2.services.AvailabilityZoneAndRegionAsyncClient;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Module;

public abstract class EC2TemplateBuilderLiveTest extends BaseTemplateBuilderLiveTest {
   
   @Test
   public void testTemplateBuilderCanUseImageIdWithoutFetchingAllImages() throws Exception {
      Template defaultTemplate = context.getComputeService().templateBuilder().build();
      String defaultImageId = defaultTemplate.getImage().getId();
      String defaultImageProviderId = defaultTemplate.getImage().getProviderId();

      ComputeServiceContext context = null;
      try {
         // Track http commands
         final List<HttpCommand> commandsInvoked = Lists.newArrayList();
         context = new ComputeServiceContextFactory(setupRestProperties()).createContext(provider, 
                  ImmutableSet.<Module> of(new Log4JLoggingModule(), 
                  TrackingJavaUrlHttpCommandExecutorService.newTrackingModule(commandsInvoked)), 
                  setupProperties());
         
         Template template = context.getComputeService().templateBuilder().imageId(defaultImageId)
                  .build();
         assertEquals(template.getImage(), defaultTemplate.getImage());

         Collection<HttpCommand> filteredCommandsInvoked = Collections2.filter(commandsInvoked, new Predicate<HttpCommand>() {
            private final Collection<Method> ignored = ImmutableSet.of(
                     AvailabilityZoneAndRegionAsyncClient.class.getMethod("describeRegions", DescribeRegionsOptions[].class),
                     AvailabilityZoneAndRegionAsyncClient.class.getMethod("describeAvailabilityZonesInRegion", String.class, DescribeAvailabilityZonesOptions[].class));
            @Override
            public boolean apply(HttpCommand input) {
               return !ignored.contains(getJavaMethodForRequest(input));
            }
         });
         
         assert filteredCommandsInvoked.size() == 1 : commandsInvoked;
         assertEquals(getJavaMethodForRequestAtIndex(filteredCommandsInvoked, 0), AMIAsyncClient.class
                  .getMethod("describeImagesInRegion", String.class, DescribeImagesOptions[].class));
         assertDescribeImagesOptionsEquals((DescribeImagesOptions[])getJavaArgsForRequestAtIndex(filteredCommandsInvoked, 0).get(1), 
                  defaultImageProviderId);

      } finally {
         if (context != null)
            context.close();
      }
   }
   
   private static void assertDescribeImagesOptionsEquals(DescribeImagesOptions[] actual, String expectedImageId) {
      assertEquals(actual.length, 1);
      assertEquals(actual[0].getImageIds(), ImmutableSet.of(expectedImageId));
   }
}
