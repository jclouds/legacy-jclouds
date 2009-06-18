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
package org.jclouds.aws.s3.commands;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.concurrent.ExecutionException;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(testName = "s3.PutBucketTest")
public class PutBucketTest {

   @Test
   void testBucketAlreadyOwnedByYouIsOk() throws Exception {
      ExecutionException e = getErrorWithCode("BucketAlreadyOwnedByYou");
      assert PutBucket.eventualConsistencyAlreadyOwnedIsOk(e);
   }

   @Test
   void testBlahIsNotOk() throws Exception {
      ExecutionException e = getErrorWithCode("blah");
      try {
         PutBucket.eventualConsistencyAlreadyOwnedIsOk(e);
         assert false;
      } catch (ExecutionException er) {
         // don't try expectedExceptions as it will fail due to easymock reasons
      }
   }

   private ExecutionException getErrorWithCode(String code) {
      AWSResponseException inner = createMock(AWSResponseException.class);
      ExecutionException e = createMock(ExecutionException.class);
      expect(e.getCause()).andReturn(inner).atLeastOnce();
      AWSError error = createMock(AWSError.class);
      expect(inner.getError()).andReturn(error);
      expect(error.getCode()).andReturn(code);
      replay(e);
      replay(inner);
      replay(error);
      return e;
   }
}