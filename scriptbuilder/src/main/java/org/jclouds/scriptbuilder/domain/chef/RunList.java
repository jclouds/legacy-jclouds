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
package org.jclouds.scriptbuilder.domain.chef;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A Run list to be executed in a Chef Solo run.
 * 
 * @author Ignasi Barrera
 */
public class RunList {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private ImmutableList.Builder<String> runlist = ImmutableList.builder();

      public Builder recipe(String recipe) {
         this.runlist.add("recipe[" + checkNotNull(recipe, "recipe must be set") + "]");
         return this;
      }

      public Builder recipes(Iterable<String> recipes) {
         this.runlist.addAll(Lists.newArrayList(transform(checkNotNull(recipes, "recipes must be set"),
               new Function<String, String>() {
                  @Override
                  public String apply(String input) {
                     return "recipe[" + input + "]";
                  }
               })));
         return this;
      }

      public Builder role(String role) {
         this.runlist.add("role[" + checkNotNull(role, "role must be set") + "]");
         return this;
      }

      public Builder roles(Iterable<String> roles) {
         this.runlist.addAll(Lists.newArrayList(transform(checkNotNull(roles, "roles must be set"),
               new Function<String, String>() {
                  @Override
                  public String apply(String input) {
                     return "role[" + input + "]";
                  }
               })));
         return this;
      }

      public RunList build() {
         return new RunList(runlist.build());
      }
   }

   private List<String> runlist;

   protected RunList(List<String> runlist) {
      this.runlist = ImmutableList.<String> copyOf(checkNotNull(runlist, "runlist must be set"));
   }

   public List<String> getRunlist() {
      return runlist;
   }

   @Override
   public String toString() {
      return "[" + Joiner.on(',').join(transform(runlist, new Function<String, String>() {
         @Override
         public String apply(String input) {
            return "\"" + input + "\"";
         }
      })) + "]";
   }

}
