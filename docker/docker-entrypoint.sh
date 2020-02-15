#!/usr/bin/env sh
./server/bin/server &
sleep 20
./java-daemon/bin/java-daemon &
# Add Python Daemon start script

# wait forever not to exit the container
while true
do
  tail -f /dev/null & wait ${!}
done
