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
package org.jclouds.openstack.swift.domain.internal;

import static com.google.common.io.BaseEncoding.base16;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.openstack.swift.internal.BasePayloadTest;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ParseObjectInfoListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseObjectInfoListFromJsonResponseTest extends BasePayloadTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_container.json");
      Set<ObjectInfo> expects = ImmutableSet
            .<ObjectInfo> of(
                  ObjectInfoImpl.builder().container("container").name("test_obj_1")
                        .uri(URI.create("http://localhost/key/test_obj_1"))
                        .hash(base16().lowerCase().decode("4281c348eaf83e70ddce0e07221c3d28")).bytes(14l)
                        .contentType("application/octet-stream")
                        .lastModified(new SimpleDateFormatDateService().iso8601DateParse("2009-02-03T05:26:32.612Z"))
                        .build(),
                  ObjectInfoImpl.builder().container("container").name("test_obj_2")
                        .uri(URI.create("http://localhost/key/test_obj_2"))
                        .hash(base16().lowerCase().decode("b039efe731ad111bc1b0ef221c3849d0")).bytes(64l)
                        .contentType("application/octet-stream")
                        .lastModified(new SimpleDateFormatDateService().iso8601DateParse("2009-02-03T05:26:32.612Z"))
                        .build());
      GeneratedHttpRequest request = requestForArgs(ImmutableList.<Object> of("container",
            new ListContainerOptions[] { new ListContainerOptions() }));
      ParseObjectInfoListFromJsonResponse parser = i.getInstance(ParseObjectInfoListFromJsonResponse.class);
      parser.setContext(request);
      assertEquals(parser.apply(is).toString(), expects.toString());
   }
}
