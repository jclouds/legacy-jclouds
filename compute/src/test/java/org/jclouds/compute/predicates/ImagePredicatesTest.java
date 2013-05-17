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
package org.jclouds.compute.predicates;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class ImagePredicatesTest {
   ComputeService computeService = ContextBuilder.newBuilder("stub").build(ComputeServiceContext.class).getComputeService();

   public void testImageId() {
      Image first = Iterables.get(computeService.listImages(), 0);
      assert ImagePredicates.idEquals(first.getId()).apply(first);
      Image second = Iterables.get(computeService.listImages(), 1);
      assert !ImagePredicates.idEquals(first.getId()).apply(second);
   }

   public void testUserMetadataContains() {
      Image first = Iterables.get(computeService.listImages(), 0);
      first = ImageBuilder.fromImage(first).userMetadata(ImmutableMap.of("foo", "bar")).build();
      assert ImagePredicates.userMetadataContains("foo", "bar").apply(first);
      Image second = Iterables.get(computeService.listImages(), 1);
      second = ImageBuilder.fromImage(second).userMetadata(ImmutableMap.of("foo", "baz")).build();
      assert !ImagePredicates.userMetadataContains("foo", "bar").apply(second);
   }

}
