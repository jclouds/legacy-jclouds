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
package org.jclouds.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.rest.Binder;

/**
 * Binds the AvailabilityZone to a form parameter if set.
 * 
 * @author Adrian Cole
 */
@Singleton
public class IfNotNullBindAvailabilityZoneToFormParam implements Binder {
   private final String param;

   @Inject
   protected IfNotNullBindAvailabilityZoneToFormParam() {
      this("Placement.AvailabilityZone");
   }

   protected IfNotNullBindAvailabilityZoneToFormParam(String param) {
      this.param = param;
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      if (input != null) {
         checkArgument(input instanceof String, "this binder is only valid for String!");
         return ModifyRequest.addFormParam(request, param, (String) input);
      }
      return request;
   }

}
