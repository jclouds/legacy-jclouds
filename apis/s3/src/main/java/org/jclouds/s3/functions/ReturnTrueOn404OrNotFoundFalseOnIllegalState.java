/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.s3.functions;

import static com.google.common.base.Predicates.equalTo;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;
import static org.jclouds.util.Throwables2.propagateOrNull;

import javax.inject.Singleton;

import org.jclouds.blobstore.ContainerNotFoundException;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnTrueOn404OrNotFoundFalseOnIllegalState implements Function<Exception, Boolean> {

   public Boolean apply(Exception from) {
      if (getFirstThrowableOfType(from, IllegalStateException.class) != null) {
         return false;
      }
      if (getFirstThrowableOfType(from, ContainerNotFoundException.class) != null) {
         return true;
      }
      if (returnValueOnCodeOrNull(from, true, equalTo(404)) != null)
         return true;

      return Boolean.class.cast(propagateOrNull(from));
   }
}
