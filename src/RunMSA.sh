#!/usr/bin/bash

#------------------------------------------------------------
# Usage
#------------------------------------------------------------

PROGRAM=$(basename $0)
read -r -d '' USAGE <<HEREDOC
Usage: $PROGRAM [options] <fasta> <prefix_out> 

Description:

Run ProGraphMSA with T-Reks for repeats domain analysis and output nwk tree

Options:
    
    -h        show this help message
    -j N      threads [1]

HEREDOC

set -eu -o pipefail

help=0

#----------------------------------------------------

# -h for help message
if [ $help -ne 0 ]; then
	echo "$USAGE"
	exit 0;
fi

# we expect 2 file arguments
if [ $# -lt 3  ]; then
    echo "Error: number of file args must be  3" >&2
	echo "$USAGE" >&2
	exit 1
fi

#Variables
fasta=$1; shift;
prefix=$1; shift;
sequence=$1; shift

if [ "${sequence}" == "prot" ]; then
ProGraphMSA_64 --custom_tr_cmd trust2treks.py --trd_output Treks.${prefix}.out \
	-R --repalign -o ${prefix}.out $fasta |& tee > ${prefix}.log
elif [ "${sequence}" == "dna" ]; then
ProGraphMSA_64 --custom_tr_cmd trust2treks.py --trd_output Treks.${prefix}.out \
        -R --repalign --dna -o ${prefix}.out $fasta |& tee > ${prefix}.log
else
echo "Sorry, specify either dna or prot"
fi
