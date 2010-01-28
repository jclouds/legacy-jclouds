package org.jclouds.aws.ec2.compute.domain;

import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.domain.Credentials;

public class KeyPairCredentials extends Credentials {
   private final KeyPair keyPair;

   public KeyPairCredentials(String account, KeyPair keyPair) {
      super(account, keyPair.getKeyMaterial());
      this.keyPair = keyPair;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((keyPair == null) ? 0 : keyPair.hashCode());
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
      KeyPairCredentials other = (KeyPairCredentials) obj;
      if (keyPair == null) {
         if (other.keyPair != null)
            return false;
      } else if (!keyPair.equals(other.keyPair))
         return false;
      return true;
   }

}