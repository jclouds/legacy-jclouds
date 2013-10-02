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

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.IOExceptionRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.base.Throwables;
import com.google.inject.Inject;

/**
 * Allow replayable request to be retried a limited number of times, and impose an exponential
 * back-off delay before returning.
 * <p>
 * The back-off delay grows rapidly according to the formula
 * <code>50 * (<i>{@link TransformingHttpCommand#getFailureCount()}</i> ^ 2)</code>. For example:
 * <table>
 * <tr>
 * <th>Number of Attempts</th>
 * <th>Delay in milliseconds</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>50</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>200</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>450</td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>800</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>1250</td>
 * </tr>
 * </table>
 * <p>
 * This implementation has two side-effects. It increments the command's failure count with
 * {@link TransformingHttpCommand#incrementFailureCount()}, because this failure count value is used
 * to determine how many times the command has already been tried. It also closes the response's
 * content input stream to ensure connections are cleaned up.
 * 
 * @author James Murty
 */
@Singleton
public class BackoffLimitedRetryHandler implements HttpRetryHandler, IOExceptionRetryHandler {

   public static final BackoffLimitedRetryHandler INSTANCE = new BackoffLimitedRetryHandler();

   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private int retryCountLimit = 5;

   @Inject(optional = true)
   @Named(Constants.PROPERTY_RETRY_DELAY_START)
   private long delayStart = 50L;

   @Resource
   protected Logger logger = Logger.NULL;

   public boolean shouldRetryRequest(HttpCommand command, IOException error) {
      return ifReplayableBackoffAndReturnTrue(command);
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      releasePayload(response);
      return ifReplayableBackoffAndReturnTrue(command);
   }

   private boolean ifReplayableBackoffAndReturnTrue(HttpCommand command) {
      command.incrementFailureCount();

      if (!command.isReplayable()) {
         logger.error("Cannot retry after server error, command is not replayable: %1$s", command);
         return false;
      } else if (command.getFailureCount() > retryCountLimit) {
         logger.error("Cannot retry after server error, command has exceeded retry limit %1$d: %2$s", retryCountLimit,
                  command);
         return false;
      } else {
         imposeBackoffExponentialDelay(command.getFailureCount(), "server error: " + command.toString());
         return true;
      }
   }

   public void imposeBackoffExponentialDelay(int failureCount, String commandDescription) {
      imposeBackoffExponentialDelay(delayStart, 2, failureCount, retryCountLimit, commandDescription);
   }

   public void imposeBackoffExponentialDelay(long period, int pow, int failureCount, int max, String commandDescription) {
      imposeBackoffExponentialDelay(period, period * 10l, pow, failureCount, max, commandDescription);
   }

   public void imposeBackoffExponentialDelay(long period, long maxPeriod, int pow, int failureCount, int max,
            String commandDescription) {
      long delayMs = (long) (period * Math.pow(failureCount, pow));
      delayMs = delayMs > maxPeriod ? maxPeriod : delayMs;
      logger.debug("Retry %d/%d: delaying for %d ms: %s", failureCount, max, delayMs, commandDescription);
      try {
         Thread.sleep(delayMs);
      } catch (InterruptedException e) {
         Throwables.propagate(e);
      }
   }

}
