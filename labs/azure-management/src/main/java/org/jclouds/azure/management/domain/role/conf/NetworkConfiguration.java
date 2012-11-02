package org.jclouds.azure.management.domain.role.conf;

import java.util.ArrayList;
import java.util.List;

//@XmlRootElement(name = "ConfigurationSet")
public class NetworkConfiguration extends ConfigurationSet {

   public static final String ID = "NetworkConfiguration";

   /**
    * Contains a collection of external endpoints for the virtual machine.
    */
   // @XmlElementWrapper(name = "InputEndpoints")
   // @XmlElement(name = "InputEndpoint")
   private List<InputEndpoint> inputEndpoints = new ArrayList<InputEndpoint>(0);

   /**
    * Specifies the name of a subnet to which the virtual machine belongs.
    */
   // @XmlElementWrapper(name = "SubnetNames")
   // @XmlElement(name = "SubnetName")
   private List<String> subnetNames = new ArrayList<String>(0);

   public NetworkConfiguration() {
      setConfigurationSetType(ID);
   }

   public List<InputEndpoint> getInputEndpoints() {
      return inputEndpoints;
   }

   public void setInputEndpoints(List<InputEndpoint> inputEndpoints) {
      this.inputEndpoints = inputEndpoints;
   }

   public List<String> getSubnetNames() {
      return subnetNames;
   }

   public void setSubnetNames(List<String> subnetNames) {
      this.subnetNames = subnetNames;
   }

   @Override
   public String toString() {
      return "NetworkConfigurationSet [configurationSetType=" + configurationSetType + ", InputEndpoints="
               + inputEndpoints + ", SubnetNames=" + subnetNames + "]";
   }

}
