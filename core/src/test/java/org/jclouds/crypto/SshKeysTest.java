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
package org.jclouds.crypto;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true)
public class SshKeysTest {

   public static final String SSH_PUBLIC_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDJvZkkmoabQopH7yd9Ak2xJ34X21c0xXsJ85xbqOyqzwRmCJVHT2EPUhg6PhiozSok42WDKDjFFZ7B1IbtBM+PWUmlUCJ1r2xfLb6TPJqBkDUCbQ5lxupvmGh4gOBxf54Nrv2zS7QOiaNx870QqG8csHP7Mz7dCo9GQ9XydhNt+z4eNXPM5ToPUHRdHf4g9lmXYCdazZ3SqGdK0dwNS+dFVDQ/jzZjA31WBx45m2k/PQMIoQnlPHlJO5vyTT/O39UAwdBp8tK5Awt3azhku45mm03/+4CxObGKt6p3fvP7xlN0FsnwsSkn6IJe5J+blpSHuLDqjG9OhjYAvnf6MrZR";
   public static final String SSH_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDJvZkkmoabQopH\\n7yd9Ak2xJ34X21c0xXsJ85xbqOyqzwRmCJVHT2EPUhg6PhiozSok42WDKDjFFZ7B\\n1IbtBM+PWUmlUCJ1r2xfLb6TPJqBkDUCbQ5lxupvmGh4gOBxf54Nrv2zS7QOiaNx\\n870QqG8csHP7Mz7dCo9GQ9XydhNt+z4eNXPM5ToPUHRdHf4g9lmXYCdazZ3SqGdK\\n0dwNS+dFVDQ/jzZjA31WBx45m2k/PQMIoQnlPHlJO5vyTT/O39UAwdBp8tK5Awt3\\nazhku45mm03/+4CxObGKt6p3fvP7xlN0FsnwsSkn6IJe5J+blpSHuLDqjG9OhjYA\\nvnf6MrZRAgMBAAECggEBAMDzwHeL/FafS9cFXEVqYJih5y42MbBdeRLJl7DrXoD4\\nQ4K7jtuHhpO6t0VtgvRgVoC1pa/OVo3Z4eANv4cO5N58Tb35aRwaTpKyE+aLPlPR\\nc4IAgJbDrBJUOQeYbBLiNm9sAWbtbyfAaT1iHGDEWJGeCzAlkWik4ugXlZeza13y\\nC4hg2yUymju27nSIWcEfAo7Zmw8CTy32edLGUNq8qu7KYSaocKvf522Kjl5p6cly\\nnd+OhcFA4pSTeKkgjb1dEHw2p4JVX/srHCpySo4ERtHAFyVyTIhsP5GICAq856U5\\nGMSIT1Hja+6q/FEDqT2R32ju62VfQBS2kSfeBlzot6kCgYEA/1p6Xljvn6wrYklQ\\n046E7xp7jlx2yKWnbIjRf5QFpK5Czb53I+mSkQcDF3imaZK+f/xz1OURmGv6phsA\\nFdU+UOdzGki7DYCNuTuz9LhdpIqxGqlcvnvOP9A6ouRGTz45VX2rqUx5A2ou0SPW\\n7ok++JfvRGj4VC1G2005ht90/98CgYEAykBeNHnChXmxRv5ESD28S5ijxFLI5qvG\\nooUIhdVQ2ws7H7bb4Arj4JClu55Y3uOi2uVgPq5Pqy9aLcAALuSJjRQj9+NU9EJS\\nLy1WhBQTRNpTlUshNpg4H5b9sFoMTgz3nrTU24NY73RtoDl19TjiK7O9rTasYlcr\\n36dLejyeT88CgYEAs4vp2OcN7ibABobolyhx3jGvyOTI/MJFm7IEJIFvCmEhRctz\\nuEOms+TLTridwkPVQObAh2Rd39+kySDZCYD8JSTosQWMyKyoeiM5oIv2BBkk+Es3\\nlBQ3bHU8lYaOzW9CHxOTHSJRQI5rxtA9c1H7fg5OxbpNSdrgJJkDJwt+F98CgYEA\\niCBWx58EK+5CQXQ15SGYMJFl+Gd3zLnlEdHUcK+ooiWm/6uFxf/ObIEu616iljJE\\nlGw6ITYVbTSLz6sg9G7hndDmfJvHvDc/NX2gc3lHltoT07IjgqllbO2lhiK1kXrs\\n1ycC9VQscc69UlAacph8scliaskXsYDWiMwC4x0VuMUCgYA/FSHHAXLyBqoFqEpQ\\nRmfgXzUcU/mKEaGRPhAvoAU87Z74mEgaO5Hbv7zmFwWpD4cdW0WybzVv9r5Hs/hS\\n4JGBzx4KPFAygnVeXiftVuM9scycg3RHK907hmlYNZicI7TbUlM5RQ4ngn/LTjXo\\nG+TDTPn3Luw1fvAMEEcdsr9cJw==\\n-----END RSA PRIVATE KEY-----";

   @Test
   public void testCanGenerate() {
      Map<String, String> map = SshKeys.generate();
      assert map.get("public").startsWith("ssh-rsa ") : map;
      assert map.get("private").startsWith("-----BEGIN RSA PRIVATE KEY-----") : map;
   }

   @Test
   public void testEncodeAsPem() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String encoded = SshKeys.encodeAsPem((RSAPrivateCrtKey) KeyFactory.getInstance("RSA").generatePrivate(
            Pems.privateKeySpec(Payloads.newStringPayload(PemsTest.PRIVATE_KEY))));
      assertEquals(encoded.replace("\n", "\\n").trim(), SSH_PRIVATE_KEY);
   }

   @Test
   public void testEncodeAsOpenSSH() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String encoded = SshKeys.encodeAsOpenSSH((RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(
            Pems.publicKeySpec(Payloads.newStringPayload(PemsTest.PUBLIC_KEY))));
      assertEquals(encoded, SSH_PUBLIC_KEY);
   }

}
