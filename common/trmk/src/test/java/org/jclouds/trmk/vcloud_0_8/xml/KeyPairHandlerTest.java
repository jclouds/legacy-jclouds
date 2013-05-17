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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ssh.SshKeys;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code KeyHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "KeyPairHandlerTest")
public class KeyPairHandlerTest extends BaseHandlerTest {
   public static final KeyPair keyPair = new KeyPair(URI
            .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/key/13691"),
            "jclouds_te_r_1241", false, "-----BEGIN RSA PRIVATE KEY-----\n"
                     + "MIICWwIBAAKBgQCxdOcKd0WDuphOojK6Z2uErdgPsjei3/Xs6CzjL7a4ofd15rfz\n"
                     + "oRLbuNhLto4p/3drDjYdNrtPRLq5iNHq/qrRsHOJKS+bpAVQapuKDLCWwXa25nyM\n"
                     + "hG9iL/kwYGvS55CSOvHp5AH+diBuu6M9ay0w7idOBPnFdg4OMLIpAUTvqwICAQEC\n"
                     + "gYEAi3rDirdldHy/bp1eqG0tqQgkWAqVUTOEiGbMnqUBLINJ+Q7+KBUD3YJYU2qh\n"
                     + "i5f8Pjr7t1XZ8pmhtLFEXw3k40F7oV/NGs4CsnbYlxbegzHTzMNnN1dA+zj333jr\n"
                     + "N7U760b5D1cx5yTL5wF59gYaP8xugDnuCvRqbinqijApnzECQQDbJcxGUShVCpEh\n"
                     + "W0+BT/YHtM7BI1S6me7JdL5NMHsg1HE/0Ghi7nFUfbJDNsuIomRqbO5qcG7RpIN/\n"
                     + "xxsXyJFFAkEAz0xTsTiMxRMyaIpytoTliE3JBC/g5375eo2Epb740jbdkFvC5cin\n"
                     + "r6breljhbcm4LUIQG5sdnZspnxMH65N0LwJAEQ3mD27oPmZs6bFwxVTqTkvUUsP+\n"
                     + "n/QSlSf25yCo9Zr92yEnALnm86YrwX7JHY8gt5jptpoueebzUqjlOKMIUQJANgru\n"
                     + "+GljdB/mSxEq2uneNHfjuGAntOFZ8SHnz5FptU8M79gr1k02rExjPcd7hDeToDgS\n"
                     + "JRSEPAOYTFWlbTpdCwJAU1dI02ailOUpa1Dso5Mi7kO6QPygJfGyI6HlomvvHQZ3\n"
                     + "vyHKfLPKeAOfRYvkgHpf3q0WGoNh5M6GcQRwFQdhTA==\n" + "-----END RSA PRIVATE KEY-----",
            "fa:69:2c:0c:81:03:80:39:33:4d:de:ef:13:3c:e6:ef");

   public void testGood() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/key.xml");

      KeyPair result = factory.create(injector.getInstance(KeyPairHandler.class)).parse(is);

      assertEquals(result, keyPair);
      assertEquals(result.getFingerPrint(), SshKeys.fingerprintPrivateKey(result.getPrivateKey()));
   }

}
