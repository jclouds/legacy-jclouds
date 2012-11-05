package org.jclouds.azure.management.domain.role.conf;

import java.util.List;
import java.util.TimeZone;

import com.google.common.collect.Lists;

//@XmlRootElement(name = "ConfigurationSet")
public class WindowsProvisioningConfiguration extends ConfigurationSet {

   public static final String ID = "WindowsProvisioningConfiguration";

   /**
    * Specifies the computer name for the virtual machine
    * 
    * Computer names must be 1 to 15 characters in length.
    */
   // @XmlElement(name = "ComputerName")
   private String computerName;
   /**
    * Specifies the base-64 encoded string representing the administrator password to use for the
    * virtual machine.
    */
   // @XmlElement(required = true, name = "AdminPassword")
   private String adminPassword;
   /**
    * Specifies whether the user must change the administrator password on first logon. The default
    * value is false.
    */
   // @XmlElement(required = true, name = "ResetPasswordOnFirstLogon")
   private Boolean resetPasswordOnFirstLogon;
   /**
    * Specifies whether automatic updates are enabled for the virtual machine. The default value is
    * true.
    */
   // @XmlElement(name = "EnableAutomaticUpdates")
   private Boolean enableAutomaticUpdates;
   /**
    * Specifies the time zone for the virtual machine.
    */
   // @XmlElement(name = "TimeZone")
   private TimeZone timeZone;
   /**
    * Contains properties that specify a domain to which the virtual machine will be joined. The
    * DomainJoin node contains either credentials or provisioning information.
    */
   // @XmlElement(name = "DomainJoin")
   private DomainJoin domainJoin;
   /**
    * Contains a list of service certificates with which to provision to the new virtual machine.
    */
   // @XmlElementWrapper(required = true, name = "StoredCertificateSettings")
   // @XmlElement(name = "CertificateSetting")
   private List<CertificateSetting> storedCertificateSettings = Lists.newArrayList();

   public WindowsProvisioningConfiguration() {

   }

   public String getComputerName() {
      return computerName;
   }

   public void setComputerName(String computerName) {
      this.computerName = computerName;
   }

   public String getAdminPassword() {
      return adminPassword;
   }

   public void setAdminPassword(String adminPassword) {
      this.adminPassword = adminPassword;
   }

   public Boolean getResetPasswordOnFirstLogon() {
      return resetPasswordOnFirstLogon;
   }

   public void setResetPasswordOnFirstLogon(Boolean resetPasswordOnFirstLogon) {
      this.resetPasswordOnFirstLogon = resetPasswordOnFirstLogon;
   }

   public Boolean getEnableAutomaticUpdates() {
      return enableAutomaticUpdates;
   }

   public void setEnableAutomaticUpdates(Boolean enableAutomaticUpdates) {
      this.enableAutomaticUpdates = enableAutomaticUpdates;
   }

   public TimeZone getTimeZone() {
      return timeZone;
   }

   public void setTimeZone(TimeZone timeZone) {
      this.timeZone = timeZone;
   }

   public DomainJoin getDomainJoin() {
      return domainJoin;
   }

   public void setDomainJoin(DomainJoin domainJoin) {
      this.domainJoin = domainJoin;
   }

   public List<CertificateSetting> getStoredCertificateSettings() {
      return storedCertificateSettings;
   }

   public void setStoredCertificateSettings(List<CertificateSetting> storedCertificateSettings) {
      this.storedCertificateSettings = storedCertificateSettings;
   }

   @Override
   public String toString() {
      return "WindowsProvisioningConfigurationSet [computerName=" + computerName + ", adminPassword=" + adminPassword
               + ", resetPasswordOnFirstLogon=" + resetPasswordOnFirstLogon + ", enableAutomaticUpdates="
               + enableAutomaticUpdates + ", timeZone=" + timeZone + ", domainJoin=" + domainJoin
               + ", storedCertificateSettings=" + storedCertificateSettings + "]";
   }

}
