package org.jclouds.azure.management.domain.role.conf;

import javax.xml.bind.annotation.XmlElement;

public class SSHKey {

   /**
    * Specifies the SHA1 fingerprint of an X509 certificate associated with the hosted service that
    * includes the SSH public key.
    */
   @XmlElement(required = true, name = "FingerPrint")
   protected String fingerPrint;

   /**
    * Specifies the full path of a file, on the virtual machine, which stores the SSH public key. If
    * the file already exists, the specified key is appended to the file.
    */
   @XmlElement(required = true, name = "Path")
   protected String path;

   public SSHKey() {
   }

   public String getFingerPrint() {
      return fingerPrint;
   }

   public void setFingerPrint(String fingerPrint) {
      this.fingerPrint = fingerPrint;
   }

   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
   }
}
