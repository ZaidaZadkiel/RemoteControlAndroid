#!/bin/bash
## this script assumes the NETCAT program OpenBSD netcat (Debian patchlevel 1.206-1ubuntu1)
nc -klp 45340 -vv &
while true; do  echo "server_online" | nc -luvv  4960 ; done
