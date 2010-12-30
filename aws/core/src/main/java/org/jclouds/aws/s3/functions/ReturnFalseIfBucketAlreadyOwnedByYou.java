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

package org.jclouds.aws.s3.functions;

import static com.google.common.base.Throwables.getCausalChain;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.util.Throwables2.propagateOrNull;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReturnFalseIfBucketAlreadyOwnedByYou implements Function<Exception, Boolean> {

   public Boolean apply(Exception from) {
      List<Throwable> throwables = getCausalChain(from);

      Iterable<AWSResponseException> matchingAWSResponseException = filter(throwables, AWSResponseException.class);
      if (size(matchingAWSResponseException) >= 1 && get(matchingAWSResponseException, 0).getError() != null) {
         if (get(matchingAWSResponseException, 0).getError().getCode().equals("BucketAlreadyOwnedByYou"))
            return false;
      }
      return Boolean.class.cast(propagateOrNull(from));
   }
}
