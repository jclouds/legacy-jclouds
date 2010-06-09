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
package org.jclouds.rest.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jclouds.http.MultipartForm.Part;

import com.google.common.base.Function;

/**
 * Designates that this parameter will be bound to a multipart form.
 * 
 * @author Adrian Cole
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface PartParam {

   public static class ALREADY_PART implements Function<Object, Part> {

      @Override
      public Part apply(Object from) {
         return Part.class.cast(from);
      }
   };

   /**
    * how to convert this to a part.
    */
   Class<? extends Function<Object, Part>> value() default ALREADY_PART.class;
}
