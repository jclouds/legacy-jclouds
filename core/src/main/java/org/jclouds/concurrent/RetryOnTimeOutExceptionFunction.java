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
package org.jclouds.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.util.concurrent.TimeoutException;

import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingObject;

/**
 * 
 * @author Adrian Cole
 */
public class RetryOnTimeOutExceptionFunction<K,V>  extends ForwardingObject implements Function<K,V>{
   private final Function<K,V> delegate;

   public RetryOnTimeOutExceptionFunction(Function<K,V> delegate) {
      this.delegate =  checkNotNull(delegate, "delegate");
   }
   
   //TODO: backoff limited retry handler
   @Override
   public V apply(K key) {
      TimeoutException ex = null;
      for (int i = 0; i < 3; i++) {
         try {
            ex = null;
            return delegate().apply(key);
         } catch (Exception e) {
            if ((ex = Throwables2.getFirstThrowableOfType(e, TimeoutException.class)) != null)
               continue;
            throw propagate(e);
         }
      }
      if (ex != null)
         throw propagate(ex);
      assert false;
      return null;
   }

   @Override
   public boolean equals(Object obj) {
      return delegate.equals(obj);
   }

   @Override
   public int hashCode() {
      return delegate.hashCode();
   }

   @Override
   protected Function<K,V> delegate() {
      return delegate;
   }

}
