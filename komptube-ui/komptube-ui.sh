#!/bin/bash

workdir=~/myscripts/komptube

if [ -z $1 ]; then
    cmd=ps
  else
    cmd=$1
fi

#-----------------------------------------------------------------------------------------------------------------------

function help() {
    echo 'Syntax: ktubeui [command|--help]'
    echo 'Without arguments trys to find running process.'
    echo '--help  - shows this help'
    echo 'ps      - trys to find running process'
    echo 'start   - starts UI'
    echo 'stop    - stops UI'
    echo 'stopf   - forces to stop UI'
    echo 'log     - shows logs'
    echo 'logf    - shows and follows logs'
    echo 'clear   - clears logs'
}

#-----------------------------------------------------------------------------------------------------------------------

function start() {
  java -jar -Durls.phone="http://192.168.1.3:8765" -Durls.local_backend="http://localhost:8765" ~/myscripts/jars/komptube-ui.jar \
    > "$workdir/komptube-ui.log" \
    2>>"$workdir/komptube-ui.log"
}

#-----------------------------------------------------------------------------------------------------------------------

function killproc() {
  if [[ $1 == "force" ]]; then
    signame="-SIGKILL"
  else
    signame=""
  fi

  echo "***** kill komptube UI"
  if [[ -f "$workdir/pid" ]]; then
    kill $signame $(cat "$workdir/pid") && rm "$workdir/komptube-ui.pid"
  fi
}

#-----------------------------------------------------------------------------------------------------------------------

function stop() {
  killproc $1
}

#-----------------------------------------------------------------------------------------------------------------------

function log() {
  if [[ -f "$workdir/komptube-ui.log" ]]; then
    if [ -z $1 ]; then
        cat "$workdir/komptube-ui.log"
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
        tail -s 0.3 $follow $lines "$workdir/komptube-ui.log"
    fi
    else
      echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
      echo "Не найден файл логов ""$workdir/komptube-ui.log"
      echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  fi
}

#-----------------------------------------------------------------------------------------------------------------------

function _ps() {
  if [[ -f "$workdir/komptube-ui.pid" ]]; then
    pid="$(cat $workdir/komptube-ui.pid)"
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
    echo > "$workdir/komptube-ui.log"
    ;;
esac

cd "$curr_dir"
