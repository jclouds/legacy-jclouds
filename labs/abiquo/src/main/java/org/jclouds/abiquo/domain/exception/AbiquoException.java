/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.exception;

import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.abiquo.predicates.ErrorPredicates.code;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.google.common.collect.Lists;

/**
 * Abiquo API exception.
 * 
 * @author Francesc Montserrat
 * @author Ignasi Barrera
 */
public class AbiquoException extends RuntimeException {

   /** The HTTP status. */
   private Status httpStatus;

   /** The errors. */
   private ErrorsDto errors;

   public AbiquoException(final Status httpStatus, final ErrorsDto errors) {
      super();
      this.httpStatus = httpStatus;
      this.errors = errors;
   }

   /**
    * Check if there is an error with the given code.
    */
   public boolean hasError(final String code) {
      return any(errors.getCollection(), code(code));
   }

   /**
    * Find the first error with the given code.
    */
   public ErrorDto findError(final String code) {
      return find(errors.getCollection(), code(code), null);
   }

   /**
    * Find all errors with the given code.
    */
   public List<ErrorDto> findErrors(final String code) {
      return Lists.newLinkedList(filter(errors.getCollection(), code(code)));
   }

   /**
    * Get the number of errors.
    */
   public int numErrors() {
      return errors.getCollection().size();
   }

   /**
    * Get the list of all errors.
    */
   public List<ErrorDto> getErrors() {
      return errors.getCollection();
   }

   /**
    * Get the HTTP status code.
    */
   public int getHttpStatusCode() {
      return httpStatus.getStatusCode();
   }

   /**
    * Get the HTTP status name.
    */
   public String getHttpStatusName() {
      return httpStatus.getReasonPhrase();
   }

   /**
    * Get the HTTP status.
    */
   public Status getHttpStatus() {
      return httpStatus;
   }

   @Override
   public String getMessage() {
      return errors.toString();
   }
}
