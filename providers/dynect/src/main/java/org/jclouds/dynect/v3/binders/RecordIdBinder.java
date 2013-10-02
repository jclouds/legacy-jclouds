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
package org.jclouds.dynect.v3.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.http.Uris.uriBuilder;

import java.net.URI;

import org.jclouds.dynect.v3.domain.RecordId;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMap;

public class RecordIdBinder implements Binder {
   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object recordId) {
      RecordId valueToAppend = RecordId.class.cast(checkNotNull(recordId, "recordId"));
      URI path = uriBuilder(request.getEndpoint()).appendPath("/{type}Record/{zone}/{fqdn}/{id}").build(
            ImmutableMap.<String, Object> builder()
                        .put("type", valueToAppend.getType())
                        .put("zone", valueToAppend.getZone())
                        .put("fqdn", valueToAppend.getFQDN())
                        .put("id", valueToAppend.getId()).build());
      return (R) request.toBuilder().endpoint(path).build();
   }
}
