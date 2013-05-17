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
package org.jclouds.rest.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.net.InternetDomainName.from;
import static com.google.common.net.InternetDomainName.isValid;
import static org.jclouds.http.Uris.uriBuilder;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.net.InternetDomainName;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindAsHostPrefix implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(R request, Object payload) {
      checkNotNull(payload, "hostprefix");
      checkArgument(isValid(request.getEndpoint().getHost()), "this is only valid for hostnames: " + request);
      InternetDomainName name = from(request.getEndpoint().getHost()).child(payload.toString());
      return (R) request.toBuilder().endpoint(uriBuilder(request.getEndpoint()).host(name.name()).build()).build();
   }
}
