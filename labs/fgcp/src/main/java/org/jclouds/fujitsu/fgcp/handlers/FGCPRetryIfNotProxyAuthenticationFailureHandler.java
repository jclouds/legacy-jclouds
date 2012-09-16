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
package org.jclouds.fujitsu.fgcp.handlers;

import javax.annotation.Resource;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Singleton;

/**
 * Created by IntelliJ IDEA.
 * 
 * @author Dies Koper
 */
@Singleton
public class FGCPRetryIfNotProxyAuthenticationFailureHandler implements
      HttpRetryHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      int statusCode = response.getStatusCode();
      System.out.println("Response status code: " + statusCode);
      logger.error("StatusCode", statusCode);
      return true;
   }
}
