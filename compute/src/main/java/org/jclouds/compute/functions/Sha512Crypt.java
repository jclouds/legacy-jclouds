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
/*
   Sha512Crypt.java

   Created: 18 December 2007
   Last Changed By: $Author: broccol $
   Version: $Revision: 7692 $
   Last Mod Date: $Date: 2007-12-30 01:55:31 -0600 (Sun, 30 Dec 2007) $

   Java Port By: James Ratcliff, falazar@arlut.utexas.edu

   This class implements the new generation, scalable, SHA512-based
   Unix 'crypt' algorithm developed by a group of engineers from Red
   Hat, Sun, IBM, and HP for common use in the Unix and Linux
   /etc/shadow files.

   The Linux glibc library (starting at version 2.7) includes support
   for validating passwords hashed using this algorithm.

   The algorithm itself was released into the Public Domain by Ulrich
   Drepper <drepper@redhat.com>.  A discussion of the rationale and
   development of this algorithm is at

   http://people.redhat.com/drepper/sha-crypt.html

   and the specification and a sample C language implementation is at

   http://people.redhat.com/drepper/SHA-crypt.txt

   This Java Port is  

     Copyright (c) 2008 The University of Texas at Austin.

     All rights reserved.

     Redistribution and use in source and binary form are permitted
     provided that distributions retain this entire copyright notice
     and comment. Neither the name of the University nor the names of
     its contributors may be used to endorse or promote products
     derived from this software without specific prior written
     permission. THIS SOFTWARE IS PROVIDED "AS IS" AND WITHOUT ANY
     EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE
     IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
     PARTICULAR PURPOSE.

 */

package org.jclouds.compute.functions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Throwables;

/**
 * This class defines a method,
 * {@link Sha512Crypt#Sha512_crypt(java.lang.String, java.lang.String, int)
 * Sha512_crypt()}, which takes a password and a salt string and generates a
 * Sha512 encrypted password entry.
 * 
 * This class implements the new generation, scalable, SHA512-based Unix 'crypt'
 * algorithm developed by a group of engineers from Red Hat, Sun, IBM, and HP
 * for common use in the Unix and Linux /etc/shadow files.
 * 
 * The Linux glibc library (starting at version 2.7) includes support for
 * validating passwords hashed using this algorithm.
 * 
 * The algorithm itself was released into the Public Domain by Ulrich Drepper
 * &lt;drepper@redhat.com&gt;. A discussion of the rationale and development of
 * this algorithm is at
 * 
 * http://people.redhat.com/drepper/sha-crypt.html
 * 
 * and the specification and a sample C language implementation is at
 * 
 * http://people.redhat.com/drepper/SHA-crypt.txt
 */
public class Sha512Crypt {
   public static com.google.common.base.Function<String, String> function() {
      return Function.INSTANCE;
   }

   private static enum Function implements com.google.common.base.Function<String, String> {
      INSTANCE;

      @Override
      public String apply(String input) {
         return Sha512Crypt.makeShadowLine(input, null);
      }

      @Override
      public String toString() {
         return "sha512Crypt()";
      }
   }

   private static final String sha512_salt_prefix = "$6$";
   private static final String sha512_rounds_prefix = "rounds=";
   private static final int SALT_LEN_MAX = 16;
   private static final int ROUNDS_DEFAULT = 5000;
   private static final int ROUNDS_MIN = 1000;
   private static final int ROUNDS_MAX = 999999999;
   private static final String SALTCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
   private static final String itoa64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

   /**
    * This method actually generates an Sha512 crypted password hash from a
    * plaintext password and a salt.
    * 
    * <p>
    * The resulting string will be in the form
    * '$6$&lt;rounds=n&gt;$&lt;salt&gt;$&lt;hashed mess&gt;
    * </p>
    * 
    * @param password
    *           Plaintext password
    * 
    * @param shadowPrefix
    *           An encoded salt/rounds which will be consulted to determine the
    *           salt and round count, if not null
    * 
    * @return The Sha512 Unix Crypt hash text for the password
    */
   static String makeShadowLine(String password, @Nullable String shadowPrefix) {
      MessageDigest ctx = sha512();
      MessageDigest alt_ctx = sha512();

      byte[] alt_result;
      byte[] temp_result;
      byte[] p_bytes = null;
      byte[] s_bytes = null;
      int cnt;
      int cnt2;
      int rounds = ROUNDS_DEFAULT; // Default number of rounds.
      StringBuilder buffer;

      /* -- */

      if (shadowPrefix != null) {
         if (shadowPrefix.startsWith(sha512_salt_prefix)) {
            shadowPrefix = shadowPrefix.substring(sha512_salt_prefix.length());
         }

         if (shadowPrefix.startsWith(sha512_rounds_prefix)) {
            String num = shadowPrefix.substring(sha512_rounds_prefix.length(), shadowPrefix.indexOf('$'));
            int srounds = Integer.valueOf(num).intValue();
            shadowPrefix = shadowPrefix.substring(shadowPrefix.indexOf('$') + 1);
            rounds = Math.max(ROUNDS_MIN, Math.min(srounds, ROUNDS_MAX));
         }

         if (shadowPrefix.length() > SALT_LEN_MAX) {
            shadowPrefix = shadowPrefix.substring(0, SALT_LEN_MAX);
         }
      } else {
         java.util.Random randgen = new java.util.Random();
         StringBuilder saltBuf = new StringBuilder();

         while (saltBuf.length() < 16) {
            int index = (int) (randgen.nextFloat() * SALTCHARS.length());
            saltBuf.append(SALTCHARS.substring(index, index + 1));
         }

         shadowPrefix = saltBuf.toString();
      }

      byte[] key = password.getBytes();
      byte[] salts = shadowPrefix.getBytes();

      ctx.reset();
      ctx.update(key, 0, key.length);
      ctx.update(salts, 0, salts.length);

      alt_ctx.reset();
      alt_ctx.update(key, 0, key.length);
      alt_ctx.update(salts, 0, salts.length);
      alt_ctx.update(key, 0, key.length);

      alt_result = alt_ctx.digest();

      for (cnt = key.length; cnt > 64; cnt -= 64) {
         ctx.update(alt_result, 0, 64);
      }

      ctx.update(alt_result, 0, cnt);

      for (cnt = key.length; cnt > 0; cnt >>= 1) {
         if ((cnt & 1) != 0) {
            ctx.update(alt_result, 0, 64);
         } else {
            ctx.update(key, 0, key.length);
         }
      }

      alt_result = ctx.digest();

      alt_ctx.reset();

      for (cnt = 0; cnt < key.length; ++cnt) {
         alt_ctx.update(key, 0, key.length);
      }

      temp_result = alt_ctx.digest();

      p_bytes = new byte[key.length];

      for (cnt2 = 0, cnt = p_bytes.length; cnt >= 64; cnt -= 64) {
         System.arraycopy(temp_result, 0, p_bytes, cnt2, 64);
         cnt2 += 64;
      }

      System.arraycopy(temp_result, 0, p_bytes, cnt2, cnt);

      alt_ctx.reset();

      for (cnt = 0; cnt < 16 + (alt_result[0] & 0xFF); ++cnt) {
         alt_ctx.update(salts, 0, salts.length);
      }

      temp_result = alt_ctx.digest();

      s_bytes = new byte[salts.length];

      for (cnt2 = 0, cnt = s_bytes.length; cnt >= 64; cnt -= 64) {
         System.arraycopy(temp_result, 0, s_bytes, cnt2, 64);
         cnt2 += 64;
      }

      System.arraycopy(temp_result, 0, s_bytes, cnt2, cnt);

      /*
       * Repeatedly run the collected hash value through SHA512 to burn CPU
       * cycles.
       */

      for (cnt = 0; cnt < rounds; ++cnt) {
         ctx.reset();

         if ((cnt & 1) != 0) {
            ctx.update(p_bytes, 0, key.length);
         } else {
            ctx.update(alt_result, 0, 64);
         }

         if (cnt % 3 != 0) {
            ctx.update(s_bytes, 0, salts.length);
         }

         if (cnt % 7 != 0) {
            ctx.update(p_bytes, 0, key.length);
         }

         if ((cnt & 1) != 0) {
            ctx.update(alt_result, 0, 64);
         } else {
            ctx.update(p_bytes, 0, key.length);
         }

         alt_result = ctx.digest();
      }

      buffer = new StringBuilder(sha512_salt_prefix);

      if (rounds != 5000) {
         buffer.append(sha512_rounds_prefix);
         buffer.append(rounds);
         buffer.append("$");
      }

      buffer.append(shadowPrefix);
      buffer.append("$");

      buffer.append(b64_from_24bit(alt_result[0], alt_result[21], alt_result[42], 4));
      buffer.append(b64_from_24bit(alt_result[22], alt_result[43], alt_result[1], 4));
      buffer.append(b64_from_24bit(alt_result[44], alt_result[2], alt_result[23], 4));
      buffer.append(b64_from_24bit(alt_result[3], alt_result[24], alt_result[45], 4));
      buffer.append(b64_from_24bit(alt_result[25], alt_result[46], alt_result[4], 4));
      buffer.append(b64_from_24bit(alt_result[47], alt_result[5], alt_result[26], 4));
      buffer.append(b64_from_24bit(alt_result[6], alt_result[27], alt_result[48], 4));
      buffer.append(b64_from_24bit(alt_result[28], alt_result[49], alt_result[7], 4));
      buffer.append(b64_from_24bit(alt_result[50], alt_result[8], alt_result[29], 4));
      buffer.append(b64_from_24bit(alt_result[9], alt_result[30], alt_result[51], 4));
      buffer.append(b64_from_24bit(alt_result[31], alt_result[52], alt_result[10], 4));
      buffer.append(b64_from_24bit(alt_result[53], alt_result[11], alt_result[32], 4));
      buffer.append(b64_from_24bit(alt_result[12], alt_result[33], alt_result[54], 4));
      buffer.append(b64_from_24bit(alt_result[34], alt_result[55], alt_result[13], 4));
      buffer.append(b64_from_24bit(alt_result[56], alt_result[14], alt_result[35], 4));
      buffer.append(b64_from_24bit(alt_result[15], alt_result[36], alt_result[57], 4));
      buffer.append(b64_from_24bit(alt_result[37], alt_result[58], alt_result[16], 4));
      buffer.append(b64_from_24bit(alt_result[59], alt_result[17], alt_result[38], 4));
      buffer.append(b64_from_24bit(alt_result[18], alt_result[39], alt_result[60], 4));
      buffer.append(b64_from_24bit(alt_result[40], alt_result[61], alt_result[19], 4));
      buffer.append(b64_from_24bit(alt_result[62], alt_result[20], alt_result[41], 4));
      buffer.append(b64_from_24bit((byte) 0x00, (byte) 0x00, alt_result[63], 2));

      /*
       * Clear the buffer for the intermediate result so that people attaching
       * to processes or reading core dumps cannot get any information.
       */

      ctx.reset();

      return buffer.toString();
   }

   private static MessageDigest sha512() {
      try {
         return MessageDigest.getInstance("SHA-512");
      } catch (NoSuchAlgorithmException e) {
         throw Throwables.propagate(e);
      }
   }

   private static final String b64_from_24bit(byte B2, byte B1, byte B0, int size) {
      int v = ((((int) B2) & 0xFF) << 16) | ((((int) B1) & 0xFF) << 8) | ((int) B0 & 0xff);

      StringBuilder result = new StringBuilder();

      while (--size >= 0) {
         result.append(itoa64.charAt(v & 0x3f));
         v >>>= 6;
      }

      return result.toString();
   }

}
