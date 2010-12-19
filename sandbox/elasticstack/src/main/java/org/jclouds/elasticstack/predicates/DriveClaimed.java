package org.jclouds.elasticstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.elasticstack.CommonElasticStackClient;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class DriveClaimed implements Predicate<DriveInfo> {

   private final CommonElasticStackClient client;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public DriveClaimed(CommonElasticStackClient client) {
      this.client = client;
   }

   public boolean apply(DriveInfo drive) {
      logger.trace("looking for claims on drive %s", checkNotNull(drive, "drive"));
      drive = refresh(drive);
      if (drive == null)
         return false;
      logger.trace("%s: looking for drive claims: currently: %s", drive.getUuid(), drive.getClaimed());
      return drive.getClaimed().size() > 0;
   }

   private DriveInfo refresh(DriveInfo drive) {
      return client.getDriveInfo(drive.getUuid());
   }
}
