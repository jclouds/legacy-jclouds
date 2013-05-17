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

/**
 * Designates that this parameter will be bound to a multipart form.
 * 
 * @author Adrian Cole
 */
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface PartParam {
   // hacks as nulls are not allowed as default values
   public static String NO_FILENAME = "---NO_FILENAME---";
   public static String NO_CONTENT_TYPE = "---NO_CONTENT_TYPE---";

   String name();

   String contentType() default NO_CONTENT_TYPE;
   
   String filename() default NO_FILENAME;
}
