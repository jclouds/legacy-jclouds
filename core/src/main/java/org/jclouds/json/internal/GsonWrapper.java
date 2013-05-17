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
package org.jclouds.json.internal;

import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.json.Json;

import com.google.common.collect.ForwardingObject;
import com.google.gson.Gson;

/**
 * @author Adrian Cole
 */
@Singleton
public class GsonWrapper extends ForwardingObject implements Json  {

   private final Gson gson;

   @Inject
   public GsonWrapper(Gson gson) {
      this.gson = gson;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T fromJson(String json, Type type) {
      return (T) gson.fromJson(json, type);
   }

   @Override
   public <T> T fromJson(String json, Class<T> classOfT) {
      return gson.fromJson(json, classOfT);
   }

   @Override
   public String toJson(Object src) {
      return gson.toJson(src);
   }

   @Override
   public String toJson(Object src, Type type) {
      return gson.toJson(src, type);
   }

   @Override
   public Gson delegate() {
      return gson;
   }

}
