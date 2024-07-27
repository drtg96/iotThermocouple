#!/bin/sh

# Used to run code on startup

# su -c cp daemon.sh /etc/init.d/
# su -c chmod 755 /etc/init.d/daemon.sh
# thermostad goes in /usr/sbin

DAEMON_NAME="thermostatd"

start()
{
    printf "Starting $DAEMON_NAME: "
    /usr/sbin/$DAEMON_NAME
    touch /var/lock/$DAEMON_NAME
    echo "OK"
}

stop()
{
    printf "Stopping $DAEMON_NAME: "
    killall $DAEMON_NAME
    rm -f /var/lock/$DAEMON_NAME
    echo "OK"
}

restart()
{
    stop
    start
}

case "$1" in
    start)
    start
    ;;
    stop)
    stop
    ;;
    restart)
    restart
    ;;
    *)
    echo "Usage: $0 {start|stop|restart}"
    exit 1
esac

exit $?

