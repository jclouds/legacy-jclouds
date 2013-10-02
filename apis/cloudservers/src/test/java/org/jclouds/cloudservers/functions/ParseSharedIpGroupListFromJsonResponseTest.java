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
package org.jclouds.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.cloudservers.domain.SharedIpGroup;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseSharedIpGroupListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseSharedIpGroupListFromJsonResponseTest {

   Injector i = Guice.createInjector(new GsonModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_sharedipgroups.json");

      List<SharedIpGroup> expects = ImmutableList.of(SharedIpGroup.builder().id(1234).name("Shared IP Group 1").build(),
            SharedIpGroup.builder().id(5678).name("Shared IP Group 2").build());

      UnwrapOnlyJsonValue<List<SharedIpGroup>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<SharedIpGroup>>>() {
            }));
      List<SharedIpGroup> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response, expects);

   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/test_list_sharedipgroups_detail.json");

      UnwrapOnlyJsonValue<List<SharedIpGroup>> parser = i.getInstance(Key
            .get(new TypeLiteral<UnwrapOnlyJsonValue<List<SharedIpGroup>>>() {
            }));
      List<SharedIpGroup> response = parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build());

      assertEquals(response.get(0).getId(), 1234);
      assertEquals(response.get(0).getName(), "Shared IP Group 1");
      assertEquals(response.get(0).getServers(), ImmutableList.of(422, 3445));

      assertEquals(response.get(1).getId(), 5678);
      assertEquals(response.get(1).getName(), "Shared IP Group 2");
      assertEquals(response.get(1).getServers(), ImmutableList.of(23203, 2456, 9891));

   }

}
