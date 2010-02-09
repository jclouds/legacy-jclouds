/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rest.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.Utils.propagateOrNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnNullOnNotFoundOr404 implements Function<Exception, Object> {
   private final ReturnTrueOn404 rto404;

   @Inject
   private ReturnNullOnNotFoundOr404(ReturnTrueOn404 rto404) {
      this.rto404 = checkNotNull(rto404, "rto404");
   }

   public Object apply(Exception from) {
      Iterable<ResourceNotFoundException> throwables = Iterables.filter(
               Throwables.getCausalChain(from), ResourceNotFoundException.class);
      if (Iterables.size(throwables) >= 1) {
         return null;
      } else if (rto404.apply(from)) {
         return null;
      }
      return Object.class.cast(propagateOrNull(from));
   }

}