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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.EncryptedPasswordAndPrivateKey;
import org.jclouds.cloudstack.functions.WindowsLoginCredentialsFromEncryptedData;
import org.jclouds.cloudstack.internal.BaseCloudStackRestClientExpectTest;
import org.jclouds.crypto.Crypto;
import org.jclouds.encryption.bouncycastle.BouncyCastleCrypto;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * Test the CloudStack VirtualMachineClientClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "VirtualMachineClientExpectTest")
public class VirtualMachineClientExpectTest extends BaseCloudStackRestClientExpectTest<VirtualMachineClient> {

   public void testGetPasswordForVirtualMachineWhenResponseIs2xx() throws NoSuchAlgorithmException, CertificateException {
      String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
         "MIICXgIBAAKBgQDnaPKhTNgw7qPJVp3qsT+7XhhAbip25a0AnUgq8Fb9LPcZk00p\n" +
         "jm+m4JrKmDWKZWrHMNBhCNHMzvV9KrAXUMzL4s7mdEicbxTKratTYoyJM7a87bcZ\n" +
         "xr+Gtoq4tm031Cix3LKyJUB0iSVU5V/Zx4QcaF5+FWcYMVI26x2Eaz+O7wIDAQAB\n" +
         "AoGBAOI8sDkSL6pnJKmKjQkOEQjVjVAwZEOpd+HJ4uxX3DPY6huO7zlZj77Oh4ba\n" +
         "GD4duK7VAmRbgwGAtHCSc2XYEN7ICnfkQrm+3Q8nS824Sz21WlzdCxKDFkDcC1wK\n" +
         "RjE7SwXN1Kj8Xq8Vpf+z6OzHatSRZD85JM3u0/QCksOJTVIBAkEA9OpycYTuUYjC\n" +
         "2pLrO5kkl0nIHbNPvFNZyle19AsHH0z/ClV8DiFtGQpwhqwCoWT0cTmSACPD/quA\n" +
         "hdc2mvV+4QJBAPHiBi/7qDpJldLLvK5ALbn1yRaPSDXLccvFV4FkSS9b/2+mOM2a\n" +
         "8JkolVCzImxAm0ZZDZeAGKJj1RZDsMIP188CQCfZKWus7DWZ4dI8S0e0IA75czTZ\n" +
         "4uRKT3arlLAzRyJhnbFpvThzWdPULgDLZdYqndb6PfYF27LI5q1gGcNWpCECQQCB\n" +
         "r8/ldiZyafW8eaQGQT7DD7brM5Nh1FyFBp+uLljW3ZqNADBAfKw3Uf0MsZ7pL5KR\n" +
         "GzogWnvaxXAAafahdeEdAkEAzBT+UcxFmcPUO33PnuuiX5KIqThc6aHjjH5O7yzO\n" +
         "m4Et9JwQiSgcPBmNY5NKPgmcpvUi9jDylSUV0VUu436RpQ==\n" +
         "-----END RSA PRIVATE KEY-----";

      VirtualMachineClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&" +
                  "command=getVMPassword&id=1&apiKey=identity&signature=SVA2r1KRj4yG03rATMLPZWS%2BKnw%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/getvmpasswordresponse.json"))
            .build());

      String actual = client.getEncryptedPasswordForVirtualMachine("1");
      String expected = "EFOwm8icZ4sEib4y6ntVHUKHZJQrGBdyPkL1L9lpFHYhs3JfAtL5E5bxBP5Er27bJyOZPjKFcInX\r\n" +
         "pQ0LZlQBZDd5/ac0NSoM6tAX3H30pYxNw4t2f9u8aJ48oOEvufgGxTTHnM9qHXD04lt+Ouql6i2q\r\n" +
         "HxBqCxFkMZEla3LFieE=\r\n";

      assertEquals(actual, expected);

      Crypto crypto = new BouncyCastleCrypto();
      WindowsLoginCredentialsFromEncryptedData passwordDecrypt = new WindowsLoginCredentialsFromEncryptedData(crypto);

      assertEquals(passwordDecrypt.apply(
         EncryptedPasswordAndPrivateKey.builder().encryptedPassword(actual).privateKey(privateKey).build()).getPassword(), "bX7vvptvw");
   }

   @Override
   protected VirtualMachineClient clientFrom(CloudStackContext context) {
      return context.getProviderSpecificContext().getApi().getVirtualMachineClient();
   }
}
