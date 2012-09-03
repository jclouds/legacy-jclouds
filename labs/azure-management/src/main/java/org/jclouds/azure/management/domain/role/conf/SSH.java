package org.jclouds.azure.management.domain.role.conf;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SSH")
public class SSH {

   /**
    * Specifies the collection of SSH public keys.
    */
   @XmlElementWrapper(name = "PublicKeys")
   @XmlElement(name = "PublicKey")
   private List<PublicKey> publicKeys = new ArrayList<PublicKey>(0);

   /**
    * Specifies the public key.
    */
   @XmlElementWrapper(name = "KeyPairs")
   @XmlElement(name = "KeyPair")
   private List<KeyPair> keyPairs = new ArrayList<KeyPair>(0);

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
