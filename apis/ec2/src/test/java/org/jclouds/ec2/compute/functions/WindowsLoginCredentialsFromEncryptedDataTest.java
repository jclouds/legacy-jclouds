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
package org.jclouds.ec2.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.jclouds.crypto.Crypto;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.domain.PasswordDataAndPrivateKey;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.encryption.bouncycastle.BouncyCastleCrypto;
import org.testng.annotations.Test;

/**
 * @author Richard Downer
 */
public class WindowsLoginCredentialsFromEncryptedDataTest {

   private static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
      "MIIEowIBAAKCAQEAmN6GOSMnyGNWN19ETBh11tJB5OGs3Dps8kPWqAhF9RyL/mKwkW26vH+h/5Z5\n" +
      "cA5T80pK72kNnXObFaMHNoX3lavrc6yXF+8F3f1tlFX2Z+iB1pYXz1oBPqT6oOmc2XzcsJuJRakd\n" +
      "zwRwHDaqljpaW7+TZlxhMa1DmUkD/HHMxDCK8jbUIZDc6BZSrnj2uPwHwW737NRE4aC3fcu4LMwf\n" +
      "b2VotbNGNiAnNmrb/vtIIGkFE8NYEMpiz0WYTWX4eVKpJImv1PR6G1fMLSvudJs0ARObuLDvuonn\n" +
      "SCFFdkibrwMKYbHVGGh6FoY1Vy0sqI55dgQU1kSNouiDgOGxgx+TIwIDAQABAoIBAHCS/nk5QGS7\n" +
      "cpRYXa1EHhNSxx/MaUXM6MoH1x3q6cm1egqdlrWh/vAtdZkIsOkqQ/xX65Me493dcomegwNN6KOZ\n" +
      "9Uw7/xCq/sEZjga8vzaJ7IOgCGy0NVJyn/a70rv+zW5pO8/G2KLI+95rC3iSBFSoYd3xjcnNdIh/\n" +
      "UqYnD8oxYpKmf7418pMPsBrkglkFlbVBPiDXdpoSziqSN6uWQG4Yh0WR87aElhM9JJW50Hh6h7g5\n" +
      "OvgCBzS8G+KXCjqimk108+/ed5Nl6VhPAf79yCVZUueKBhaf2r0Kkyxg7M/Y+LJwcoUusIP7Cv7G\n" +
      "xyzG2vi21prWRCm2sVCUDyQy5qECgYEA92jGVAaB3OGEUIXn7eVE3U3FQH37XcJMGsHqBIzDG13p\n" +
      "C97HdN21rwRkz+G2eAsIxA+p9BsO7dSmtKC60kl6iMRgltS3W7xoC37N9BtjhpciHcLg8c70oyDx\n" +
      "qHiLKuDi90mZ1FPmWupO4FJnGEB3evHUKZSpTrVVMzt+tyEn/psCgYEAni1hrYoMkQgN3sEC3CKB\n" +
      "0jQkrOMvY219B8Tdf9LXSuP6z9POagDBDhkeT3xn8rAOmOfVGHYdO0CvPqmAkmXhf+g+OREdecQa\n" +
      "uY0FmvcTt+Dx0c6pRZmm5AhvUVXFXqONsSg79iviXbUy5Hik0k5HTs5E6B4obrh5W+xfMTUXghkC\n" +
      "gYBn92uAW8uumkYT4HF6EuJBbTD6zPYYjFGW3O4OQ2ip02jfSBrhDVoP1fTXNq6K+3gPi9WLcuNv\n" +
      "JfF37iMTwzTuzDcaqwDyV9YRHpRFhEzqfhAkGYSVmLZM5scmWKGCv0YhTJiMFUWz5sqGkZopIs4S\n" +
      "qBTT9FjBbooDIXk6U4CPCQKBgFdVBxEhnz6UC9RpDIMuKi88yuMJrChhUx7u+ryQVH3s0ZXdg6HT\n" +
      "OMPn6mxIa7v6qJSTq3wN+qW0WQ1n2Kz7wz0zpOctI/EO7RJ1YhrlP+XONLV6PMtIwnQ0lAF8MbTG\n" +
      "6HxfknugTyMd4DN0yMu0nHpOOI1P2VMIVzkBkK1CevBBAoGBALROGR7a+eijHdp0/A0chfUoBmud\n" +
      "/TsUt+0g/vf1p69rMt6DqEGMgMtp2jIRnwvLElS7gVqnCTEclxNU/0rCXR+V7ImJm8J4f0ff8m0Y\n" +
      "Fir9nfCYStszo25NvLFfynS9d/aoBuvqGJaiQyNXiyBJ4MaxxFYagzAWTnDX+kzTlkZ2\n" +
      "-----END RSA PRIVATE KEY-----";
   private static final String ENCRYPTED_PASSWORD = "gO1oMoIjjIifv2iqcfIKiQD7ziOTVXsuaBJFEQrZdb8uJH/LsAiJXZeGKEeXlHl/oMoR3HEIoYuHxl+p5iHdrpP889RmxWBDGOWC5iTUzK6CRa5mFmF1I5Lpt7v2YeVoQWihSM8B19BEdBdY1svQp9nyhPB4AqLDrY28x/OrmRh/qYq953i6Y4Z8c76OHqqGcUYM4ePysRlcizSgQjdkEDmKC10Ak3OFRRx3/LqYsFIMiOHeg47APg+UANNTyRiTIia5FDhSeHJzaeYCBRQ7UYH0z2rg4cX3YjOz/MoznjHiaaN4MO+5N3v84VawnqwKOvlwPyI2bmz0+9Tr6DKzqA==";

   @Test
   public void testApply() throws Exception {
      Crypto crypto = new BouncyCastleCrypto();
      WindowsLoginCredentialsFromEncryptedData f = new WindowsLoginCredentialsFromEncryptedData(crypto);

      PasswordData passwordData = PasswordData.builder().passwordData(ENCRYPTED_PASSWORD).build();

      LoginCredentials credentials = f.apply(new PasswordDataAndPrivateKey(passwordData, PRIVATE_KEY));

      assertEquals(credentials.getUser(), "Administrator");
      assertEquals(credentials.getPassword(), "u4.y9mb;nR.");
      assertFalse(credentials.getOptionalPrivateKey().isPresent());
   }
}
