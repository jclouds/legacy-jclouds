/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseSharedIpGroupListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudSharedIpGroups.ParseSharedIpGroupListFromJsonResponseTest")
public class ParseSharedIpGroupListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_list_sharedipgroups.json");

      List<SharedIpGroup> expects = ImmutableList.of(new SharedIpGroup(1234, "Shared IP Group 1"),
               new SharedIpGroup(5678, "Shared IP Group 2"));
      ParseSharedIpGroupListFromJsonResponse parser = new ParseSharedIpGroupListFromJsonResponse(i
               .getInstance(Gson.class));
      assertEquals(parser.apply(is), expects);
   }

   public void testApplyInputStreamDetails() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/cloudservers/test_list_sharedipgroups_detail.json");

      ParseSharedIpGroupListFromJsonResponse parser = new ParseSharedIpGroupListFromJsonResponse(i
               .getInstance(Gson.class));
      List<SharedIpGroup> response = parser.apply(is);
      assertEquals(response.get(0).getId(), 1234);
      assertEquals(response.get(0).getName(), "Shared IP Group 1");
      assertEquals(response.get(0).getServers(), ImmutableList.of(422, 3445));

      assertEquals(response.get(1).getId(), 5678);
      assertEquals(response.get(1).getName(), "Shared IP Group 2");
      assertEquals(response.get(1).getServers(), ImmutableList.of(23203, 2456, 9891));

   }

}
