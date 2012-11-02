package org.jclouds.azure.management.domain.role.conf;


public class LinuxProvisioningConfiguration extends ConfigurationSet {

   public static final String ID = "LinuxProvisioningConfiguration";

   /**
    * Specifies the host name for the VM. Host names are ASCII character strings 1 to 64 characters
    * in length.
    */
   // @XmlElement(required = true,name = "HostName")
   private String hostName;
   /**
    * Specifies the name of a user to be created in the sudoer group of the virtual machine. User
    * names are ASCII character strings 1 to 32 characters in length.
    */
   // @XmlElement(required = true,name = "UserName")
   private String userName;
   /**
    * Specifies the associated password for the user name. PasswoazureManagement are ASCII character
    * strings 6 to 72 characters in length.
    */
   // @XmlElement(required = true,name = "UserPassword")
   private String userPassword;
   /**
    * Specifies whether or not SSH password authentication is disabled. By default this value is set
    * to true.
    */
   // @XmlElement(name = "DisableSshPasswordAuthentication")
   private Boolean disableSshPasswordAuthentication;
   /**
    * Specifies the SSH public keys and key pairs to populate in the image during provisioning.
    */
   // @XmlElement(name = "SSH")
   private SSH ssh;

   public LinuxProvisioningConfiguration() {

   }

   public String getHostName() {
      return hostName;
   }

   public void setHostName(String hostName) {
      this.hostName = hostName;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getUserPassword() {
      return userPassword;
   }

   public void setUserPassword(String userPassword) {
      this.userPassword = userPassword;
   }

   public Boolean getDisableSshPasswordAuthentication() {
      return disableSshPasswordAuthentication;
   }

   public void setDisableSshPasswordAuthentication(Boolean disableSshPasswordAuthentication) {
      this.disableSshPasswordAuthentication = disableSshPasswordAuthentication;
   }

   public SSH getSsh() {
      return ssh;
   }

   public void setSsh(SSH ssh) {
      this.ssh = ssh;
   }

   @Override
   public String toString() {
      return "LinuxProvisioningConfigurationSet [hostName=" + hostName + ", userName=" + userName + ", userPassword="
               + userPassword + ", disableSshPasswordAuthentication=" + disableSshPasswordAuthentication + ", ssh="
               + ssh + "]";
   }

}
