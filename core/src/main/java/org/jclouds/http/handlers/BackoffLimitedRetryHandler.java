/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Allow replayable request to be retried a limited number of times, and 
 * impose an exponential back-off delay before returning.
 * <p>
 * The back-off delay grows rapidly according to the formula 
 * <code>50 * (<i>{@link HttpFutureCommand#getFailureCount()}</i> ^ 2)</code>. For example:
 * <table>
 * <tr><th>Number of Attempts</th><th>Delay in milliseconds</th></tr>
 * <tr><td>1</td><td>50</td></tr>
 * <tr><td>2</td><td>200</td></tr>
 * <tr><td>3</td><td>450</td></tr>
 * <tr><td>4</td><td>800</td></tr>
 * <tr><td>5</td><td>1250</td></tr>
 * </table>
 * <p>
 * This implementation has two side-effects. It increments the command's failure count
 * with {@link HttpFutureCommand#incrementFailureCount()}, because this failure count 
 * value is used to determine how many times the command has already been tried. It 
 * also closes the response's content input stream to ensure connections are cleaned up.  
 * 
 * @author James Murty
 */
public class BackoffLimitedRetryHandler implements HttpRetryHandler {
   private final int retryCountLimit;
   
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public BackoffLimitedRetryHandler(@Named("jclouds.http.max-retries") int retryCountLimit) {
      this.retryCountLimit = retryCountLimit;
   }

   public boolean retryRequest(HttpFutureCommand<?> command, HttpResponse response) 
      throws InterruptedException
   {
      IOUtils.closeQuietly(response.getContent());
     
      command.incrementFailureCount();

      if (!command.getRequest().isReplayable()) {
         logger.warn("Cannot retry after server error, command is not replayable: %1$s", command);
         return false;
      } else if (command.getFailureCount() > retryCountLimit) {
         logger.warn("Cannot retry after server error, command has exceeded retry limit %1$d: %2$s",
                     retryCountLimit, command);
         return false;
      } else {
         long delayMs = (long) (50L * Math.pow(command.getFailureCount(), 2));
         logger.debug("Retry %1$d/%2$d after server error, delaying for %3$d ms: %4$s", 
        		      command.getFailureCount(), retryCountLimit, delayMs, command);
         Thread.sleep(delayMs);
         return true;
      }
   }

}
