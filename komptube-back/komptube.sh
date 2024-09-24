#!/bin/bash

workdir=~/myscripts/komptube

if [ -z $1 ]; then
    cmd=ps
  else
    cmd=$1
fi

#-----------------------------------------------------------------------------------------------------------------------

function help() {
    echo 'Syntax: ktube [command|--help]'
    echo 'Without arguments trys to find running process.'
    echo '--help  - shows this help'
    echo 'ps      - trys to find running process'
    echo 'start   - starts background webserver'
    echo 'stop    - stops background webserver'
    echo 'stopf   - forces to stop background webserver'
    echo 'log     - shows logs'
    echo 'logf    - shows and follows logs'
    echo 'clear   - clears logs'
}

#-----------------------------------------------------------------------------------------------------------------------

function start() {
  java -jar -Durls.phone="http://192.168.1.3:8765" ~/myscripts/jars/komptube-back.jar \
    > /dev/null \
    2>>/dev/null & \
    echo $! > "$workdir/komptube.pid"
}

#-----------------------------------------------------------------------------------------------------------------------

function killproc() {
  if [[ $1 == "force" ]]; then
    signame="-SIGKILL"
  else
    signame=""
  fi

  echo "***** kill komptube backend service"
  if [[ -f "$workdir/komptube.pid" ]]; then
    kill $signame $(cat "$workdir/komptube.pid") && rm "$workdir/komptube.pid"
  fi
}

#-----------------------------------------------------------------------------------------------------------------------

function stop() {
  killproc $1
}

#-----------------------------------------------------------------------------------------------------------------------

function log() {
  if [[ -f "$workdir/komptube.log" ]]; then
    if [ -z $1 ]; then
        cat "$workdir/komptube.log"
      else
        if [ $1 = 'true' ]; then
            follow="-f"
          else
            lines="-n $1"
        fi
        if [ -z $2 ]; then
            lines="-n 50"
          else
            lines="-n $2"
        fi
        tail -s 0.3 $follow $lines "$workdir/komptube.log"
    fi
    else
      echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
      echo "Не найден файл логов ""$workdir/komptube.log"
      echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  fi
}

#-----------------------------------------------------------------------------------------------------------------------

function _ps() {
  if [[ -f "$workdir/komptube.pid" ]]; then
    pid="$(cat $workdir/komptube.pid)"
    ps aux|head -n 1; ps aux|grep -i $pid | grep java
  fi
}

#-----------------------------------------------------------------------------------------------------------------------

curr_dir="$(pwd)"
cd "$workdir"

case $cmd in
  --help)
    help
    ;;
  start)
    start
    ;;
  stop)
    stop
    ;;
  stopf)
    stop force
    ;;
  ps)
    _ps
    ;;
  log)
    log
    ;;
  logf)
    log true $2
    ;;
  clear)
    echo > "$workdir/komptube.log"
    ;;
esac

cd "$curr_dir"
