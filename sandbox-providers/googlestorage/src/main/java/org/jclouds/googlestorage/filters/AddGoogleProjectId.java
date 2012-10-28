package org.jclouds.googlestorage.filters;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.rest.annotations.Identity;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jclouds.googlestorage.GoogleConstants.PROJECT_ID_HEADER;

@Singleton
public class AddGoogleProjectId implements
        HttpRequestFilter {

    private String projectId;

    @Inject
    AddGoogleProjectId(@Identity String identity) {
        this.projectId = identity.split("@")[0];
    }

    @Override
    public HttpRequest filter(HttpRequest request) throws HttpException {
        return request.toBuilder().addHeader(PROJECT_ID_HEADER, projectId).build();
    }
}
