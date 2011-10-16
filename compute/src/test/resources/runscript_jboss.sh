#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
unset PATH JAVA_HOME LD_LIBRARY_PATH
function abort {
   echo "aborting: $@" 1>&2
   exit 1
}
function default {
   export INSTANCE_NAME="jboss"
export INSTANCE_HOME="/usr/local/jboss"
export LOG_DIR="/usr/local/jboss"
   return 0
}
function jboss {
   export JBOSS_HOME="/usr/local/jboss"
   return 0
}
function findPid {
   unset FOUND_PID;
   [ $# -eq 1 ] || {
      abort "findPid requires a parameter of pattern to match"
      return 1
   }
   local PATTERN="$1"; shift
   local _FOUND=`ps auxwww|grep "$PATTERN"|grep -v " $0"|grep -v grep|grep -v $$|awk '{print $2}'`
   [ -n "$_FOUND" ] && {
      export FOUND_PID=$_FOUND
      return 0
   } || {
      return 1
   }
}
function forget {
   unset FOUND_PID;
   [ $# -eq 3 ] || {
      abort "forget requires parameters INSTANCE_NAME SCRIPT LOG_DIR"
      return 1
   }
   local INSTANCE_NAME="$1"; shift
   local SCRIPT="$1"; shift
   local LOG_DIR="$1"; shift
   mkdir -p $LOG_DIR
   findPid $INSTANCE_NAME
   [ -n "$FOUND_PID" -a -f $LOG_DIR/stdout.log ] && {
      echo $INSTANCE_NAME already running pid [$FOUND_PID]
      return 1;
   } || {
      nohup $SCRIPT >$LOG_DIR/stdout.log 2>$LOG_DIR/stderr.log &
      RETURN=$?
      # this is generally followed by findPid, so we shouldn't exit 
      # immediately as the proc may not have registered in ps, yet
      test $RETURN && sleep 1
      return $RETURN;
   }
}
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
case $1 in
init)
   default || exit 1
   jboss || exit 1
   cat >> /usr/local/jboss/standalone/configuration/standalone-custom.xml <<'END_OF_FILE'
<?xml version='1.0' encoding='UTF-8'?>

<server name="basic" xmlns="urn:jboss:domain:1.0">
    <extensions>
        <extension module="org.jboss.as.connector"/>
        <extension module="org.jboss.as.deployment-scanner"/>
        <extension module="org.jboss.as.ee"/>
        <extension module="org.jboss.as.logging"/>
        <extension module="org.jboss.as.naming"/>
        <extension module="org.jboss.as.security"/>
        <extension module="org.jboss.as.threads"/>
        <extension module="org.jboss.as.transactions"/>
        <extension module="org.jboss.as.web"/>
        <!--
        <extension module="org.jboss.as.weld"/>
        -->
    </extensions>
    <profile>
        <subsystem xmlns="urn:jboss:domain:logging:1.0">
            <console-handler name="CONSOLE" autoflush="true">
                <level name="INFO"/>
                <formatter>
                    <pattern-formatter pattern="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
                </formatter>
            </console-handler>
            <periodic-rotating-file-handler name="FILE" autoflush="true">
                <level name="INFO"/>
                <formatter>
                    <pattern-formatter pattern="%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="server.log"/>
                <suffix value=".yyyy-MM-dd"/>
            </periodic-rotating-file-handler>
            <logger category="com.arjuna">
                <level name="WARN"/>
            </logger>
            <logger category="org.apache.tomcat.util.modeler">
                <level name="WARN"/>
            </logger>
            <logger category="sun.rmi">
                <level name="WARN"/>
            </logger>
            <root-logger>
                <level name="INFO"/>
                <handlers>
                    <handler name="CONSOLE"/>
                    <handler name="FILE"/>
                </handlers>
            </root-logger>
        </subsystem>
        <subsystem xmlns="urn:jboss:domain:deployment-scanner:1.0">
            <deployment-scanner name="default" path="deployments" scan-enabled="true" scan-interval="5000" relative-to="jboss.server.base.dir" deployment-timeout="60"/>
        </subsystem>
        <subsystem xmlns="urn:jboss:domain:ee:1.0"/>
        <subsystem xmlns="urn:jboss:domain:naming:1.0"/>
        <subsystem xmlns="urn:jboss:domain:resource-adapters:1.0"/>
        <subsystem xmlns="urn:jboss:domain:security:1.0">
            <security-domains>
                <security-domain name="other" cache-type="default">
                    <authentication>
                        <login-module code="UsersRoles" flag="required"/>
                    </authentication>
                </security-domain>
            </security-domains>
        </subsystem>
        <subsystem xmlns="urn:jboss:domain:threads:1.0"/>
        <subsystem xmlns="urn:jboss:domain:transactions:1.0">
            <core-environment>
                <process-id>
                    <uuid/>
                </process-id>
            </core-environment>
            <recovery-environment socket-binding="txn-recovery-environment" status-socket-binding="txn-status-manager"/>
            <coordinator-environment default-timeout="300"/>
            <object-store/>
        </subsystem>
        <subsystem xmlns="urn:jboss:domain:web:1.0">
            <connector name="http" protocol="HTTP/1.1" socket-binding="http" scheme="http"/>
            <virtual-server name="localhost" enable-welcome-root="true">
                <alias name="example.com"/>
            </virtual-server>
        </subsystem>
        <!--
        <subsystem xmlns="urn:jboss:domain:weld:1.0"/>
        -->
    </profile>
    <interfaces>
        <interface name="public">
            <any-address/>
        </interface>
    </interfaces>
    <socket-binding-group name="standard-sockets" default-interface="public">
        <socket-binding name="http" port="8080"/>
        <socket-binding name="https" port="8443"/>
        <socket-binding name="jmx-connector-registry" port="1090"/>
        <socket-binding name="jmx-connector-server" port="1091"/>
        <socket-binding name="jndi" port="1099"/>
        <socket-binding name="osgi-http" port="8090"/>
        <socket-binding name="remoting" port="4447"/>
        <socket-binding name="txn-recovery-environment" port="4712"/>
        <socket-binding name="txn-status-manager" port="4713"/>
    </socket-binding-group>
</server>

END_OF_FILE
   mkdir -p $INSTANCE_HOME
   
   # create runscript header
   cat > $INSTANCE_HOME/jboss.sh <<END_OF_SCRIPT
#!/bin/bash
set +u
shopt -s xpg_echo
shopt -s expand_aliases
PROMPT_COMMAND='echo -ne "\033]0;jboss\007"'
export PATH=/usr/ucb/bin:/bin:/sbin:/usr/bin:/usr/sbin
export INSTANCE_NAME='jboss'
export JBOSS_HOME='$JBOSS_HOME'
export INSTANCE_NAME='$INSTANCE_NAME'
export INSTANCE_HOME='$INSTANCE_HOME'
export LOG_DIR='$LOG_DIR'
END_OF_SCRIPT
   
   # add desired commands from the user
   cat >> $INSTANCE_HOME/jboss.sh <<'END_OF_SCRIPT'
cd $INSTANCE_HOME
java  -server -Xms128m -Xmx128m -XX:MaxPermSize=128m -Djava.net.preferIPv4Stack=true -XX:+UseFastAccessorMethods -XX:+TieredCompilation -Xverify:none -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djboss.modules.system.pkgs=org.jboss.byteman -Dorg.jboss.boot.log.file=$JBOSS_HOME/standalone/log/boot.log -Dlogging.configuration=file:$JBOSS_HOME/standalone/configuration/logging.properties -jar $JBOSS_HOME/jboss-modules.jar -mp $JBOSS_HOME/modules -logmodule org.jboss.logmanager -jaxpmodule javax.xml.jaxp-provider org.jboss.as.standalone -Djboss.home.dir=$JBOSS_HOME --server-config=standalone-custom.xml
END_OF_SCRIPT
   
   # add runscript footer
   cat >> $INSTANCE_HOME/jboss.sh <<'END_OF_SCRIPT'
exit 0
END_OF_SCRIPT
   
   chmod u+x $INSTANCE_HOME/jboss.sh
   ;;
status)
   default || exit 1
   findPid $INSTANCE_NAME || exit 1
   echo [$FOUND_PID]
   ;;
stop)
   default || exit 1
   findPid $INSTANCE_NAME || exit 1
   [ -n "$FOUND_PID" ]  && {
      echo stopping $FOUND_PID
      kill -9 $FOUND_PID
   }
   ;;
start)
   default || exit 1
   forget $INSTANCE_NAME $INSTANCE_HOME/$INSTANCE_NAME.sh $LOG_DIR || exit 1
   ;;
tail)
   default || exit 1
   tail $LOG_DIR/stdout.log
   ;;
tailerr)
   default || exit 1
   tail $LOG_DIR/stderr.log
   ;;
run)
   default || exit 1
   $INSTANCE_HOME/$INSTANCE_NAME.sh
   ;;
esac
exit 0
