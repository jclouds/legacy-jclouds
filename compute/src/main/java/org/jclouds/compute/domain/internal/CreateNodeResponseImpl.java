/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.compute.domain.internal;

import java.net.InetAddress;
import java.net.URI;
import java.util.Map;

import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.LoginType;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.domain.Credentials;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class CreateNodeResponseImpl extends NodeMetadataImpl implements CreateNodeResponse {

   /** The serialVersionUID */
   private static final long serialVersionUID = 3414239861247046054L;

   private final Credentials credentials;

   public CreateNodeResponseImpl(String id, String name, String location, URI uri,
            Map<String, String> userMetadata, NodeState state,
            Iterable<InetAddress> publicAddresses, Iterable<InetAddress> privateAddresses,
            int loginPort, LoginType loginType, Credentials credentials, Map<String, String> extra) {
      super(id, name, location, uri, userMetadata, state, publicAddresses, privateAddresses,
               loginPort, loginType, extra);
      this.credentials = credentials;
   }

   public Credentials getCredentials() {
      return credentials;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      CreateNodeResponseImpl other = (CreateNodeResponseImpl) obj;
      if (credentials == null) {
         if (other.credentials != null)
            return false;
      } else if (!credentials.equals(other.credentials))
         return false;
      return true;
   }

}
