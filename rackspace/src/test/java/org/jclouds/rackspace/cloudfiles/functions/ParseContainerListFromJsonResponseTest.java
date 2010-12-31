/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.rackspace.cloudfiles.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.rackspace.cloudfiles.domain.ContainerMetadata;
import org.jclouds.rackspace.config.RackspaceParserModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseContainerListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseContainerListFromJsonResponseTest {
   Injector i = Guice.createInjector(new RackspaceParserModule(), new GsonModule());

   @Test
   public void testApplyInputStream() {
      InputStream is = Strings2
            .toInputStream("[ {\"name\":\"test_container_1\",\"count\":2,\"bytes\":78}, {\"name\":\"test_container_2\",\"count\":1,\"bytes\":17} ]   ");

      List<ContainerMetadata> expects = ImmutableList.of(new ContainerMetadata("test_container_1", 2, 78),
            new ContainerMetadata("test_container_2", 1, 17));
      ParseJson<List<ContainerMetadata>> parser = i.getInstance(Key
            .get(new TypeLiteral<ParseJson<List<ContainerMetadata>>>() {
            }));
      assertEquals(parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(is))), expects);
   }
}
