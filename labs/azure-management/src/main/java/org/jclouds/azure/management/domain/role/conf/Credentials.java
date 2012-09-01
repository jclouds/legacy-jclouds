package org.jclouds.azure.management.domain.role.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Credentials")
public class Credentials {

   /**
    * Specifies the name of the domain used to authenticate an account. The value is a fully
    * qualified DNS domain.
    */
   @XmlElement(name = "Domain")
   private String domain;
   /**
    * Specifies a user name in the domain that can be used to join the domain.
    */
   @XmlElement(required = true, name = "Username")
   private String username;
   /**
    * Specifies the password to use to join the domain.
    */
   @XmlElement(name = "Password")
   private String password;

   public Credentials() {
      super();
   }

   public String getDomain() {
      return domain;
   }

   public void setDomain(String domain) {
      this.domain = domain;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   @Override
   public String toString() {
      return "Credentials [domain=" + domain + ", username=" + username + ", password=" + password + "]";
   }

}
