#!/usr/bin/env bash
################################################################################
# bootstrap.sh - Launches an executable jar directly.  This script should be
# prepended to an executable jar:
#     cat bootstrap.sh something.jar > something
#     chmod +x something
#     ./something --cool-option=yes
#
#     Non-standard JVM options (-Xsomething) are passed through to the JVM.
################################################################################

jvm_args=()
args=()

for arg in "$@"; do
    if [[ "${arg:0:2}" == '-X' ]]; then
        jvm_args+=("${arg}")
    else
        args+=("${arg}")
    fi
done

exec java "${jvm_args[@]}" -jar "$0" "${args[@]}"




