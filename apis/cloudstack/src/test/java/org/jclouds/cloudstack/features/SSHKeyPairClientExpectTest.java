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
package org.jclouds.cloudstack.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.jclouds.cloudstack.CloudStackApiMetadata;
import org.jclouds.cloudstack.CloudStackContext;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.internal.BaseCloudStackExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.ssh.SshKeys;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * Test the CloudStack SSHKeyPairClient
 *
 * @author Andrei Savu
 */
@Test(groups = "unit", testName = "SSHKeyPairClientExpectTest")
public class SSHKeyPairClientExpectTest extends BaseCloudStackExpectTest<SSHKeyPairClient> {

   @Test
   public void testListAndGetSSHKeyPairsWhenResponseIs2xx() {
      HttpResponse response = HttpResponse.builder()
         .statusCode(200)
         .payload(payloadFromResource("/listsshkeypairsresponse.json"))
         .build();

      SSHKeyPairClient client = requestSendsResponse(HttpRequest.builder()
         .method("GET")
         .endpoint(
            URI.create("http://localhost:8080/client/api?response=json&" +
               "command=listSSHKeyPairs&listAll=true&apiKey=identity&signature=5d2J9u%2BdKpkQsadDbl9i9OcUSLQ%3D"))
            .addHeader("Accept", "application/json")
         .build(), response);

      assertEquals(client.listSSHKeyPairs(), ImmutableSet.of(
         SshKeyPair.builder().name("jclouds-keypair")
            .fingerprint("1c:06:74:52:3b:99:1c:95:5c:04:c2:f4:ba:77:6e:7b").build()));

      client = requestSendsResponse(HttpRequest.builder()
         .method("GET")
         .endpoint(
            URI.create("http://localhost:8080/client/api?response=json&command=listSSHKeyPairs&listAll=true&" +
               "name=jclouds-keypair&apiKey=identity&signature=hJIVCFOHhdOww3aq19tFHpeD2HI%3D"))
            .addHeader("Accept", "application/json")
         .build(), response);

      assertEquals(client.getSSHKeyPair("jclouds-keypair"),
         SshKeyPair.builder().name("jclouds-keypair")
            .fingerprint("1c:06:74:52:3b:99:1c:95:5c:04:c2:f4:ba:77:6e:7b").build());
   }

   @Test
   public void testCreateSSHKeyPairsWhenResponseIs2xx() {
      SSHKeyPairClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=createSSHKeyPair&" +
                  "name=jclouds-keypair&apiKey=identity&signature=8wk32PZF44jrBLH2HLel22%2BqMC4%3D"))
            .addHeader("Accept", "application/json")
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/createsshkeypairresponse.json"))
            .build());

      SshKeyPair actual = client.createSSHKeyPair("jclouds-keypair");
      SshKeyPair expected = SshKeyPair.builder().name("jclouds-keypair")
         .fingerprint("1c:06:74:52:3b:99:1c:95:5c:04:c2:f4:ba:77:6e:7b")
         .privateKey("-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIICXgIBAAKBgQDZo/EF4Ew1uEW0raz7vCs28lBwy0UKV2Xr606gaEgxO7h9mSXZ\n" +
            "4x2K/KQ1NMnrbjppxGycLh9EKPWAO3ezFULAyuOZW4Fy+xRS8+3MAijxBJY/KBgl\n" +
            "x5rJm2ILumRkTNkMlLGCSBb9SOqYRN1VpOy7kn3StzU9LdJ/snKVE2JLHQIDAQAB\n" +
            "AoGBAMnL5okKRd9xcsBqYIAxIuiZmNhcwTErhEdRMOAukPGFbDSYsa3rldLvGdpz\n" +
            "jd2LoQG8rO/LHBZ429kASqZzyiV+NvcgH+tFNJSVAigjSICfhEKF9PY2TiAkrg7S\n" +
            "GyJgAjpPWQc2sQh0dE8EPEtBiq4ibXfMTDmbs1d/vnfdwtQJAkEA+AX5Y+xgWj74\n" +
            "dYETmNLyLhNZpftLizEfIYj7lCVhsbFwVb8jbM1m8n8bxwGjls1w/ico1CWcQna+\n" +
            "UnAfA8kJvwJBAOCj0YgDKpYd0OLQhvI3212J9QcQpJEkDOTYiMwXNHCNMKRpoF47\n" +
            "MPPX+GG8YzUiQAi9/OG4pDKCjzQWE/ebiiMCQQCssnQ5WICqtggIwYykr9VDseON\n" +
            "SFIMpHJ5xkjumazRrqx6eDGxc8BH/6uWwRRoT7pqrVeniFyqhsX03u8pkpU/AkBj\n" +
            "WfCcwBHArNUqy2EzlWKuvwogosq16oTNXbs60HR/5uIBhTnJE1K2NemDiGc0I77A\n" +
            "Xw6N4jS0piuhtLYGB8OTAkEA50abdbduXWcr62Z6E8G/6LNFaNg0uBuVgwSHtJMd\n" +
            "dNeUtVDHQCHSf3tvxXTAtaB9PCnGOfgm/dyYWEMf3rMoHQ==\n" +
            "-----END RSA PRIVATE KEY-----\n")
         .build();

      assertEquals(actual, expected);
      assertEquals(SshKeys.fingerprintPrivateKey(actual.getPrivateKey()), expected.getFingerprint());
   }

   @Test
   public void testRegisterSSHKeyPairWhenResponseIs2xx() throws UnsupportedEncodingException {
      String publicKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCc903twxU2zcQnIJdXv61RwZNZW94uId9qz08fgsBJsCOnHNIC4+L9kDOA2IHV9cUfEDBm1Be5TbpadWwSbS/05E+FARH2/MCO932UgcKUq5PGymS0249fLCBPci5zoLiG5vIym+1ij1hL/nHvkK99NIwe7io+Lmp9OcF3PTsm3Rgh5T09cRHGX9horp0VoAVa9vKJx6C1/IEHVnG8p0YPPa1lmemvx5kNBEiyoNQNYa34EiFkcJfP6rqNgvY8h/j4nE9SXoUCC/g6frhMFMOL0tzYqvz0Lczqm1Oh4RnSn3O9X4R934p28qqAobe337hmlLUdb6H5zuf+NwCh0HdZ";

      String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
         "MIIEpQIBAAKCAQEAnPdN7cMVNs3EJyCXV7+tUcGTWVveLiHfas9PH4LASbAjpxzS\n" +
         "AuPi/ZAzgNiB1fXFHxAwZtQXuU26WnVsEm0v9ORPhQER9vzAjvd9lIHClKuTxspk\n" +
         "tNuPXywgT3Iuc6C4hubyMpvtYo9YS/5x75CvfTSMHu4qPi5qfTnBdz07Jt0YIeU9\n" +
         "PXERxl/YaK6dFaAFWvbyicegtfyBB1ZxvKdGDz2tZZnpr8eZDQRIsqDUDWGt+BIh\n" +
         "ZHCXz+q6jYL2PIf4+JxPUl6FAgv4On64TBTDi9Lc2Kr89C3M6ptToeEZ0p9zvV+E\n" +
         "fd+KdvKqgKG3t9+4ZpS1HW+h+c7n/jcAodB3WQIDAQABAoIBAQCX+iKr2LzLiUMo\n" +
         "lzexsFbB1+kxFe/zPryxD/QOEGzZa/+5KAB25+q5k0sqr3ZWkVXAk84pYaVut0F9\n" +
         "oD95P9q1A/GyV6zrNSHDywD+Lv0VMWMtkH0dV5Bjl7fY9DbhoXXIuAc81Rhs21mk\n" +
         "isIKME6Zra0VrYedGRfmE2usZc7F+rrnJeWs2edk1Q/lBLIe/v+NfRrO0fpHPu8S\n" +
         "9/kbVM3fUwHXxVTbvzZjjerQcLyEr4nT53DcSQJcm3e2DGsdRr5FBxkOXlcWElew\n" +
         "pbGM+RiF7RJvPW8lrmGj4y7Eo7TmfW8Yc5MM5A/PcvvxuRTRurmqOA5Wl1Bsp8/o\n" +
         "PEU/p9G5AoGBANcBOz0vSj+NOFip9gbc2WPVFpaoCT51DBQsT9R4kxe34Ltbwqaj\n" +
         "QXMiBjgereSM/KXTriA/Lhkj09YI5OAgk64PXcmDc2urMiFlewqxld79GDLAFwqn\n" +
         "nsEm1YTjY8wujw2J5Fbp7BZFHCrfld5L8xhgSb135YEa1/4LGOg+o6FDAoGBALrl\n" +
         "GL/v8ZDc2l/GpGsOA7360s9lRUhCTlQ86am8Lw/AdMSdpi9Is3yCdZx1NWDpUEKz\n" +
         "MBQTfiEEzpYlujvdUQNyQ4JGuhU/J7JEqEP2rfXaXjn0PIThkWFuNRkyK6Pz0rsT\n" +
         "4YJQouI7PCDE3BZxY4WYZ4uBZpCf3YC5SZiwtl0zAoGBAJGNnNwD+sDhSscDcLIe\n" +
         "qvDh3iPp6DAnLyEtCnItmm7RJcvRCAqltPZLj2hIpLJ4G8XrcxMTkpKkZZGdfcyZ\n" +
         "YUDR2E1Gt0mpoQto1w5bQLmwH8SjtDWbWmcqchw/kF03G9MviaypOhGtga8opB3U\n" +
         "zuKutN0WoQFw+c5bFuaLGV1fAoGABdFLy+20H0ZApeqRA6QUCb3dAges+GrX9VdQ\n" +
         "DrCE5oCfId+mZKJms+F7t7sORk386ZaaUIWqz2xO4e2atnJVKz5LS6rX8AFfQvVQ\n" +
         "J41uLND3TeaEW76Jv/amQHqHUTstvBUKV/waleAyJvL5xtkQt//eeUE16BqR0ofx\n" +
         "+obFpnECgYEAuDT1vH9JcGhD/iX4qLhS1xS1fXJh4IYvt8bg8oLRyRBqF6x9uhx3\n" +
         "6v+WQaKHyGvebWRN+SKAsKQHsh8a7Iy7xZdZmQ8v9j4DcYwJMb7ksV//R2kXAPGL\n" +
         "BTfRj1MSI+6AsuVY/YF1O2AfGneP+Zn5bQwYzQkxOYjzF9bhZz3IniE=\n" +
         "-----END RSA PRIVATE KEY-----\n";

      // Compute the fingerprint by using the following command:  ssh-keygen -lf key.pub
      String expectedFingerprint = "8f:f1:91:2d:b1:a8:51:f1:79:cf:c4:31:c4:14:9d:81";
      
      assertTrue(SshKeys.privateKeyMatchesPublicKey(privateKey, publicKey));
      assertEquals(SshKeys.fingerprintPublicKey(publicKey), expectedFingerprint);
      assertEquals(SshKeys.fingerprintPrivateKey(privateKey), expectedFingerprint);
      
      SSHKeyPairClient client = requestSendsResponse(
         HttpRequest.builder()
            .method("GET")
            .endpoint(
               URI.create("http://localhost:8080/client/api?response=json&command=registerSSHKeyPair&" +
                  "name=jclouds-keypair&publickey=" + Strings2.urlEncode(publicKey, '/') +
                  "&apiKey=identity&signature=g/6BXLnnvOMlKQBp1yM7GKlvfus%3D"))
            .headers(
               ImmutableMultimap.<String, String>builder()
                  .put("Accept", "application/json")
                  .build())
            .build(),
         HttpResponse.builder()
            .statusCode(200)
            .payload(payloadFromResource("/registersshkeypairresponse.json"))
            .build());

      SshKeyPair actual = client.registerSSHKeyPair("jclouds-keypair", publicKey);
      SshKeyPair expected = SshKeyPair.builder().name("jclouds-keypair")
         .fingerprint(expectedFingerprint).build();

      assertEquals(actual, expected);
      assertEquals(expectedFingerprint, expected.getFingerprint());
   }

   @Override
   protected SSHKeyPairClient clientFrom(CloudStackContext context) {
      return context.unwrap(CloudStackApiMetadata.CONTEXT_TOKEN).getApi().getSSHKeyPairClient();
   }
}
