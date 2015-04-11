AKKA_HOME="$(cd "$(cd "$(dirname "$0")"; pwd -P)"/..; pwd)"
echo $AKKA_HOME
AKKA_CLASSPATH="$AKKA_HOME/config:$AKKA_HOME/lib/*"
JAVA_OPTS="-Xms1024M -Xmx1024M -Xss1M -XX:MaxPermSize=256M"

java $JAVA_OPTS -cp "$AKKA_CLASSPATH" -Dakka.home="$AKKA_HOME" akka.kernel.Main com.xtuone.kernel.HelloKernel &