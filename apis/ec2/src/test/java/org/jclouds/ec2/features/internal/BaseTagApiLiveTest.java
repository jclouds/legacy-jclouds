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
package org.jclouds.ec2.features.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.logging.Logger;

import org.jclouds.ec2.domain.Tag;
import org.jclouds.ec2.features.TagApi;
import org.jclouds.ec2.internal.BaseEC2ApiLiveTest;
import org.jclouds.ec2.util.TagFilterBuilder;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * tests ability to tag, filter, and delete tags from a resource.
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public abstract class BaseTagApiLiveTest extends BaseEC2ApiLiveTest {

   private Resource resource;

   private Tag tag;
   private Tag tag2;

   public void testApplyTag() {

      api().applyToResources(ImmutableSet.of("foo"), ImmutableSet.of(resource.id));

      tag = api().filter(new TagFilterBuilder().resourceId(resource.id).key("foo").build()).get(0);

      Logger.getAnonymousLogger().info("created tag: " + tag);

      assertEquals(tag.getKey(), "foo");
      assertEquals(tag.getResourceId(), resource.id);
      assertEquals(tag.getResourceType(), resource.type);
      assertFalse(tag.getValue().isPresent());
   }

   public void testApplyTagWithValue() {

      api().applyToResources(ImmutableMap.of("type", "bar"), ImmutableSet.of(resource.id));

      tag2 = api().filter(new TagFilterBuilder().resourceId(resource.id).key("type").build()).get(0);

      Logger.getAnonymousLogger().info("created tag: " + tag2);

      assertEquals(tag2.getKey(), "type");
      assertEquals(tag2.getResourceId(), resource.id);
      assertEquals(tag2.getResourceType(), resource.type);
      assertEquals(tag2.getValue().get(), "bar");
   }

   @Test(dependsOnMethods = { "testApplyTag", "testApplyTagWithValue" })
   protected void testList() {
      assertTrue(retry(new Predicate<Iterable<Tag>>() {
         public boolean apply(Iterable<Tag> input) {
            return api().list().filter(new Predicate<Tag>() {
               @Override
               public boolean apply(Tag in) {
                  return in.getResourceId().equals(resource.id);
               }
            }).toSet().equals(input);
         }
      }, 600, 200, 200, MILLISECONDS).apply(ImmutableSet.of(tag, tag2)));
   }

   @Test(dependsOnMethods = "testList")
   public void testDeleteTags() {
      // shouldn't delete with the incorrect values
      api().conditionallyDeleteFromResources(ImmutableMap.of(tag.getKey(), "FOO", tag2.getKey(), "FOO"),
            ImmutableSet.of(tag.getResourceId(), tag2.getResourceId()));

      assertEquals(tagsForResource().size(), 2);

      api().deleteFromResources(ImmutableSet.of(tag.getKey(), tag2.getKey()),
            ImmutableSet.of(tag.getResourceId(), tag2.getResourceId()));

      assertEquals(tagsForResource().size(), 0);

      Logger.getAnonymousLogger().info("tags deleted: " + tag + ", " + tag2);
   }

   private FluentIterable<Tag> tagsForResource() {
      return api().filter(new TagFilterBuilder().resourceId(resource.id).build());
   }

   @Override
   @BeforeClass(groups = "live")
   public void setup() {
      super.setup();
      resource = checkNotNull(createResourceForTagging(System.getProperty("user.name") + "-tag"), "resource");
   }

   public static class Resource {
      public String id;
      public String type;

      /**
       * 
       * @param id
       *           ex. {sg-abc23d}
       * @param type
       *           a type listed in {@link Tag.ResourceType}
       */
      public Resource(String id, String type) {
         this.id = checkNotNull(id, "id");
         this.type = checkNotNull(type, "type of %s", id);
      }
   }

   protected abstract Resource createResourceForTagging(String prefix);

   protected abstract void cleanupResource(Resource resource);
   
   protected TagApi api() {
      Optional<? extends TagApi> tagOption = api.getTagApi();
      if (!tagOption.isPresent())
         throw new SkipException("tag api not present");
      return tagOption.get();
   }
   
   @AfterClass(groups = "live")
   protected void tearDown() {
      if (resource != null)
         cleanupResource(resource);
      super.tearDown();
   }
}
