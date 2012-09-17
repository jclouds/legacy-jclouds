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
package org.jclouds.openstack.swift.functions;

import com.google.common.base.Function;
import com.google.common.collect.Multimap;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.swift.reference.SwiftHeaders;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.openstack.swift.reference.SwiftHeaders.ACCOUNT_TEMPORARY_URL_KEY;

/**
 * @author Andrei Savu
 */
public class ParseTemporaryUrlKeyFromHeaders implements Function<HttpResponse, String> {

   @Override
   public String apply(HttpResponse httpResponse) {
      Multimap<String, String> headers = httpResponse.getHeaders();
      if (headers.containsKey(ACCOUNT_TEMPORARY_URL_KEY)) {
         return getOnlyElement(headers.get(ACCOUNT_TEMPORARY_URL_KEY));
      } else {
         return null;
      }
   }
}
