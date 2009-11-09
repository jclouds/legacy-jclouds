package org.jclouds.aws.ec2.domain;


/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-TerminateInstancesResponseInfoType.html"
 *      />
 * @author Adrian Cole
 */
public class TerminatedInstance implements Comparable<TerminatedInstance> {

   private final String instanceId;
   private final InstanceState shutdownState;
   private final InstanceState previousState;

   public int compareTo(TerminatedInstance o) {
      return (this == o) ? 0 : getInstanceId().compareTo(o.getInstanceId());
   }

   public TerminatedInstance(String instanceId, InstanceState shutdownState,
            InstanceState previousState) {
      this.instanceId = instanceId;
      this.shutdownState = shutdownState;
      this.previousState = previousState;
   }

   public String getInstanceId() {
      return instanceId;
   }

   public InstanceState getShutdownState() {
      return shutdownState;
   }

   public InstanceState getPreviousState() {
      return previousState;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((instanceId == null) ? 0 : instanceId.hashCode());
      result = prime * result + ((previousState == null) ? 0 : previousState.hashCode());
      result = prime * result + ((shutdownState == null) ? 0 : shutdownState.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TerminatedInstance other = (TerminatedInstance) obj;
      if (instanceId == null) {
         if (other.instanceId != null)
            return false;
      } else if (!instanceId.equals(other.instanceId))
         return false;
      if (previousState == null) {
         if (other.previousState != null)
            return false;
      } else if (!previousState.equals(other.previousState))
         return false;
      if (shutdownState == null) {
         if (other.shutdownState != null)
            return false;
      } else if (!shutdownState.equals(other.shutdownState))
         return false;
      return true;
   }

}