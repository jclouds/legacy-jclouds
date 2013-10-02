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
package org.jclouds.json;

import java.lang.reflect.Type;

/**
 * @author Adrian Cole
 */
public interface Json {
   /**
    * Serialize the object into json. If the object is a generic type, use
    * {@link #toJson(Object, Type)}
    */
   String toJson(Object src);

   /**
    * Serialize the generic object into json. If the object is not a generic, use
    * {@link #toJson(Object, Type)}
    */
   String toJson(Object src, Type type);

   /**
    * Deserialize the generic object from json. If the object is not a generic type, use
    * {@link #fromJson(Object, Class)}
    */
   <T> T fromJson(String json, Type type);

   /**
    * Deserialize the object from json. If the object is a generic type, use
    * {@link #fromJson(Object, Type)}
    */
   <T> T fromJson(String json, Class<T> classOfT);

}
