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
package org.jclouds.s3.functions;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(testName = "ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalStateTest")
public class ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalStateTest {

   @Test
   void testBucketAlreadyOwnedByYouIsOk() throws Exception {
      Exception e = getErrorWithCode("BucketAlreadyOwnedByYou");
      assert !new ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalState().apply(e);
   }

   @Test
   void testIllegalStateIsOk() throws Exception {
      Exception e = new IllegalStateException();
      assert !new ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalState().apply(e);
   }

   @Test(expectedExceptions = AWSResponseException.class)
   void testBlahIsNotOk() throws Exception {
      Exception e = getErrorWithCode("blah");
      new ReturnFalseIfBucketAlreadyOwnedByYouOrIllegalState().apply(e);
   }

   private Exception getErrorWithCode(String code) {
      AWSError error = new AWSError();
      error.setCode(code);
      return new AWSResponseException(null, null, null, error);
   }
}
