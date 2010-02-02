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

import static org.jclouds.util.Utils.propagateOrNull;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnFalseOnResourceNotFound implements Function<Exception, Boolean> {

   public Boolean apply(Exception from) {
      List<Throwable> throwables = Throwables.getCausalChain(from);
      if (Iterables.size(Iterables.filter(throwables, ResourceNotFoundException.class)) >= 1) {
         return false;
      }
      return Boolean.class.cast(propagateOrNull(from));
   }
}