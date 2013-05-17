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
package org.jclouds.scriptbuilder.statements.login;

import com.google.common.base.Function;

/**
 * Statements used to manipulate the shadow file
 * 
 * @author Adrian Cole
 */
public class ShadowStatements {

   /**
    * note must be run as root, and will either reset the root password, or whoever sudoed to root.
    */
   public static ReplaceShadowPasswordEntryOfLoginUser resetLoginUserPasswordTo(Function<String, String> cryptFunction,
         String password) {
      return new ReplaceShadowPasswordEntryOfLoginUser(cryptFunction, password);
   }
}
