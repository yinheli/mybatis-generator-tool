#!/bin/bash
set -e

working_dir=$(pwd)
script_dir=$(cd $(dirname $0); pwd)
cd $working_dir
java -Xmx128M -Dfile.encoding=UTF8 -jar $script_dir/mybatis-generator-tool-1.0.0.jar $@