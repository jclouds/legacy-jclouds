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
package org.jclouds;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.FutureFallback;

/**
 * Provides a backup value to replace an earlier exception.
 * 
 * @param <V>
 *           the result type of the backup value
 * 
 * @author Adrian Cole
 * @see FutureFallback
 * @since 1.6
 */
@Beta
public interface Fallback<V> extends FutureFallback<V> {
   /**
    * The exception is provided so that the {@code Fallback} implementation can
    * conditionally determine whether to propagate the exception or to attempt
    * to recover.
    * 
    * @param t
    *           the exception that made the call fail.
    */
   V createOrPropagate(Throwable t) throws Exception;
}
