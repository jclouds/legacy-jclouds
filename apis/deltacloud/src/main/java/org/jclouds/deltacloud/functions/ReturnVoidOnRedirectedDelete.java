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
package org.jclouds.deltacloud.functions;

import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;

import org.jclouds.http.HttpResponseException;
import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

/**
 * When a delete operation is performed, Deltacloud returns 302.
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnVoidOnRedirectedDelete implements Function<Exception, Void> {

   public Void apply(Exception from) {
      HttpResponseException exception = Throwables2.getFirstThrowableOfType(from, HttpResponseException.class);
      if (exception != null && exception.getCommand().getCurrentRequest().getMethod().equals(HttpMethod.DELETE)
            && exception.getResponse().getStatusCode() == 302) {
         return null;
      }
      throw Throwables.propagate(from);
   }
}
