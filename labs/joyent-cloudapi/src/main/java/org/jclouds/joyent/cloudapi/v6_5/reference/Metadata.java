/**
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
package org.jclouds.joyent.cloudapi.v6_5.reference;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.reflect.TypeToken;

/**
 * Known keys for metadata
 * 
 * @author Adrian Cole
 */
public enum Metadata {
   ROOT_AUTHORIZED_KEYS(typeToken(String.class).getType()),
   /**
    * If the dataset you create a machine from is set to generate passwords for
    * you, the username/password pairs will be returned in the metadata response
    * as a nested object, like:
    * 
    * <pre>
    * "credentials": {
    *     "root": "s8v9kuht5e",
    *     "admin": "mf4bteqhpy"
    *   }
    * </pre>
    */
   CREDENTIALS(new TypeToken<Map<String, String>>() {
      private static final long serialVersionUID = 1L;
   }.getType());

   private final Type valueType;

   Metadata(Type valueType) {
      this.valueType = valueType;
   }

   public String key() {
      return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, name());
   }

   /**
    * type of the value; 
    */
   public Type type() {
      return valueType;
   }

}
