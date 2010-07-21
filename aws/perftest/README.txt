This sample uses the Google App Engine for Java SDK located at http://googleappengine.googlecode.com/files/appengine-java-sdk-1.3.5.zip

Please unzip the above file and modify your maven settings.xml like below before attempting to run 'mvn -Plive install'

    <profile>
      <id>appengine</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <appengine.home>/path/to/appengine-java-sdk-1.3.5</appengine.home>
      </properties>
    </profile>

    <profile>
      <id>aws</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <jclouds.aws.accesskeyid>YOUR_ACCESS_KEY_ID</jclouds.aws.accesskeyid>
        <jclouds.aws.secretaccesskey>YOUR_SECRET_KEY</jclouds.aws.secretaccesskey>
      </properties>
    </profile>

    <repositories>
        <repository>
            <id>jclouds</id>
            <url>http://jclouds.googlecode.com/svn/trunk/repo</url>
        </repository>
        <repository>
            <id>jclouds-rimu-snapshots-nexus</id>
            <url>http://jclouds.rimuhosting.com:8081/nexus/content/repositories/snapshots</url>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
