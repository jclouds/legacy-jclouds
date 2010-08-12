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

package org.jclouds.chef.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.Automatic;
import org.jclouds.ohai.config.multibindings.MapBinder;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class ChefUtils {

   public static Date fromOhaiTime(JsonBall ohaiDate) {
      return new Date(Long.parseLong(checkNotNull(ohaiDate, "ohaiDate").toString().replaceAll("\\.[0-9]*$", "")));
   }

   public static JsonBall toOhaiTime(long millis) {
      return new JsonBall(millis + "");
   }

   public static MapBinder<String, Supplier<JsonBall>> ohaiAutomaticAttributeBinder(Binder binder) {
      MapBinder<String, Supplier<JsonBall>> mapbinder = MapBinder.newMapBinder(binder, new TypeLiteral<String>() {
      }, new TypeLiteral<Supplier<JsonBall>>() {
      }, Automatic.class);
      return mapbinder;
   }

   /**
    * 
    * @return NoSuchElementException if no element in the runList is a role.
    */
   public static String findRoleInRunList(List<String> runList) {
      final Pattern pattern = Pattern.compile("^role\\[(.*)\\]$");
      String roleToParse = Iterables.find(runList, new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return pattern.matcher(input).matches();
         }

      });
      Matcher matcher = pattern.matcher(roleToParse);
      matcher.find();
      return matcher.group(1);
   }
}