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
package org.jclouds.gogrid.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.gogrid.domain.Server;
import org.jclouds.http.functions.ParseJson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Parses {@link Server servers} from a json string.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseServerListFromJsonResponse extends
      ParseJson<SortedSet<Server>> {

   @Inject
   ParseServerListFromJsonResponse(Gson gson) {
      super(gson);
   }

   public SortedSet<Server> apply(InputStream stream) {
      Type setType = new TypeToken<GenericResponseContainer<Server>>() {
      }.getType();
      GenericResponseContainer<Server> response;
      try {
         response = gson.fromJson(new InputStreamReader(stream, "UTF-8"),
               setType);
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
      return response.getList();
   }
}