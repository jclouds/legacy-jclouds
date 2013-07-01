/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.compute;

import static org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService.getArgsForRequestAtIndex;
import static org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService.getInvokerOfRequest;
import static org.jclouds.http.internal.TrackingJavaUrlHttpCommandExecutorService.getInvokerOfRequestAtIndex;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseTemplateBuilderLiveTest;
import org.jclouds.ec2.options.DescribeAvailabilityZonesOptions;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.ec2.options.DescribeRegionsOptions;
import org.jclouds.ec2.features.AMIApi;
import org.jclouds.ec2.features.AvailabilityZoneAndRegionApi;
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
      Template defaultTemplate = view.getComputeService().templateBuilder().build();
      String defaultImageId = defaultTemplate.getImage().getId();
      String defaultImageProviderId = defaultTemplate.getImage().getProviderId();

      ComputeServiceContext context = null;
      try {
         // Track http commands
         final List<HttpCommand> commandsInvoked = Lists.newArrayList();
         context = createView(
               setupProperties(),
               ImmutableSet.<Module> of(new Log4JLoggingModule(),
                     TrackingJavaUrlHttpCommandExecutorService.newTrackingModule(commandsInvoked)));
         
         Template template = context.getComputeService().templateBuilder().imageId(defaultImageId)
                  .build();
         assertEquals(template.getImage(), defaultTemplate.getImage());

         Collection<HttpCommand> filteredCommandsInvoked = Collections2.filter(commandsInvoked, new Predicate<HttpCommand>() {
            private final Collection<Method> ignored = ImmutableSet.of(
                     AvailabilityZoneAndRegionApi.class.getMethod("describeRegions", DescribeRegionsOptions[].class),
                     AvailabilityZoneAndRegionApi.class.getMethod("describeAvailabilityZonesInRegion", String.class, DescribeAvailabilityZonesOptions[].class));
            @Override
            public boolean apply(HttpCommand input) {
               return !ignored.contains(getInvokerOfRequest(input));
            }
         });
         
         assert filteredCommandsInvoked.size() == 1 : commandsInvoked;
         assertEquals(getInvokerOfRequestAtIndex(filteredCommandsInvoked, 0), AMIApi.class
                  .getMethod("describeImagesInRegion", String.class, DescribeImagesOptions[].class));
         assertDescribeImagesOptionsEquals((DescribeImagesOptions[])getArgsForRequestAtIndex(filteredCommandsInvoked, 0).get(1), 
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
