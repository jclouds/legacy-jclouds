package org.jclouds.azure.management.domain.role.conf;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DomainJoin")
public class DomainJoin {

   /**
    * Specifies the domain to join.
    */
   @XmlElement(name = "JoinDomain")
   private String joinDomain;
   /**
    * Specifies the Lightweight Directory Access Protocol (LDAP) X 500-distinguished name of the
    * organizational unit (OU) in which the computer account is created. This account is in Active
    * Directory on a domain controller in the domain to which the computer is being joined.
    */
   @XmlElement(name = "MachineObjectOU")
   private String machineObjectOU;
   /**
    * Specifies the Domain, Password, and Username values to use to join the virtual machine to the
    * domain.
    */
   @XmlElement(name = "Credentials")
   private Credentials credentials;

   public DomainJoin() {
      super();
   }

   public String getJoinDomain() {
      return joinDomain;
   }

   public void setJoinDomain(String joinDomain) {
      this.joinDomain = joinDomain;
   }

   public String getMachineObjectOU() {
      return machineObjectOU;
   }

   public void setMachineObjectOU(String machineObjectOU) {
      this.machineObjectOU = machineObjectOU;
   }

   public Credentials getCredentials() {
      return credentials;
   }

   public void setCredentials(Credentials credentials) {
      this.credentials = credentials;
   }

   @Override
   public String toString() {
      return "DomainJoin [joinDomain=" + joinDomain + ", machineObjectOU=" + machineObjectOU + ", credentials="
               + credentials + "]";
   }

}
