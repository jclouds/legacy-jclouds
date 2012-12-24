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
package org.jclouds.savvis.vpdc.options;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.http.Uris.uriBuilder;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindGetVMOptions implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkArgument(input instanceof GetVMOptions[], "this binder is only valid for GetVAppOptions!");
      GetVMOptions[] options = GetVMOptions[].class.cast(input);
      if (options.length > 0 && options[0].isWithPowerState())
         return (R) request.toBuilder()
               .endpoint(uriBuilder(request.getEndpoint()).appendPath("withpowerstate").build()).build();
      else
         return request;
   }
}
