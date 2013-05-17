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
package org.jclouds.ec2.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.ec2.domain.Tag;
import org.jclouds.ec2.xml.DescribeTagsResponseHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeTagsResponseTest")
public class DescribeTagsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_tags.xml");

      FluentIterable<Tag> expected = expected();

      DescribeTagsResponseHandler handler = injector.getInstance(DescribeTagsResponseHandler.class);
      FluentIterable<Tag> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }
   public FluentIterable<Tag> expected() {
      return FluentIterable.from(ImmutableSet.<Tag>builder()
               .add(Tag.builder()
                       .resourceId("i-5f4e3d2a")
                       .resourceType("instance")
                       .key("webserver")
                       .build())                
               .add(Tag.builder()
                       .resourceId("i-5f4e3d2a")
                       .resourceType("instance")
                       .key("stack")
                       .value("Production")
                       .build())                
               .add(Tag.builder()
                       .resourceId("i-12345678")
                       .resourceType("instance")
                       .key("database_server")
                       .build())                
               .add(Tag.builder()
                       .resourceId("i-12345678")
                       .resourceType("instance")
                       .key("stack")
                       .value("Test")
                       .build()).build());
   }
}
