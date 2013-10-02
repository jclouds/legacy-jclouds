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
package org.jclouds.openstack.nova.v2_0.functions;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * Parsers for extracting a single field value from a response and comparing it to an expected value
 */
public class FieldValueResponseParsers {
   @Singleton
   public static class StatusEnabledResponseParser extends FieldValueResponseParser<String> {
      @Inject
      public StatusEnabledResponseParser(GsonWrapper wrapper) {
         super(wrapper, "status", "enabled");
      }
   }

   @Singleton
   public static class StatusDisabledResponseParser extends FieldValueResponseParser<String> {
      @Inject
      public StatusDisabledResponseParser(GsonWrapper wrapper) {
         super(wrapper, "status", "disabled");
      }
   }

   @Singleton
   public static class MaintenanceModeEnabledResponseParser extends FieldValueResponseParser<String> {
      @Inject
      public MaintenanceModeEnabledResponseParser(GsonWrapper wrapper) {
         super(wrapper, "maintenance_mode", "on_maintenance");
      }
   }

   @Singleton
   public static class MaintenanceModeDisabledResponseParser extends FieldValueResponseParser<String> {
      @Inject
      public MaintenanceModeDisabledResponseParser(GsonWrapper wrapper) {
         super(wrapper, "maintenance_mode", "off_maintenance");
      }
   }

   @Singleton
   public static class PowerIsStartupResponseParser extends FieldValueResponseParser<String> {
      @Inject
      public PowerIsStartupResponseParser(GsonWrapper wrapper) {
         super(wrapper, "power_action", "startup");
      }
   }

   @Singleton
   public static class PowerIsShutdownResponseParser extends FieldValueResponseParser<String> {
      @Inject
      public PowerIsShutdownResponseParser(GsonWrapper wrapper) {
         super(wrapper, "power_action", "shutdown");
      }
   }

   @Singleton
   public static class PowerIsRebootResponseParser extends FieldValueResponseParser<String> {
      @Inject
      public PowerIsRebootResponseParser(GsonWrapper wrapper) {
         super(wrapper, "power_action", "reboot");
      }
   }

   public abstract static class FieldValueResponseParser<T> implements Function<HttpResponse, Boolean> {
      private final T expectedValue;
      private final ParseFirstJsonValueNamed<T> valueParser;

      public FieldValueResponseParser(GsonWrapper wrapper, String fieldName, T expectedValue) {
         valueParser = new ParseFirstJsonValueNamed<T>(wrapper, new TypeLiteral<T>() {
         }, fieldName);
         this.expectedValue = expectedValue;
      }

      @Override
      public Boolean apply(HttpResponse response) {
         return Objects.equal(expectedValue, valueParser.apply(response));
      }
   }

}
