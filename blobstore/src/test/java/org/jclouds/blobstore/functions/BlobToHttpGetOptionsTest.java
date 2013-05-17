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
package org.jclouds.blobstore.functions;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.jclouds.http.options.GetOptions;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BlobToHttpGetOptionsTest {
   BlobToHttpGetOptions fn = new BlobToHttpGetOptions();

   @Test
   public void testNoneReturnsNone()  {
      assertEquals(fn.apply(org.jclouds.blobstore.options.GetOptions.NONE), GetOptions.NONE);
   }

   @Test
   public void testIfUnmodifiedSince()  {

      Date ifUnmodifiedSince = new Date(999999l);

      org.jclouds.blobstore.options.GetOptions in = new org.jclouds.blobstore.options.GetOptions();
      in.ifUnmodifiedSince(ifUnmodifiedSince);
      GetOptions expected = new GetOptions();
      expected.ifUnmodifiedSince(ifUnmodifiedSince);

      assertEquals(fn.apply(in), expected);
   }

   @Test
   public void testIfModifiedSince()  {

      Date ifModifiedSince = new Date(999999l);

      org.jclouds.blobstore.options.GetOptions in = new org.jclouds.blobstore.options.GetOptions();
      in.ifModifiedSince(ifModifiedSince);
      GetOptions expected = new GetOptions();
      expected.ifModifiedSince(ifModifiedSince);

      assertEquals(fn.apply(in), expected);
   }

   public void testIfUnmatch()  {

      String ifUnmatch = "foo";

      org.jclouds.blobstore.options.GetOptions in = new org.jclouds.blobstore.options.GetOptions();
      in.ifETagDoesntMatch(ifUnmatch);
      GetOptions expected = new GetOptions();
      expected.ifETagDoesntMatch(ifUnmatch);

      assertEquals(fn.apply(in), expected);
   }

   @Test
   public void testIfMatch()  {

      String ifMatch = "foo";

      org.jclouds.blobstore.options.GetOptions in = new org.jclouds.blobstore.options.GetOptions();
      in.ifETagMatches(ifMatch);
      
      GetOptions expected = new GetOptions();
      expected.ifETagMatches(ifMatch);

      assertEquals(fn.apply(in), expected);
   }

   @Test
   public void testRanges(){
      org.jclouds.blobstore.options.GetOptions in = new org.jclouds.blobstore.options.GetOptions();
      in.range(0,1024);
      in.startAt(2048);
      
      GetOptions expected = new GetOptions();
      expected.range(0,1024);
      expected.startAt(2048);

      assertEquals(fn.apply(in), expected);

   }

   @Test
   public void testRangesTail(){
      org.jclouds.blobstore.options.GetOptions in = new org.jclouds.blobstore.options.GetOptions();
      in.tail(1024);

      GetOptions expected = new GetOptions();
      expected.tail(1024);

      assertEquals(fn.apply(in), expected);

   }
   @Test
   public void testRangesStart(){
      org.jclouds.blobstore.options.GetOptions in = new org.jclouds.blobstore.options.GetOptions();
      in.startAt(1024);

      GetOptions expected = new GetOptions();
      expected.startAt(1024);

      assertEquals(fn.apply(in), expected);

   }
   
   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      fn.apply(null);
   }
}
