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

import org.jclouds.googlecompute.options.FirewallOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Inject;
import java.net.URI;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author David Alves
 */
public class FirewallBinder implements MapBinder {

   @Inject
   private BindToJsonPayload jsonBinder;

   /**
    * {@inheritDoc}
    */
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      FirewallOptions options = (FirewallOptions) checkNotNull(postParams.get("options"), "firewallOptions");
      String name = (String) checkNotNull(postParams.get("name"), "name");
      URI network = (URI) checkNotNull(postParams.get("network"), "network");
      options.name(name);
      options.network(network);
      return bindToRequest(request, options);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return jsonBinder.bindToRequest(request, input);
   }
}