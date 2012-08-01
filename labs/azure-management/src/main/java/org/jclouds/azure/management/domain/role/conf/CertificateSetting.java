package org.jclouds.azure.management.domain.role.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CertificateSetting")
public class CertificateSetting {

   /**
    * Specifies the name of the certificate store from which retrieve certificate.
    */
   @XmlElement(required = true, name = "StoreLocation")
   private String StoreLocation;
   /**
    * Specifies the target certificate store location on the virtual machine.
    * 
    * The only supported value is LocalMachine.
    */
   @XmlElement(required = true, name = "StoreName")
   private String StoreName;
   /**
    * Specifies the thumbprint of the certificate to be provisioned. The thumbprint must specify an
    * existing service certificate.
    */
   @XmlElement(required = true, name = "Thumbprint")
   private String Thumbprint;

   public CertificateSetting() {
      super();
   }

   public String getStoreLocation() {
      return StoreLocation;
   }

   public void setStoreLocation(String storeLocation) {
      StoreLocation = storeLocation;
   }

   public String getStoreName() {
      return StoreName;
   }

   public void setStoreName(String storeName) {
      StoreName = storeName;
   }

   public String getThumbprint() {
      return Thumbprint;
   }

   public void setThumbprint(String thumbprint) {
      Thumbprint = thumbprint;
   }

   @Override
   public String toString() {
      return "CertificateSetting [StoreLocation=" + StoreLocation + ", StoreName=" + StoreName + ", Thumbprint="
               + Thumbprint + "]";
   }

}
