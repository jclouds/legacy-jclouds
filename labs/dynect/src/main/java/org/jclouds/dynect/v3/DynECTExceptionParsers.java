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
package org.jclouds.dynect.v3;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public final class DynECTExceptionParsers {
   private DynECTExceptionParsers() {
   }

   public static class ReturnFalseOn400 implements Function<Exception, Boolean> {
      public Boolean apply(Exception e) {
         if (returnValueOnCodeOrNull(e, false, equalTo(400)) != null)
            return false;
         throw propagate(e);
      }
   }
}
