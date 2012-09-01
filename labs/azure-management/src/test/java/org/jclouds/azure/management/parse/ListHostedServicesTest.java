/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.azure.management.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.azure.management.domain.DetailedHostedServiceProperties;
import org.jclouds.azure.management.domain.HostedService.Status;
import org.jclouds.azure.management.domain.HostedServiceWithDetailedProperties;
import org.jclouds.azure.management.xml.ListHostedServicesHandler;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListHostedServicesTest")
public class ListHostedServicesTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/hostedservices.xml");

      Set<HostedServiceWithDetailedProperties> expected = expected();

      ListHostedServicesHandler handler = injector.getInstance(ListHostedServicesHandler.class);
      Set<HostedServiceWithDetailedProperties> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   protected final DateService dateService = new SimpleDateFormatDateService();

   public Set<HostedServiceWithDetailedProperties> expected() {
      return ImmutableSet.<HostedServiceWithDetailedProperties>builder()
                         .add(HostedServiceWithDetailedProperties.builder()
                                      .url(URI.create("https://management.core.windows.net/eb0347c3-68d4-4550-9b39-5e7e0f92f7db/services/hostedservices/neotys"))
                                      .name("neotys")
                                      .properties(DetailedHostedServiceProperties.builder()
                                               .description("Implicitly created hosted service2012-08-06  14:55")
                                               .location("West Europe")
                                               .label("neotys")
                                               .rawStatus("Created")
                                               .status(Status.CREATED)
                                               .created(dateService.iso8601SecondsDateParse("2012-08-06T14:55:17Z"))
                                               .lastModified(dateService.iso8601SecondsDateParse("2012-08-06T15:50:34Z"))
                                               .build())
                                      .build())
                         .add(HostedServiceWithDetailedProperties.builder()
                                      .url(URI.create("https://management.core.windows.net/eb0347c3-68d4-4550-9b39-5e7e0f92f7db/services/hostedservices/neotys3"))
                                      .name("neotys3")
                                      .properties(DetailedHostedServiceProperties.builder()
                                               .location("West Europe")
                                               .label("neotys3")
                                               .rawStatus("Created")
                                               .status(Status.CREATED)
                                               .created(dateService.iso8601SecondsDateParse("2012-08-07T09:00:02Z"))
                                               .lastModified(dateService.iso8601SecondsDateParse("2012-08-07T09:00:02Z"))
                                               .build())
                                      .build()).build();
   }
}
