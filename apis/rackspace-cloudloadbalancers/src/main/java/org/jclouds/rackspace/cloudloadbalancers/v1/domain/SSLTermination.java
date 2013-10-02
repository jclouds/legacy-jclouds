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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * The SSL Termination feature allows a load balancer user to terminate SSL traffic at the load balancer layer versus 
 * at the web server layer. A user may choose to configure SSL Termination using a key and an SSL certificate or an 
 * (Intermediate) SSL certificate.
 * <p/> 
 * When SSL Termination is configured on a load balancer, a secure shadow server is created that listens only for 
 * secure traffic on a user-specified port. This shadow server is only visible to and manageable by the system. 
 * Existing or updated attributes on a load balancer with SSL Termination will also apply to its shadow server. 
 * For example, if Connection Logging is enabled on an SSL load balancer, it will also be enabled on the shadow server 
 * and Cloud Files logs will contain log files for both.
 * <p/>
 * Notes
 * <ol>
 * <li>SSL Termination may only be configured on load balancers with non-secure protocols. For example, SSL Termination 
 * can be applied to an HTTP load balancer, but not to an HTTPS load balancer.</li>
 * <li>SSL-terminated load balancers decrypt the traffic at the traffic manager and pass unencrypted traffic to the 
 * back-end node. Because of this, the customer's back-end nodes don't know what protocol the client requested. 
 * Therefore the X-Forwarded-Proto (XFP) header has been added for identifying the originating protocol of an HTTP 
 * request as "http" or "https" depending on what protocol the client requested.</li>
 * <li>Not every service will return certificates in the proper order. Please verify that your chain of certificates 
 * matches that of walking up the chain from the domain to the CA root.</li>
 * </ol>
 * 
 * Warning
 * <ol>
 * <li>If SSL is enabled on a load balancer that is configured with nodes that are NOT in the same datacenter, then 
 * decrypted traffic will be sent in clear text over the public internet to the external node(s) and will no longer 
 * be secure.</li>
 * </ol>
 * 
 * <table border="1">
 *   <caption>
 *     Optional SSL Attributes
 *   </caption>
 *   <thead>
 *     <tr align="center">
 *       <td>Optional SSL Attributes</td>
 *       <td>Non-SSL Traffic</td>
 *       <td>SSL Traffic</td>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr align="left">
 *       <td><code class="code">enabled</code> = <code class="code">true</code> (default)</td>
 *       <td>Yes</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr align="left">
 *       <td><code class="code">enabled</code> = <code class="code">false</code></td>
 *       <td>Yes</td>
 *       <td>No</td>
 *     </tr>
 *     <tr align="left">
 *       <td><code class="code">secureTrafficOnly</code> = <code class="code">true</code></td>
 *       <td>No</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr align="left">
 *       <td><code class="code">secureTrafficOnly</code> = <code class="code">false</code> (default)</td>
 *       <td>Yes</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr align="left">
 *       <td>
 *         <p><code class="code">enabled</code> = <code class="code">true</code></p>
 *         <p><code class="code">secureTrafficOnly</code> = <code class="code">true</code></p>
 *     </td>
 *       <td>No</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr align="left">
 *       <td>
 *         <p><code class="code">enabled</code> = <code class="code">true</code></p>
 *         <p><code class="code">secureTrafficOnly</code> = <code class="code">false</code></p>
 *       </td>
 *       <td>Yes</td>
 *       <td>Yes</td>
 *     </tr>
 *     <tr align="left">
 *       <td>
 *         <p><code class="code">enabled</code> = <code class="code">false</code></p>
 *         <p><code class="code">secureTrafficOnly</code> = <code class="code">false</code></p>
 *       </td>
 *       <td>Yes</td>
 *       <td>No</td>
 *     </tr>
 *     <tr align="left">
 *       <td>
 *         <p><code class="code">enabled</code> = <code class="code">false</code></p>
 *         <p><code class="code">secureTrafficOnly</code> = <code class="code">true</code></p>
 *       </td>
 *       <td>Yes</td>
 *       <td>No</td>
 *     </tr>
 *   </tbody>
 * </table>
 * 
 *  
 * @author Everett Toews
 */
public class SSLTermination {
   private final boolean enabled;
   private final boolean secureTrafficOnly;
   private final int securePort;
   private final Optional<String> certificate;
   @Named("privatekey")
   private final Optional<String> privateKey;
   private final Optional<String> intermediateCertificate;

   @ConstructorProperties({ "enabled", "secureTrafficOnly", "securePort", "certificate", "privatekey",
         "intermediateCertificate" })
   protected SSLTermination(boolean enabled, boolean secureTrafficOnly, int securePort, String certificate,
         String privateKey, String intermediateCertificate) {
      this.enabled = enabled;
      this.secureTrafficOnly = secureTrafficOnly;
      this.securePort = securePort;
      this.certificate = Optional.fromNullable(certificate);
      this.privateKey = Optional.fromNullable(privateKey);
      this.intermediateCertificate = Optional.fromNullable(intermediateCertificate);
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public boolean getSecureTrafficOnly() {
      return this.secureTrafficOnly;
   }

   public int getSecurePort() {
      return this.securePort;
   }

   public Optional<String> getCertificate() {
      return this.certificate;
   }

   public Optional<String> getPrivateKey() {
      return this.privateKey;
   }

   public Optional<String> getIntermediateCertificate() {
      return this.intermediateCertificate;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(enabled, secureTrafficOnly, securePort, certificate, privateKey, intermediateCertificate);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      SSLTermination that = SSLTermination.class.cast(obj);

      return Objects.equal(this.enabled, that.enabled) && Objects.equal(this.secureTrafficOnly, that.secureTrafficOnly)
            && Objects.equal(this.securePort, that.securePort) && Objects.equal(this.certificate, that.certificate)
            && Objects.equal(this.privateKey, that.privateKey)
            && Objects.equal(this.intermediateCertificate, that.intermediateCertificate);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("enabled", enabled)
            .add("secureTrafficOnly", secureTrafficOnly).add("securePort", securePort)
            .add("certificate", certificate.orNull()).add("privateKey", privateKey.orNull())
            .add("intermediateCertificate", intermediateCertificate.orNull());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   public static class Builder {
      private boolean enabled;
      private boolean secureTrafficOnly;
      private int securePort;
      private String certificate;
      private String privateKey;
      private String intermediateCertificate;

      /**
       * Required. Determines if the load balancer is enabled to terminate SSL traffic.
       * </p>
       * If enabled = false, the load balancer will retain its specified SSL attributes, but will not terminate SSL traffic.
       */
      public Builder enabled(boolean enabled) {
         this.enabled = enabled;
         return this;
      }

      /**
       * Required. Determines if the load balancer may accept only secure traffic.
       * </p>
       * If secureTrafficOnly = true, the load balancer will not accept non-secure traffic.
       */
      public Builder secureTrafficOnly(boolean secureTrafficOnly) {
         this.secureTrafficOnly = secureTrafficOnly;
         return this;
      }

      /**
       * Required. The port on which the SSL termination load balancer will listen for secure traffic.
       * </p>
       * The securePort must be unique to the existing LB protocol/port combination. For example, port 443.
       */
      public Builder securePort(int securePort) {
         this.securePort = securePort;
         return this;
      }

      /**
       * Required. The certificate used for SSL termination.
       * </p>
       * The certificate is validated and verified against the key and intermediate certificate if provided.
       * </p>
       * All requests to SSL termination require the key/certificates to be in "proper" format, meaning that all raw 
       * line feed characters should be wrapped in a newline character. So if the user pastes in the key from a 
       * mykey.key file, it will not properly handle the field. For example, use string.replaceAll("\n", "\\n").
       */
      public Builder certificate(String certificate) {
         this.certificate = certificate;
         return this;
      }

      /**
       * Required. The private key for the SSL certificate.
       * </p>
       * The private key is validated and verified against the provided certificate(s).
       * 
       * @see SSLTermination#certificate(String)
       */
      public Builder privatekey(String privateKey) {
         this.privateKey = privateKey;
         return this;
      }

      /**
       * Optional only when configuring Intermediate SSL Termination. The user's intermediate certificate used for SSL
       * termination.
       * </p>
       * The intermediate certificate is validated and verified against the key and certificate credentials provided.
       * </p>
       * A user may only provide an intermediateCertificate when accompanied by a certificate, private key, and 
       * securePort. It may not be added to an existing SSL configuration as a single attribute in a future request.
       * 
       * @see SSLTermination#certificate(String)
       */
      public Builder intermediateCertificate(String intermediateCertificate) {
         this.intermediateCertificate = intermediateCertificate;
         return this;
      }

      public SSLTermination build() {
         return new SSLTermination(enabled, secureTrafficOnly, securePort, certificate, privateKey,
               intermediateCertificate);
      }

      public Builder from(SSLTermination in) {
         return this.enabled(in.getEnabled()).secureTrafficOnly(in.getSecureTrafficOnly())
               .securePort(in.getSecurePort()).certificate(in.getCertificate().orNull())
               .privatekey(in.getPrivateKey().orNull())
               .intermediateCertificate(in.getIntermediateCertificate().orNull());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
