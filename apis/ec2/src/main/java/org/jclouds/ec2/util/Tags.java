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

import static com.google.common.collect.Multimaps.index;

import java.util.Map;

import org.jclouds.ec2.domain.Tag;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
public class Tags {
   private Tags() {
   }

   /**
    * maps the input on {@link Tag#getResourceId()} with a value of a map of its {@link Tag#getKey()} to
    * {@link Tag#getValue()}
    */
   public static Map<String, Map<String, String>> resourceToTagsAsMap(Iterable<Tag> tags) {
      return Maps.transformValues(index(tags, resourceIdFunction()).asMap(),
            new Function<Iterable<Tag>, Map<String, String>>() {
               @Override
               public Map<String, String> apply(Iterable<Tag> in) {
                  return Maps.transformValues(Maps.uniqueIndex(in, keyFunction()), valueFunction());
               }
            });
   }

   public static enum ValueFunction implements Function<Tag, String> {
      INSTANCE;
      @Override
      public String apply(Tag in) {
         return in.getValue().or("");
      }

      @Override
      public String toString() {
         return "getValue()";
      }
   }

   public static Function<Tag, String> valueFunction() {
      return ValueFunction.INSTANCE;
   }

   public static enum KeyFunction implements Function<Tag, String> {
      INSTANCE;
      @Override
      public String apply(Tag in) {
         return in.getKey();
      }

      @Override
      public String toString() {
         return "getKey()";
      }
   }

   public static Function<Tag, String> keyFunction() {
      return KeyFunction.INSTANCE;
   }

   public static enum ResourceIdFunction implements Function<Tag, String> {
      INSTANCE;
      @Override
      public String apply(Tag in) {
         return in.getResourceId();
      }

      @Override
      public String toString() {
         return "getResourceId()";
      }
   }

   public static Function<Tag, String> resourceIdFunction() {
      return ResourceIdFunction.INSTANCE;
   }

}
