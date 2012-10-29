/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.binders.infrastructure;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.binders.AppendToPath;
import org.jclouds.abiquo.functions.infrastructure.ParseMachineId;
import org.jclouds.http.HttpRequest;

/**
 * Append the {@link Machine} id to the request URI.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AppendMachineIdToPath extends AppendToPath {
   private ParseMachineId parser;

   @Inject
   public AppendMachineIdToPath(final ParseMachineId parser) {
      super();
      this.parser = parser;
   }

   @Override
   protected <R extends HttpRequest> String getValue(final R request, final Object input) {
      return parser.apply(input);
   }

}
