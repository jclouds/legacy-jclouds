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

package org.jclouds.abiquo.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Singleton;

import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.abiquo.model.rest.RESTLink;

/**
 * Binds the given link to the uri.
 * 
 * @author Francesc Montserrat
 */
@Singleton
public class BindLinkToPath extends BindToPath {

   @Override
   protected String getNewEndpoint(final GeneratedHttpRequest gRequest, final Object input) {
      checkArgument(checkNotNull(input, "input") instanceof RESTLink, "this binder is only valid for RESTLink objects");

      return ((RESTLink) input).getHref();
   }

}
