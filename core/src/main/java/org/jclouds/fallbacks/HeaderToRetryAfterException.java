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
package org.jclouds.fallbacks;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import javax.inject.Inject;

import org.jclouds.date.DateCodec;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.RetryAfterException;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Ticker;
import com.google.common.net.HttpHeaders;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * propagates as {@link RetryAfterException} if a Throwable is an
 * {@link HttpResponseException} with a {@link HttpResponse set} and a valid
 * {@link HttpHeaders#RETRY_AFTER} header set.
 * 
 * @author Adrian Cole
 * @see <a href="https://tools.ietf.org/html/rfc2616#section-14.37">Retry-After
 *      format</a>
 */
@Beta
public final class HeaderToRetryAfterException implements PropagateIfRetryAfter {

   private final Ticker ticker;
   private final DateCodec dateCodec;

   /**
    * 
    * @param ticker
    *           how to read current time
    * @param dateParser
    *           how to parse the {@link HttpHeaders#RETRY_AFTER} header, if it
    *           is a Date.
    * @return
    */
   public static HeaderToRetryAfterException create(Ticker ticker, DateCodec dateCodec) {
      return new HeaderToRetryAfterException(ticker, dateCodec);
   }

   /**
    * uses {@link Ticker#systemTicker()} and {@link DateCodecFactory#rfc822()}
    */
   @Inject
   private HeaderToRetryAfterException(DateCodecFactory factory) {
      this(Ticker.systemTicker(), factory.rfc822());
   }

   private HeaderToRetryAfterException(Ticker ticker, DateCodec dateCodec) {
      this.ticker = checkNotNull(ticker, "ticker");
      this.dateCodec = checkNotNull(dateCodec, "dateCodec");
   }

   @Override
   public ListenableFuture<Object> create(Throwable t) {
      if (!(t instanceof HttpResponseException))
         throw propagate(t);
      HttpResponse response = HttpResponseException.class.cast(t).getResponse();
      if (response == null)
         return immediateFuture(null);

      // https://tools.ietf.org/html/rfc2616#section-14.37
      String retryAfter = response.getFirstHeaderOrNull(HttpHeaders.RETRY_AFTER);
      if (retryAfter != null) {
         Optional<RetryAfterException> retryException = tryCreateRetryAfterException(t, retryAfter);
         if (retryException.isPresent())
            throw retryException.get();
      }

      return immediateFuture(null);
   }

   /**
    * returns a {@link RetryAfterException} if parameter {@code retryAfter}
    * corresponds to known formats.
    * 
    * @see <a
    *      href="https://tools.ietf.org/html/rfc2616#section-14.37">Retry-After
    *      format</a>
    */
   public Optional<RetryAfterException> tryCreateRetryAfterException(Throwable in, String retryAfter) {
      checkNotNull(in, "throwable");
      checkNotNull(retryAfter, "retryAfter");

      if (retryAfter.matches("^[0-9]+$"))
         return Optional.of(new RetryAfterException(in, Integer.parseInt(retryAfter)));
      try {
         long retryTimeMillis = dateCodec.toDate(retryAfter).getTime();
         long currentTimeMillis = NANOSECONDS.toMillis(ticker.read());
         return Optional.of(new RetryAfterException(in, (int) MILLISECONDS.toSeconds(retryTimeMillis
               - currentTimeMillis)));
      } catch (IllegalArgumentException e) {
         // ignored
      }
      return Optional.absent();
   }

}
