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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;

import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * @author Andrei Savu
 */
public class ParseLoginResponseFromHttpResponse implements Function<HttpResponse, LoginResponse> {

   private ParseFirstJsonValueNamed<LoginResponse> parser;

   @Inject
   ParseLoginResponseFromHttpResponse(GsonWrapper gson) {
      this.parser = new ParseFirstJsonValueNamed<LoginResponse>(checkNotNull(gson, "gsonView"),
         new TypeLiteral<LoginResponse>(){}, "loginresponse");
   }

   @Override
   public LoginResponse apply(HttpResponse response) {
      checkNotNull(response, "response");

      LoginResponse login =  parser.apply(response);
      checkNotNull(login, "loginResponse");

      String jSessionId = get(Splitter.on("=").split(get(Splitter.on(";").trimResults().split(
         getOnlyElement(response.getHeaders().get("Set-Cookie"))), 0)), 1);
      
      return LoginResponse.builder().fromLoginResponse(login).jSessionId(jSessionId).build();
   }
}
