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
package org.jclouds.cloudwatch.functions;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.date.DateService;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class ISO8601Format implements Function<Object, String> {

   private final DateService dateService;

   @Inject
   ISO8601Format(DateService dateService) {
      this.dateService = dateService;
   }

   @Override
   public String apply(Object from) {
      checkArgument(from instanceof Date, "this binder is only valid for Date!");
      return dateService.iso8601SecondsDateFormat(Date.class.cast(from));
   }
}
