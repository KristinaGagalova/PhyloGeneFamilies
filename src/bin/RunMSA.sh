#!/usr/bin/bash

#------------------------------------------------------------
# Usage
#------------------------------------------------------------

PROGRAM=$(basename $0)
read -r -d '' USAGE <<HEREDOC
Usage: $PROGRAM [options] <fasta> <prefix_out> 

Description:

Run ProGraphMSA with T-Reks for repeats domain analysis and output nwk tree

HEREDOC

set -eu -o pipefail


#----------------------------------------------------

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
