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
package org.jclouds.cloudstack.predicates;

import javax.inject.Singleton;

import org.jclouds.cloudstack.domain.Template;

import com.google.common.base.Predicate;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class TemplatePredicates {
   public static Predicate<Template> isReady() {
      return Ready.INSTANCE;
   }

   public enum Ready implements Predicate<Template> {
      INSTANCE;
      @Override
      public boolean apply(Template arg0) {
         return arg0.isReady();
      }

      @Override
      public String toString() {
         return "isReady()";
      }
   }

   public static enum PasswordEnabled implements Predicate<Template> {
      INSTANCE;

      @Override
      public boolean apply(Template arg0) {
         return arg0.isPasswordEnabled();
      }

      @Override
      public String toString() {
         return "isPasswordEnabled()";
      }
   }

   public static Predicate<Template> isPasswordEnabled() {
      return PasswordEnabled.INSTANCE;
   }

}
