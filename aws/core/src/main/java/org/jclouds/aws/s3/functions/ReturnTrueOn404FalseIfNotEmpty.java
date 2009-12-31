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
package org.jclouds.aws.s3.functions;

import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;

import com.google.common.base.Function;

@Singleton
public class ReturnTrueOn404FalseIfNotEmpty implements Function<Exception, Boolean> {

   public Boolean apply(Exception from) {
      if (from instanceof AWSResponseException) {
         AWSResponseException responseException = (AWSResponseException) from;
         if (responseException.getResponse().getStatusCode() == 404) {
            return true;
         } else if ("BucketNotEmpty".equals(responseException.getError().getCode())
                  || responseException.getResponse().getStatusCode() == 409) {
            return false;
         }
      }
      return null;
   }

}
