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
package org.jclouds.rest.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URI;

import javax.inject.Singleton;

import com.google.common.base.Function;

/**
 * Extracts the endpoint of a parameter from an object.
 * 
 * @see PathParam
 * @author Adrian Cole
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface EndpointParam {
   @Singleton
   public static class ReturnSame implements Function<Object, URI> {

      @Override
      public URI apply(Object from) {
         // TODO check arg;
         return (URI) from;
      }

   }

   Class<? extends Function<Object, URI>> parser() default ReturnSame.class;
}
