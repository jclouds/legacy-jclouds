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
package org.jclouds.util;

import static org.jclouds.util.Strings2.urlDecode;
import static org.jclouds.util.Strings2.urlEncode;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class Strings2Test {

   public void testIsEncoded() {
      assert Strings2.isUrlEncoded("/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assert !Strings2.isUrlEncoded("/read-tests/ tep");
   }

   public void testNoDoubleEncode() {
      assertEquals(urlEncode("/read-tests/%73%6f%6d%65%20%66%69%6c%65", '/'),
            "/read-tests/%73%6f%6d%65%20%66%69%6c%65");
      assertEquals(urlEncode("/read-tests/ tep", '/'), "/read-tests/%20tep");
   }
   
   public void testReplaceTokens() {
      assertEquals(Strings2.replaceTokens("hello {where}", ImmutableMap.of("where", "world")), "hello world");
   }

   public void testUrlEncodeDecodeShouldGiveTheSameString() {
      String actual = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCc903twxU2zcQnIJdXv61RwZNZW94uId9qz08fgsBJsCOnHNIC4+L9k" +
         "DOA2IHV9cUfEDBm1Be5TbpadWwSbS/05E+FARH2/MCO932UgcKUq5PGymS0249fLCBPci5zoLiG5vIym+1ij1hL/nHvkK99NIwe7io+Lmp" +
         "9OcF3PTsm3Rgh5T09cRHGX9horp0VoAVa9vKJx6C1/IEHVnG8p0YPPa1lmemvx5kNBEiyoNQNYa34EiFkcJfP6rqNgvY8h/j4nE9SXoUCC" +
         "/g6frhMFMOL0tzYqvz0Lczqm1Oh4RnSn3O9X4R934p28qqAobe337hmlLUdb6H5zuf+NwCh0HdZ";
      assertEquals(actual, urlDecode(urlEncode(actual)));
   }

}
