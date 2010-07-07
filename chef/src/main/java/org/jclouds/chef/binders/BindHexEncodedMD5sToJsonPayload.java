/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.chef.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.binders.BindToStringPayload;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindHexEncodedMD5sToJsonPayload extends BindToStringPayload {

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(input, "input") instanceof Set,
               "this binder is only valid for Set!");

      Set<String> hexEncodedmd5s = (Set<String>) input;

      StringBuilder builder = new StringBuilder();
      builder.append("{\"checksums\":{");

      for (String hexEncodedmd5 : hexEncodedmd5s)
         builder.append(String.format("\"%s\":null,", hexEncodedmd5));
      builder.deleteCharAt(builder.length() - 1);
      builder.append("}}");
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_TYPE,
               ImmutableSet.of(MediaType.APPLICATION_JSON));
      super.bindToRequest(request, builder.toString());
   }

}