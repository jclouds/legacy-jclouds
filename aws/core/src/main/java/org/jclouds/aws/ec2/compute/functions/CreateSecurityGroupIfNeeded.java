package org.jclouds.aws.ec2.compute.functions;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.compute.domain.PortsRegionTag;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

@Singleton
public class CreateSecurityGroupIfNeeded implements Function<PortsRegionTag, String> {
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   protected final EC2Client ec2Client;

   @Inject
   public CreateSecurityGroupIfNeeded(EC2Client ec2Client) {
      this.ec2Client = ec2Client;
   }

   @Override
   public String apply(PortsRegionTag from) {
      createSecurityGroupInRegion(from.getRegion(), from.getTag(), from.getPorts());
      return from.getTag();
   }

   private void createSecurityGroupInRegion(Region region, String name, int... ports) {
      logger.debug(">> creating securityGroup region(%s) name(%s)", region, name);
      try {
         ec2Client.getSecurityGroupServices().createSecurityGroupInRegion(region, name, name);
         logger.debug("<< created securityGroup(%s)", name);
         for (int port : ports) {
            logger.debug(">> authorizing securityGroup region(%s) name(%s) port(%s)", region, name,
                     port);
            ec2Client.getSecurityGroupServices().authorizeSecurityGroupIngressInRegion(region,
                     name, IpProtocol.TCP, port, port, "0.0.0.0/0");
            logger.debug("<< authorized securityGroup(%s)", name);
         }
      } catch (AWSResponseException e) {
         if (e.getError().getCode().equals("InvalidGroup.Duplicate")) {
            logger.debug("<< reused securityGroup(%s)", name);
         } else {
            throw e;
         }
      }
   }

}
