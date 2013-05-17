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
package org.jclouds.openstack.swift.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.internal.BasePayloadTest;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseContainerListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseContainerListFromJsonResponseTest extends BasePayloadTest {

   @Test
   public void testApplyInputStream() {
      InputStream is = Strings2
               .toInputStream("[ {\"name\":\"test_container_1\",\"count\":2,\"bytes\":78}, {\"name\":\"test_container_2\",\"count\":1,\"bytes\":17} ]   ");
      Map<String, String> meta = Maps.newHashMap();

      List<ContainerMetadata> expects = ImmutableList.of(ContainerMetadata.builder().name("test_container_1").count( 2).bytes(78).metadata(meta).build(),
               ContainerMetadata.builder().name("test_container_2").count(1).bytes(17).metadata(meta).build());
      ParseJson<List<ContainerMetadata>> parser = i.getInstance(Key
               .get(new TypeLiteral<ParseJson<List<ContainerMetadata>>>() {
               }));
      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build()), expects);
   }
}
