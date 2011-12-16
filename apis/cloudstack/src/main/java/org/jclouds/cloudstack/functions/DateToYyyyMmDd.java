/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.cloudstack.functions;

import com.google.common.base.Function;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Convert a Date object into a "yyyy-MM-dd" String
 *
 * @author Richard Downer
 */
public class DateToYyyyMmDd implements Function<Object, String> {

   public String apply(Object input) {
      checkNotNull(input, "input cannot be null");
      checkArgument(input instanceof Date, "input must be a Date");

      return new SimpleDateFormat("yyyy-MM-dd").format((Date)input);
   }

}
