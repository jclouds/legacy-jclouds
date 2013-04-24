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
package org.jclouds.cloudwatch.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMultimap;
import org.jclouds.http.HttpRequest;

/**
 * Binds the alarm names request to the http request
 *
 * @author Jeremy Whitlock
 */
@Beta
public class AlarmNamesBinder implements org.jclouds.rest.Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      Iterable<String> alarmNames = (Iterable<String>) checkNotNull(input, "alarm names must be set");
      ImmutableMultimap.Builder<String, String> formParameters = ImmutableMultimap.builder();
      int alarmNameIndex = 1;

      for (String alarmName : alarmNames) {
         formParameters.put("AlarmNames.member." + alarmNameIndex, alarmName);
         alarmNameIndex++;
      }

      return (R) request.toBuilder().replaceFormParams(formParameters.build()).build();
   }

}
