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
package org.jclouds.http.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;

import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.util.Strings2;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class CloseContentAndSetExceptionErrorHandler implements HttpErrorHandler {

   public void handleError(HttpCommand command, HttpResponse from) {
      String content;
      try {
         content = from.getPayload() != null ? Strings2.toString(from.getPayload()) : null;
         command.setException(new HttpResponseException(command, from, content));
      } catch (IOException e) {
         command.setException(new HttpResponseException(command, from));
      } finally {
         releasePayload(from);
      }
   }
}
