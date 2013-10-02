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
package org.jclouds.ec2.util;

import static org.jclouds.ec2.domain.Tag.ResourceType.IMAGE;
import static org.jclouds.ec2.domain.Tag.ResourceType.INSTANCE;
import static org.testng.Assert.assertEquals;

import org.jclouds.ec2.domain.Tag;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test
public class TagsTest {
   Tag resourceTag1 = Tag.builder().resourceType(IMAGE).resourceId("1").key("key").value("value").build();

   public void testValueFunction() {
      assertEquals(Tags.valueFunction().apply(resourceTag1), "value");
   }

   public void testKeyFunction() {
      assertEquals(Tags.keyFunction().apply(resourceTag1), "key");
   }

   Tag resourceTag2 = Tag.builder().resourceType(IMAGE).resourceId("1").key("foo").value("bar").build();
   Tag resource2Tag1 = Tag.builder().resourceType(INSTANCE).resourceId("2").key("absent").build();
   Tag resource2Tag2 = Tag.builder().resourceType(INSTANCE).resourceId("2").key("hello").value("world").build();

   public void testResourceToTagsAsMap() {
      assertEquals(
            Tags.resourceToTagsAsMap(ImmutableSet.of(resourceTag1, resourceTag2, resource2Tag1, resource2Tag2)),
            ImmutableMap.of("1", ImmutableMap.of("key", "value", "foo", "bar"),
                            "2", ImmutableMap.of("absent", "", "hello", "world")));
   }

}
