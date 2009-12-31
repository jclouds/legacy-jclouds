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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Prefixes the hostname of the endpoint with the contents of a method parameter,
 * 
 * @author Adrian Cole
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HostPrefixParam {

   /**
    * Defines the characters that will be inserted in-between the value of the annotated method
    * argument and the existing hostname.
    * 
    * <p />
    * <ul>
    * <li>hostname was {@code mydomain.com}</li>
    * <li>method argument is {@code myhost}</li>
    * <li>if {@code joinOn} is not set, result is {@code myhostmydomain.com}</li>
    * <li>if {@code joinOn} is set to {@code .}, result is {@code myhost.mydomain.com}</li>
    * </ul>
    * 
    */
   String value() default ".";
}
