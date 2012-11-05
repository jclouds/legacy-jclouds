package org.jclouds.azure.management.domain.role.conf;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

@XmlRootElement(name = "SSH")
public class SSH {

   /**
    * Specifies the collection of SSH public keys.
    */
   @XmlElementWrapper(name = "PublicKeys")
   @XmlElement(name = "PublicKey")
   private List<PublicKey> publicKeys = Lists.newArrayListWithCapacity(0);

   /**
    * Specifies the public key.
    */
   @XmlElementWrapper(name = "KeyPairs")
   @XmlElement(name = "KeyPair")
   private List<KeyPair> keyPairs = Lists.newArrayListWithCapacity(0);

   public SSH() {
   }

   public List<PublicKey> getPublicKeys() {
      return publicKeys;
   }

   public void setPublicKeys(List<PublicKey> publicKeys) {
      this.publicKeys = publicKeys;
   }

   public List<KeyPair> getKeyPairs() {
      return keyPairs;
   }

   public void setKeyPairs(List<KeyPair> keyPairs) {
      this.keyPairs = keyPairs;
   }

   @Override
   public String toString() {
      return "SSH [publicKeys=" + publicKeys + ", keyPairs=" + keyPairs + "]";
   }
}
