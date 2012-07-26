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
package org.jclouds.rds.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.base.Function;

/**
 * it is ok to have a db instance already in the process of deleting
 */
@Singleton
public class ReturnNullOnStateDeletingNotFoundOr404 implements Function<Exception, Object> {
   private ReturnNullOnNotFoundOr404 rto404;

   @Inject
   private ReturnNullOnStateDeletingNotFoundOr404(ReturnNullOnNotFoundOr404 rto404) {
      this.rto404 = rto404;
   }

   public Object apply(Exception from) {
      if (from instanceof AWSResponseException) {
         AWSResponseException e = AWSResponseException.class.cast(from);
         if (e.getError().getCode().equals("InvalidDBInstanceState")
                  && e.getError().getMessage().contains("has state: deleting"))
            return null;
      }
      return rto404.apply(from);
   }

}
