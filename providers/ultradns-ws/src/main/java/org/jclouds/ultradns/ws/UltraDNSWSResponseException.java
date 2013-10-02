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
package org.jclouds.ultradns.ws;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * @see UltraDNSWSError
 * @author Adrian Cole
 */
public class UltraDNSWSResponseException extends HttpResponseException {

   private static final long serialVersionUID = 5493782874839736777L;

   private final UltraDNSWSError error;

   public UltraDNSWSResponseException(HttpCommand command, HttpResponse response, UltraDNSWSError error) {
      super(error.toString(), command, response);
      this.error = error;
   }

   public UltraDNSWSError getError() {
      return error;
   }

}
