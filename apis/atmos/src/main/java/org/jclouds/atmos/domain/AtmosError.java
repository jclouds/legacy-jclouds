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
package org.jclouds.atmos.domain;

/**
 * When an Atmos Storage request is in error, the client receives an error response.
 * 
 * Provides access to EMC Atmos Online Storage resources via their REST API.
 * 
 * @author Adrian Cole
 * 
 */
public class AtmosError {
   private final int code;
   private final String message;
   private String stringSigned;

   @Override
   public String toString() {
      return "AtmosError [code=" + code + ", message=" + message
               + (stringSigned != null ? (", stringSigned=" + stringSigned) : "") + "]";
   }

   public AtmosError(int code, String message) {
      this.code = code;
      this.message = message;
   }

   public int getCode() {
      return code;
   }

   public String getMessage() {
      return message;
   }

   public void setStringSigned(String stringSigned) {
      this.stringSigned = stringSigned;
   }

   /**
    * @return what jclouds signed before sending the request.
    */
   public String getStringSigned() {
      return stringSigned;
   }

}
