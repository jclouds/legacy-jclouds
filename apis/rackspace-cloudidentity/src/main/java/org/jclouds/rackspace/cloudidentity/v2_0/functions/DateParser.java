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
package org.jclouds.rackspace.cloudidentity.v2_0.functions;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.date.DateService;

import com.google.common.base.Function;

/**
 * Takes a Date and return a yyyy-MM-dd String.
 * 
 * @author Everett Toews
 */
@Singleton
public class DateParser implements Function<Object, String> {
   
   private final DateService dateService;
   
   @Inject
   DateParser(DateService dateService) {
      this.dateService = dateService;
   }
   
   @Override
   public String apply(Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Date, "This function is only valid for Dates!");
      Date date = Date.class.cast(input);

      return dateService.iso8601SecondsDateFormat(date);
   }

}
