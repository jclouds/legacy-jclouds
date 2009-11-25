/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rimuhosting.miro.functions;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.rimuhosting.miro.domain.NewInstanceResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;


@Singleton
public class ParseNewInstanceResponseFromJsonResponse extends ParseJson<NewInstanceResponse> {
   @Inject
   public ParseNewInstanceResponseFromJsonResponse(Gson gson) {
      super(gson);
   }

   @Override
   protected NewInstanceResponse apply(InputStream stream) {
      Type setType = new TypeToken<Map<String, NewInstanceResponse>>() {
      }.getType();
      try {
         Map<String, NewInstanceResponse> responseMap = gson.fromJson(new InputStreamReader(stream, "UTF-8"), setType);
         return responseMap.values().iterator().next();
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}