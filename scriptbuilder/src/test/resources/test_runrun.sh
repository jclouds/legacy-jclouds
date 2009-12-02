mkdir -p /tmp/$USER/scripttest
rm /tmp/$USER/scripttest/yahooprod.sh 2>&-
echo '#!/bin/bash'>>/tmp/$USER/scripttest/yahooprod.sh
echo 'set +u'>>/tmp/$USER/scripttest/yahooprod.sh
echo 'shopt -s xpg_echo'>>/tmp/$USER/scripttest/yahooprod.sh
echo 'shopt -s expand_aliases'>>/tmp/$USER/scripttest/yahooprod.sh
echo "PROMPT_COMMAND='echo -ne \"\033]0;yahooprod\007\"'">>/tmp/$USER/scripttest/yahooprod.sh
echo "export INSTANCE_NAME='yahooprod'">>/tmp/$USER/scripttest/yahooprod.sh
echo "export JAVA_HOME='$JAVA_HOME'">>/tmp/$USER/scripttest/yahooprod.sh
echo 'cd /tmp/$USER/scripttest'>>/tmp/$USER/scripttest/yahooprod.sh
echo 'echo $JAVA_HOME/bin/java -DinstanceName=$INSTANCE_NAME myServer.Main'>>/tmp/$USER/scripttest/yahooprod.sh
echo 'exit 0'>>/tmp/$USER/scripttest/yahooprod.sh
chmod u+x /tmp/$USER/scripttest/yahooprod.sh
