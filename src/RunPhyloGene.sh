#!/usr/bin/bash

#------------------------------------------------------------
# Usage
#------------------------------------------------------------

PROGRAM=$(basename $0)
read -r -d '' USAGE <<HEREDOC
Usage: $PROGRAM [options] <fasta> <prefix_out>

Description:

Run PhyloGene for MSA alignment and generation of phylo plot

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

RunMSA.sh $fasta $prefix $sequence

grep "#=GF" ${prefix}.out > ${prefix}.nwk
paste <(grep ">" $fasta | sed 's/>//' | cut -d"_" -f1) <(grep ">" $fasta | sed 's/>//') > Labs.out

RplotPhylo.R ${prefix}.nwk Labs.out ${prefix}

#remove intermediates
rm Labs.out
