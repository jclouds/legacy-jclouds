package org.jclouds.openstack.swift.blobstore.strategy.internal;

import com.google.inject.ImplementedBy;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.options.PutOptions;
import org.jclouds.openstack.swift.blobstore.functions.BlobToObject;
import org.jclouds.openstack.swift.blobstore.strategy.MultipartUpload;

@ImplementedBy(SequentialMultipartUploadStrategy.class)
public interface MultipartUploadStrategy extends MultipartUpload {

    String execute(String container, Blob blob, PutOptions options, BlobToObject blob2Object);
}
