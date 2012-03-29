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
package org.jclouds.ec2.xml;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.crypto.SshKeys;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code KeyPairResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "KeyPairResponseHandlerTest")
public class KeyPairResponseHandlerTest extends BaseEC2HandlerTest {
   public void testApplyInputStream() throws IOException {

      InputStream is = getClass().getResourceAsStream("/create_keypair.xml");

      KeyPair expected = KeyPair.builder().region(defaultRegion).keyName("adriancole-ec21").sha1OfPrivateKey(
               "13:36:74:b9:56:bb:07:96:c0:19:ab:00:7f:9f:06:d2:16:a0:45:32").keyMaterial(
               "-----BEGIN RSA PRIVATE KEY-----\n"
                        + "MIIEowIBAAKCAQEA0CbFlhSdbMdad2ux2BVqk6Ut5fLKb0CdbqubGcEBfwsSz9Rp4Ile76P90MpV\n"
                        + "W1BGKL5V4MO+flG6dZnRWPVmgrNVyDTmEsALiMGjfEwbACEZ1A8C6mPa36wWO7MlxuyMjg8OczTB\n"
                        + "EXnHNDpxE5a6KowJtzFlmgjHk2Y+Q42UIqPx47lQUv5bdMDCnfNNomSzTVRjOZLUkDja+ybCKdux\n"
                        + "gqTsuInhuBRMx+wxff8Z43ECdJV6UPoXK3der1dlZunxGCFkCeYq0kCX7FZ7PV35X744jqhD8P+7\n"
                        + "y5prO4W+M3DWgChUx0OlbDbSHtDVlcfdbj/+4AKYKU6rQOqh+4DPDQIDAQABAoIBAHjQuEiXKJSV\n"
                        + "1U2RZcVtENInws9AL/2I/Jfa5Qh6vTqXG9EjklywfzkK72x7tDVvD3ngmAoAs5WwLFDL+fXvYhOk\n"
                        + "sbql8ZCahVdYRWME7XsSu2IZYHDZipXe1XzLS7b9X8uos5Ns4E8bZuNKtI1RJDdD1vPMqRNR2z0T\n"
                        + "0Dn3eC7t+t+t7PWaK5AXu2ot7DoOeG1QhqJbwd5pMkIn2ydBILytgmDk/2P3EtJGePIJIeQBicmw\n"
                        + "Z0KrJFa/K2cC8AtmMJUoZMo+mh1yemDbDLCZW30PjFHbZtcszS2cydAgq/HDFkZynvZG0zhbx/To\n"
                        + "jzcNza1AyypYwOwb2/9/ulXZp0UCgYEA+QFgWDfYLH2zwjU5b6e0UbIyd/X/yRZ+L8lOEBd0Bbu8\n"
                        + "qO3txaDbwi7o2mG7pJENHJ3u62CHjgTGDNW9V9Q8eNoGtj3uHvMvi7FdDEK8B6izdZyR7hmZmQ/5\n"
                        + "MIldelyiGZlz1KBSoy4FsCpA7hV7cI6H6x+Im24NxG90/wd/EgMCgYEA1f+cUyUisIO3yKOCf0hQ\n"
                        + "aL289q2//F2cbvBxtki6I8JzTg1H3oTO2WVrXQeCA3a/yiuRUatgGH4mxrpCF6byVJyqrEWAj4kU\n"
                        + "uTbhMgIYhLGoaF1e+vMirCRXUXox0i5X976ASzHn64V9JSd1B+UbKfpcFTYYnChmrRDzmhKN1a8C\n"
                        + "gYBTvIHAyO7ab18/BRUOllAOVSWhr8lXv0eqHEEzKh/rOaoFCRY3qpOcZpgJsGogumK1Z+sLnoeX\n"
                        + "W8WaVVp6KbY4UeGF8aedItyvVnLbB6ohzTqkZ4Wvk05S6cs75kXYO0SL5U3NiCiiFXz2NA9nwTOk\n"
                        + "s1nD2PPgiQ76Kx0mEkhKLwKBgFhHEJqv+AZu37Kx2NRe5WS/2KK9/DPD/hM5tv7mM3sq7Nvm2J3v\n"
                        + "lVDS6J5AyZ5aLzXcER9qncKcz6wtC7SsFs1Wr4VPSoBroRPikrVJbgnXK8yZr+O/xq7Scv7WdJTq\n"
                        + "rzkw6cWbObvLnltkUn/GQBVqBPBvF2nbtLdyBbuqKb5bAoGBAI1+aoJnvXEXxT4UHrMkQcY0eXRz\n"
                        + "3UdbzJmtjMW9CR6l9s11mV6PcZP4qnODp3nd6a+lPeL3wVYQ47DsTJ/Bx5dI17zA5mU57n6mV0a3\n"
                        + "DbSoPKSdaKTQdo2THnVE9P9sPKZWueAcsE4Yw/qcTjoxrtUnAH/AXN250v0tkKIOvMhu\n"
                        + "-----END RSA PRIVATE KEY-----").build();

      KeyPairResponseHandler handler = injector.getInstance(KeyPairResponseHandler.class);
      addDefaultRegionToHandler(handler);
      KeyPair result = factory.create(handler).parse(is);

      assertEquals(result, expected);

      assert SshKeys.privateKeyHasSha1(result.getKeyMaterial(), result.getSha1OfPrivateKey());
      assert SshKeys.privateKeyHasFingerprint(result.getKeyMaterial(), result.getFingerprint());

   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of()).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
