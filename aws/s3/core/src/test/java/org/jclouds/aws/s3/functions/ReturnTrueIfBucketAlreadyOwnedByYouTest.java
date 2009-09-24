/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.s3.functions.ReturnTrueIfBucketAlreadyOwnedByYou;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(testName = "s3.ReturnTrueIfBucketAlreadyOwnedByYouTest")
public class ReturnTrueIfBucketAlreadyOwnedByYouTest {

   @Test
   void testBucketAlreadyOwnedByYouIsOk() throws Exception {
      Exception e = getErrorWithCode("BucketAlreadyOwnedByYou");
      assert new ReturnTrueIfBucketAlreadyOwnedByYou().apply(e);
   }

   @Test
   void testBlahIsNotOk() throws Exception {
      Exception e = getErrorWithCode("blah");
      assert new ReturnTrueIfBucketAlreadyOwnedByYou().apply(e) == null;
   }

   private Exception getErrorWithCode(String code) {
      AWSResponseException inner = createMock(AWSResponseException.class);
      AWSError error = createMock(AWSError.class);
      expect(inner.getError()).andReturn(error);
      expect(error.getCode()).andReturn(code);
      replay(inner);
      replay(error);
      return inner;
   }
}