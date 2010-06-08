/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
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
package org.jclouds.ibmdev.functions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ParseJson;
import org.jclouds.ibmdev.domain.Key;

import com.google.common.collect.Sets;
import com.google.gson.Gson;

/**
 * @author Adrian Cole
 */
@Singleton
public class ParseKeysFromJson extends ParseJson<Set<? extends Key>> {
   @Inject
   public ParseKeysFromJson(Gson gson) {
      super(gson);
   }

   private static class KeyListResponse {
      Set<Key> keys = Sets.newLinkedHashSet();
   }
   
   @Override
   protected Set<? extends Key> apply(InputStream stream) {
      try {
         return gson.fromJson(new InputStreamReader(stream, "UTF-8"), KeyListResponse.class).keys;
      } catch (UnsupportedEncodingException e) {
         throw new RuntimeException("jclouds requires UTF-8 encoding", e);
      }
   }
}