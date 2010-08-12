/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.opscodeplatform.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.crypto.Crypto;
import org.jclouds.crypto.Pems;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.jclouds.opscodeplatform.domain.User;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseUserFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseUserFromJsonTest")
public class ParseUserFromJsonTest {

   private ParseJson<User> handler;
   private Crypto crypto;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<User>>() {
      }));
      crypto = injector.getInstance(Crypto.class);
   }

   public void test() throws InvalidKeySpecException, CertificateException, IOException {
      User user = new User(
            "dopey",
            "Adrian",
            "",
            "Cole",
            "Adrian Cole",
            "adrian+dopey@opscode.com",
            null,
            null,
            null,
            null,
            "abcdef",
            crypto
                  .rsaKeyFactory()
                  .generatePublic(
                        Pems
                              .publicKeySpec("-----BEGIN RSA PUBLIC KEY-----\nMIIBCgKCAQEAvCKTqPdHv7GsHTjk02g91Aw3T6xQmdRaI70X6E6GGFxZtcH+tb1X\nqHxwhFydECVXhu0WVjTcWvxZ1aMFzn9BLHQYWzZxU/fIKVNR6ujyZ3jRxDXRFpX5\n/zvMdvNbdsJ+8foEbdoP1iujUMZuy6ZMvcbTDCgWjYVQ2omR9CkH/5Fwlbk3cSrF\n6qfGaM7340OGknKUfXdvhCq4vxydlOwfHJyNDWY0PW+8rDKHWxxNtYDDDeIMw2z/\nYC34f1bcAkR+/lyx5b25RwDomZNqXJqp1hjOVJVlo+UMvzWfXph5hgjcgtwzc5Iu\nmWWMUdxLcdw+/iQm6NW9cmU28bvHu0q7FwIDAQAB\n-----END RSA PUBLIC KEY-----\n")),
            null,
            Pems
                  .x509Certificate("-----BEGIN CERTIFICATE-----\nMIIClzCCAgCgAwIBAgIBATANBgkqhkiG9w0BAQUFADCBnjELMAkGA1UEBhMCVVMx\nEzARBgNVBAgMCldhc2hpbmd0b24xEDAOBgNVBAcMB1NlYXR0bGUxFjAUBgNVBAoM\nDU9wc2NvZGUsIEluYy4xHDAaBgNVBAsME0NlcnRpZmljYXRlIFNlcnZpY2UxMjAw\nBgNVBAMMKW9wc2NvZGUuY29tL2VtYWlsQWRkcmVzcz1hdXRoQG9wc2NvZGUuY29t\nMB4XDTEwMDgwMzA0MDUzNVoXDTIwMDczMTA0MDUzNVowADCCASIwDQYJKoZIhvcN\nAQEBBQADggEPADCCAQoCggEBALwik6j3R7+xrB045NNoPdQMN0+sUJnUWiO9F+hO\nhhhcWbXB/rW9V6h8cIRcnRAlV4btFlY03Fr8WdWjBc5/QSx0GFs2cVP3yClTUero\n8md40cQ10RaV+f87zHbzW3bCfvH6BG3aD9Yro1DGbsumTL3G0wwoFo2FUNqJkfQp\nB/+RcJW5N3EqxeqnxmjO9+NDhpJylH13b4QquL8cnZTsHxycjQ1mND1vvKwyh1sc\nTbWAww3iDMNs/2At+H9W3AJEfv5cseW9uUcA6JmTalyaqdYYzlSVZaPlDL81n16Y\neYYI3ILcM3OSLplljFHcS3HcPv4kJujVvXJlNvG7x7tKuxcCAwEAATANBgkqhkiG\n9w0BAQUFAAOBgQBcoSP/2tFhP8yjF/dRDRdDed0/Cg0xnpp2wvM38gBRgvhpZbQ3\nI2rqpw5THNzrzBVnrYxd57uAa+y2MMG57XnvNWOmyL6WIYXLfN1QI3nHdpHS/QVF\nCRWpDWxLM1TkqAD9xQZOpUDdByF2exiCDNTzSYYg/ISLlIEzicNJeoPNbA==\n-----END CERTIFICATE-----\n"),
            "xBOmipWikVEicS7tNOPdQzPsPmsROMgPme5O19ZHh6R7N9MQT7d5olDiGFpO");

      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(ParseUserFromJsonTest.class
            .getResourceAsStream("/user.json")))), user);
   }
}
