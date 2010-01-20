package org.jclouds.vcloud.terremark.compute;

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudTemplate implements Template {
   private final TerremarkVCloudClient client;
   private final Set<? extends Image> images;
   private final Set<? extends Size> sizes;
   private Size size;
   private String vDC;
   private OperatingSystem operatingSystem;
   private transient Image image;

   public TerremarkVCloudTemplate(TerremarkVCloudClient client, Set<? extends Image> images,
            Set<? extends Size> sizes, String location, @Nullable Size size,
            OperatingSystem operatingSystem, @Nullable Image image) {
      this.client = client;
      this.images = images;
      this.sizes = sizes;
      this.vDC = location;
      this.image = image != null ? image : resolveTemplate(images, operatingSystem);
      this.size = size != null ? size : Iterables.get(sizes, 0);
      this.operatingSystem = operatingSystem;
   }

   private Image resolveTemplate(Set<? extends Image> images, OperatingSystem operatingSystem) {
      for (Image image : images) {
         if (image.getOperatingSystem() == operatingSystem)
            return image;
      }
      throw new RuntimeException("no configured image matches os: " + operatingSystem);
   }

   TerremarkVCloudTemplate(TerremarkVCloudClient client, Set<? extends Image> images,
            Set<? extends Size> sizes, String location) {
      this(client, images, sizes, location, null, OperatingSystem.UBUNTU, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Size getSize() {
      return size;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Image getImage() {
      return image;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template smallest() {
      this.size = Iterables.get(sizes, 0);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template biggest() {
      this.size = Iterables.getLast(sizes);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template fastest() {
      this.size = Iterables.getLast(sizes);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template inLocation(String location) {
      if (location.equalsIgnoreCase("default"))
         location = client.getDefaultVDC().getId();
      this.vDC = location;// TODO match vdc on template as well arch
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Template os(OperatingSystem os) {
      this.operatingSystem = os;
      this.image = resolveTemplate(images, operatingSystem);
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Object clone() throws CloneNotSupportedException {
      return new TerremarkVCloudTemplate(client, images, sizes, vDC, size, operatingSystem, image);
   }

   public String getvDC() {
      return vDC;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((image == null) ? 0 : image.hashCode());
      result = prime * result + ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
      result = prime * result + ((size == null) ? 0 : size.hashCode());
      result = prime * result + ((vDC == null) ? 0 : vDC.hashCode());
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
      TerremarkVCloudTemplate other = (TerremarkVCloudTemplate) obj;
      if (image == null) {
         if (other.image != null)
            return false;
      } else if (!image.equals(other.image))
         return false;
      if (operatingSystem == null) {
         if (other.operatingSystem != null)
            return false;
      } else if (!operatingSystem.equals(other.operatingSystem))
         return false;
      if (size == null) {
         if (other.size != null)
            return false;
      } else if (!size.equals(other.size))
         return false;
      if (vDC == null) {
         if (other.vDC != null)
            return false;
      } else if (!vDC.equals(other.vDC))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "TerremarkVCloudTemplate [image=" + image + ", operatingSystem=" + operatingSystem
               + ", size=" + size + ", vDC=" + vDC + "]";
   }

}
