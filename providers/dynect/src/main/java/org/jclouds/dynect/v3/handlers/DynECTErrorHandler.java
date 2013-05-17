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
package org.jclouds.dynect.v3.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import org.jclouds.dynect.v3.DynECTExceptions.TargetExistsException;
import org.jclouds.dynect.v3.DynECTExceptions.JobStillRunningException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;

/**
 * @author Adrian Cole
 */
public class DynECTErrorHandler implements HttpErrorHandler {
   private static final String JOB_STILL_RUNNING = "This session already has a job running";
   private static final String OPERATION_BLOCKED = "Operation blocked by current task";
   private static final String TARGET_EXISTS = "Name already exists";
   
   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         byte[] data = closeClientButKeepContentStream(response);
         String message = data != null ? new String(data) : null;
         if (message != null) {
            exception = new HttpResponseException(command, response, message);
            if (message.indexOf(JOB_STILL_RUNNING) != -1)
               exception = new JobStillRunningException(JOB_STILL_RUNNING, exception);
            else if (message.indexOf(OPERATION_BLOCKED) != -1)
               exception = new JobStillRunningException(OPERATION_BLOCKED, exception);
            else if (message.indexOf(TARGET_EXISTS) != -1)
               exception = new TargetExistsException(TARGET_EXISTS, exception);
         } else {
            exception = new HttpResponseException(command, response);
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }
}
