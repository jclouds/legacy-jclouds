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

package org.jclouds.rest.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * Unwraps the only value in a nested json reponse
 * 
 * ex. { "foo" :"bar" } becomes "bar"
 * 
 * @author Adrian Cole
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Unwrap {
   /**
    * level to unwrap.
    * 
    * ex. if default (1)
    * 
    * { "foo" :"bar" } becomes "bar"
    * 
    * ex. if (2)
    * 
    * { "foo" : {"bar" : ["baz"]} } becomes ["baz"]
    * 
    * @return nestingLevel
    */
   int depth() default 1;

   /**
    * final collection type
    * 
    * ex. if depth(2), edgeCollection(Map.class)
    * 
    * { "foo" : {"bar" : ["baz"]} } becomes ["baz"]
    * 
    * ex. if depth(3), edgeCollection(Set.class)
    * 
    * { "foo" : {"bar" : ["baz"]} } becomes "baz"
    * 
    * <h4>Note</h4> only Map and Set are valid
    * 
    * @return
    */
   Class<?> edgeCollection() default Map.class;

}
