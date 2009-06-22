/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.http.handlers;

import java.io.IOException;

import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.util.Utils;

/**
 * 
 * @author Adrian Cole
 */
public class CloseContentAndSetExceptionHandler implements HttpErrorHandler {

   public void handle(HttpFutureCommand<?> command, HttpResponse response) {
      String content;
      try {
         content = response.getContent() != null ? Utils.toStringAndClose(response.getContent())
                  : null;
         command.setException(new HttpResponseException(command, response, content));
      } catch (IOException e) {
         command.setException(new HttpResponseException(command, response));
      }
   }
}
