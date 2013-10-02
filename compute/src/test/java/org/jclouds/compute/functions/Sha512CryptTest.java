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
package org.jclouds.compute.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.functions.Sha512Crypt;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class Sha512CryptTest {

   public static final Object[][] TEST_DATA = {
            { "Hello world!", "$6$saltstring",
                     "$6$saltstring$svn8UoSVapNtMuq1ukKS4tPQd8iKwSMHWjl/O817G3uBnIFNjnQJuesI68u4OTLiBFdcbYEdFCoEOfaS35inz1" },
            {
                     "Hello world!",
                     "$6$rounds=10000$saltstringsaltstring",
                     "$6$rounds=10000$saltstringsaltst$OW1/O6BYHV6BcXZu8QVeXbDWra3Oeqh0sbHbbMCVNSnCM/UrjmM0Dp8vOuZeHBy/YTBmSK6H9qs/y3RnOaw5v." },
            { "This is just a test", "$6$rounds=5000$toolongsaltstring",
                     "$6$toolongsaltstrin$lQ8jolhgVRVhY4b5pZKaysCLi0QBxGoNeKQzQ3glMhwllF7oGDZxUhx1yxdYcz/e1JSbq3y6JMxxl8audkUEm0" },
            {
                     "a very much longer text to encrypt.  This one even stretches over morethan one line.",
                     "$6$rounds=1400$anotherlongsaltstring",
                     "$6$rounds=1400$anotherlongsalts$POfYwTEok97VWcjxIiSOjiykti.o/pQs.wPvMxQ6Fm7I6IoYN3CmLs66x9t0oSwbtEW7o7UmJEiDwGqd8p4ur1" },

            {
                     "a short string",
                     "$6$rounds=123456$asaltof16chars..",
                     "$6$rounds=123456$asaltof16chars..$BtCwjqMJGx5hrJhZywWvt0RLE8uZ4oPwcelCjmw2kSYu.Ec6ycULevoBK25fs2xXgMNrCzIMVcgEJAstJeonj1" },
            { "the minimum number is still observed", "$6$rounds=10$roundstoolow",
                     "$6$rounds=1000$roundstoolow$kUMsbe306n21p9R.FRkW3IGn.S9NPN0x50YhH1xhLsPuWGsUSklZt58jaTfF4ZEQpyUNGc0dqbpBYYBaHHrsX." } };

   @DataProvider(name = "data")
   public Object[][] createData1() {
      return TEST_DATA;
   }

   /**
    * Validate our implementation using test data from Ulrich Drepper's C implementation.
    */
   @Test(dataProvider = "data")
   public void testMakeCryptedPasswordHash(String password, String salt, String expected) {
      assertEquals(Sha512Crypt.makeShadowLine(password, salt), expected);
   }
}
