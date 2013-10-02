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

import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.ImplementedBy;

/**
 * propagates as {@link org.jclouds.rest.RetryAfterException} if a Throwable contains information such as a retry
 * offset.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(HeaderToRetryAfterException.class)
public interface PropagateIfRetryAfter extends FutureFallback<Object> {
   /**
    * if input is not of type {@link org.jclouds.http.HttpResponseException}, this method propagates. Otherwise, immediate future of
    * {@code null}, if didn't coerce to {@link org.jclouds.rest.RetryAfterException}
    */
   @Override
   ListenableFuture<Object> create(Throwable t);

}
