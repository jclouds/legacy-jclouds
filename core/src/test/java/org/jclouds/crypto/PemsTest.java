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
package org.jclouds.crypto;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;

import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "jclouds.PemsTest")
public class PemsTest {

   public static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\nMIIEpQIBAAKCAQEAyb2ZJJqGm0KKR+8nfQJNsSd+F9tXNMV7CfOcW6jsqs8EZgiV\nR09hD1IYOj4YqM0qJONlgyg4xRWewdSG7QTPj1lJpVAida9sXy2+kzyagZA1Am0O\nZcbqb5hoeIDgcX+eDa79s0u0DomjcfO9EKhvHLBz+zM+3QqPRkPV8nYTbfs+HjVz\nzOU6D1B0XR3+IPZZl2AnWs2d0qhnStHcDUvnRVQ0P482YwN9VgceOZtpPz0DCKEJ\n5Tx5STub8k0/zt/VAMHQafLSuQMLd2s4ZLuOZptN//uAsTmxireqd37z+8ZTdBbJ\n8LEpJ+iCXuSfm5aUh7iw6oxvToY2AL53+jK2UQIDAQABAoIBAQDA88B3i/xWn0vX\nBVxFamCYoecuNjGwXXkSyZew616A+EOCu47bh4aTurdFbYL0YFaAtaWvzlaN2eHg\nDb+HDuTefE29+WkcGk6SshPmiz5T0XOCAICWw6wSVDkHmGwS4jZvbAFm7W8nwGk9\nYhxgxFiRngswJZFopOLoF5WXs2td8guIYNslMpo7tu50iFnBHwKO2ZsPAk8t9nnS\nxlDavKruymEmqHCr3+dtio5eaenJcp3fjoXBQOKUk3ipII29XRB8NqeCVV/7Kxwq\nckqOBEbRwBclckyIbD+RiAgKvOelORjEiE9R42vuqvxRA6k9kd9o7utlX0AUtpEn\n3gZc6LepAoGBAP9ael5Y75+sK2JJUNOOhO8ae45cdsilp2yI0X+UBaSuQs2+dyPp\nkpEHAxd4pmmSvn/8c9TlEZhr+qYbABXVPlDncxpIuw2Ajbk7s/S4XaSKsRqpXL57\nzj/QOqLkRk8+OVV9q6lMeQNqLtEj1u6JPviX70Ro+FQtRttNOYbfdP/fAoGBAMpA\nXjR5woV5sUb+REg9vEuYo8RSyOarxqKFCIXVUNsLOx+22+AK4+CQpbueWN7jotrl\nYD6uT6svWi3AAC7kiY0UI/fjVPRCUi8tVoQUE0TaU5VLITaYOB+W/bBaDE4M9560\n1NuDWO90baA5dfU44iuzva02rGJXK9+nS3o8nk/PAoGBALOL6djnDe4mwAaG6Jco\ncd4xr8jkyPzCRZuyBCSBbwphIUXLc7hDprPky064ncJD1UDmwIdkXd/fpMkg2QmA\n/CUk6LEFjMisqHojOaCL9gQZJPhLN5QUN2x1PJWGjs1vQh8Tkx0iUUCOa8bQPXNR\n+34OTsW6TUna4CSZAycLfhffAoGBAIggVsefBCvuQkF0NeUhmDCRZfhnd8y55RHR\n1HCvqKIlpv+rhcX/zmyBLuteopYyRJRsOiE2FW00i8+rIPRu4Z3Q5nybx7w3PzV9\noHN5R5baE9OyI4KpZWztpYYitZF67NcnAvVULHHOvVJQGnKYfLHJYmrJF7GA1ojM\nAuMdFbjFAoGAPxUhxwFy8gaqBahKUEZn4F81HFP5ihGhkT4QL6AFPO2e+JhIGjuR\n27+85hcFqQ+HHVtFsm81b/a+R7P4UuCRgc8eCjxQMoJ1Xl4n7VbjPbHMnIN0Ryvd\nO4ZpWDWYnCO021JTOUUOJ4J/y0416Bvkw0z59y7sNX7wDBBHHbK/XCc=\n-----END RSA PRIVATE KEY-----\n";

   public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyb2ZJJqGm0KKR+8nfQJNsSd+F9tXNMV7CfOcW6jsqs8EZgiVR09hD1IYOj4YqM0qJONlgyg4xRWewdSG7QTPj1lJpVAida9sXy2+kzyagZA1Am0OZcbqb5hoeIDgcX+eDa79s0u0DomjcfO9EKhvHLBz+zM+3QqPRkPV8nYTbfs+HjVzzOU6D1B0XR3+IPZZl2AnWs2d0qhnStHcDUvnRVQ0P482YwN9VgceOZtpPz0DCKEJ5Tx5STub8k0/zt/VAMHQafLSuQMLd2s4ZLuOZptN//uAsTmxireqd37z+8ZTdBbJ8LEpJ+iCXuSfm5aUh7iw6oxvToY2AL53+jK2UQIDAQAB\n-----END PUBLIC KEY-----\n";

   private static final String CERTIFICATE = "-----BEGIN CERTIFICATE-----\nMIIClzCCAgCgAwIBAgIBATANBgkqhkiG9w0BAQUFADCBnjELMAkGA1UEBhMCVVMx\nEzARBgNVBAgMCldhc2hpbmd0b24xEDAOBgNVBAcMB1NlYXR0bGUxFjAUBgNVBAoM\nDU9wc2NvZGUsIEluYy4xHDAaBgNVBAsME0NlcnRpZmljYXRlIFNlcnZpY2UxMjAw\nBgNVBAMMKW9wc2NvZGUuY29tL2VtYWlsQWRkcmVzcz1hdXRoQG9wc2NvZGUuY29t\nMB4XDTEwMDczMDIwNDEzMFoXDTIwMDcyNzIwNDEzMFowADCCASIwDQYJKoZIhvcN\nAQEBBQADggEPADCCAQoCggEBAMm9mSSahptCikfvJ30CTbEnfhfbVzTFewnznFuo\n7KrPBGYIlUdPYQ9SGDo+GKjNKiTjZYMoOMUVnsHUhu0Ez49ZSaVQInWvbF8tvpM8\nmoGQNQJtDmXG6m+YaHiA4HF/ng2u/bNLtA6Jo3HzvRCobxywc/szPt0Kj0ZD1fJ2\nE237Ph41c8zlOg9QdF0d/iD2WZdgJ1rNndKoZ0rR3A1L50VUND+PNmMDfVYHHjmb\naT89AwihCeU8eUk7m/JNP87f1QDB0Gny0rkDC3drOGS7jmabTf/7gLE5sYq3qnd+\n8/vGU3QWyfCxKSfogl7kn5uWlIe4sOqMb06GNgC+d/oytlECAwEAATANBgkqhkiG\n9w0BAQUFAAOBgQBftzSZxstWw60GqRTDNN/F2GnrdtnKBoXzHww3r6jtGEylYq20\n5KfKpEx+sPX0gyZuYJiXC2CkEjImAluWKcdN9ZF6VD541sheAjbiaU7q7ZsztTxF\nWUH2tCvHeDXYKPKek3QzL7bYpUhLnCN/XxEv6ibeMDwtI7f5qpk2Aspzcw==\n-----END CERTIFICATE-----\n";

   @Test
   public void testPrivateKeySpecFromPem() throws IOException {
      Pems.privateKeySpec(Payloads.newStringPayload(PRIVATE_KEY));
   }

   @Test
   public void testPublicKeySpecFromPem() throws IOException {
      Pems.publicKeySpec(Payloads.newStringPayload(PUBLIC_KEY));
   }

   @Test
   public void testX509CertificateFromPemDefault() throws IOException, CertificateException {
      Pems.x509Certificate(Payloads.newStringPayload(CERTIFICATE), null);
   }

   @Test
   public void testX509CertificateFromPemSuppliedCertFactory() throws IOException, CertificateException {
      Pems.x509Certificate(Payloads.newStringPayload(CERTIFICATE), CertificateFactory.getInstance("X.509"));
   }

   @Test(enabled = false)
   // TODO figure out how to write back in the same format
   public void testPrivateKeySpecPem() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String encoded = Pems.pem(KeyFactory.getInstance("RSA").generatePrivate(
               Pems.privateKeySpec(Payloads.newStringPayload(PRIVATE_KEY))));
      assertEquals(encoded, PRIVATE_KEY.replaceAll("\n", "").replaceAll("Y-----", "Y-----\n").replaceAll("-----E",
               "\n-----E"));
   }

   @Test
   public void testPublicKeySpecPem() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
      String encoded = Pems.pem(KeyFactory.getInstance("RSA").generatePublic(
               Pems.publicKeySpec(Payloads.newStringPayload(PUBLIC_KEY))));
      assertEquals(encoded, PUBLIC_KEY.replaceAll("\n", "").replaceAll("Y-----", "Y-----\n").replaceAll("-----E",
               "\n-----E"));
   }

   @Test
   public void testX509CertificatePem() throws IOException, CertificateException {
      String encoded = Pems.pem(Pems.x509Certificate(Payloads.newStringPayload(CERTIFICATE), CertificateFactory
               .getInstance("X.509")));
      assertEquals(encoded, CERTIFICATE.replaceAll("\n", "").replaceAll("E-----", "E-----\n").replaceAll("-----E",
               "\n-----E"));
   }

}
