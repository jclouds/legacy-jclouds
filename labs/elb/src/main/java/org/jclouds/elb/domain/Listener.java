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
package org.jclouds.elb.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Listener is a process that listens for api connection requests. It is configured with a
 * protocol and a port number for front-end (Load Balancer) and back-end (Back-end instance)
 * connections.
 * 
 * 
 * By default, your load balancer is set to use the HTTP protocol with port 80 for the front-end
 * connection and the back-end connection. The default settings can be changed using the AWS
 * Management Console, the Query API, the command line interface (CLI), or the SDKs.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/DeveloperGuide/elb-listener-config.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class Listener {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromListener(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected int instancePort = -1;
      protected Protocol instanceProtocol;
      protected int port = -1;
      protected Protocol protocol;
      protected Optional<String> SSLCertificateId = Optional.absent();

      /**
       * @see Listener#getInstancePort()
       */
      public T instancePort(int instancePort) {
         this.instancePort = instancePort;
         if (port == -1)
             port = instancePort;
         return self();
      }

      /**
       * @see Listener#getInstanceProtocol()
       */
      public T instanceProtocol(Protocol instanceProtocol) {
         this.instanceProtocol = instanceProtocol;
         if (protocol == null)
             protocol = instanceProtocol;
         return self();
      }

      /**
       * @see Listener#getPort()
       */
      public T port(int port) {
         this.port = port;
         if (instancePort == -1)
             instancePort = port;
         return self();
      }

      /**
       * @see Listener#getProtocol()
       */
      public T protocol(Protocol protocol) {
         this.protocol = protocol;
         if (instanceProtocol == null)
            instanceProtocol = protocol;
         return self();
      }

      /**
       * @see Listener#getSSLCertificateId()
       */
      public T SSLCertificateId(String SSLCertificateId) {
         this.SSLCertificateId = Optional.fromNullable(SSLCertificateId);
         return self();
      }

      public Listener build() {
         return new Listener(instancePort, instanceProtocol, port, protocol, SSLCertificateId);
      }

      public T fromListener(Listener in) {
         return this.instancePort(in.getInstancePort()).instanceProtocol(in.getInstanceProtocol()).port(in.getPort())
                  .protocol(in.getProtocol()).SSLCertificateId(in.getSSLCertificateId().orNull());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final int instancePort;
   protected final Protocol instanceProtocol;
   protected final int port;
   protected final Protocol protocol;
   protected final Optional<String> SSLCertificateId;

   protected Listener(int instancePort, Protocol instanceProtocol, int port, Protocol protocol,
            Optional<String> SSLCertificateId) {
      this.instancePort = checkNonNegative(instancePort, "instancePort");
      this.instanceProtocol = checkNotNull(instanceProtocol, "instanceProtocol");
      this.port = checkNonNegative(port, "port");
      this.protocol = checkNotNull(protocol, "protocol");
      this.SSLCertificateId = checkNotNull(SSLCertificateId, "SSLCertificateId");
   }

   static int checkNonNegative(int in, String name) {
      checkArgument(in > 0, "%s must be non-negative", name);
      return in;
   }
   
   /**
    * The name associated with the LoadBalancer. The name must be unique within your set of
    * LoadBalancers.
    */
   public int getInstancePort() {
      return instancePort;
   }

   /**
    * Specifies the protocol to use for routing traffic to back-end instances - HTTP, HTTPS, TCP, or
    * SSL. This property cannot be modified for the life of the LoadBalancer.
    */
   public Protocol getInstanceProtocol() {
      return instanceProtocol;
   }

   /**
    * Specifies the external LoadBalancer port number. This property cannot be modified for the life
    * of the LoadBalancer.
    */
   public int getPort() {
      return port;
   }

   /**
    * Specifies the LoadBalancer transport protocol to use for routing - HTTP, HTTPS, TCP or SSL.
    * This property cannot be modified for the life of the LoadBalancer.
    */
   public Protocol getProtocol() {
      return protocol;
   }

   /**
    * The ARN string of the server certificate. To get the ARN of the server certificate, call the
    * AWS Identity and Access Management UploadServerCertificate API.
    */
   public Optional<String> getSSLCertificateId() {
      return SSLCertificateId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(instancePort, instanceProtocol, port, protocol, SSLCertificateId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Listener other = (Listener) obj;
      return Objects.equal(this.instancePort, other.instancePort)
               && Objects.equal(this.instanceProtocol, other.instanceProtocol) && Objects.equal(this.port, other.port)
               && Objects.equal(this.protocol, other.protocol)
               && Objects.equal(this.SSLCertificateId, other.SSLCertificateId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("instancePort", instancePort).add("instanceProtocol",
               instanceProtocol).add("port", port).add("protocol", protocol).add("SSLCertificateId",
               SSLCertificateId.orNull());
   }

}
