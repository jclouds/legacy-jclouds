/*
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

package org.jclouds.googlecompute.handlers;

import org.jclouds.googlecompute.config.GoogleComputeParserModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author David Alves
 */
public class MetadataBinder implements Binder {

   @Inject
   private BindToJsonPayload jsonBinder;

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      Map<String, String> metadataEntries = (Map<String, String>) checkNotNull(input, "input metadata");
      return jsonBinder.bindToRequest(request, new GoogleComputeParserModule.Metadata(metadataEntries));
   }
}
