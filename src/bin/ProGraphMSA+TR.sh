#!/bin/bash
bashSrc=${BASH_SOURCE[0]}
wrkDir=$( dirname ${bashSrc} )
BINARY="$wrkDir/bin/ProGraphMSA_32"
echo $wrkDir
SYSTEM=$(uname -s)
ARCH=$(uname -m)

if [[ "$SYSTEM" = "Linux" ]]; then
   if [[ "$ARCH" = "i386" ]]; then
      BINARY="${wrkDir}/bin/ProGraphMSA_32"
   elif [[ "$ARCH" = "x86_64" ]]; then
      BINARY="${wrkDir}/bin/ProGraphMSA_64"
   fi
elif [[ "$SYSTEM" = "Darwin" ]]; then
   BINARY="${wrkDir}/bin/ProGraphMSA_64"
fi

# Check whether ProGraphMSA+TR is run from the correct directory

if [[ ! -x "${BINARY}" ]]; then
   echo "ProGraphMSA binary not found at \"${BINARY}\". Make sure you run the program from the installation directory"
   exit 1
fi

if [[ ! $* =~ trust2treks ]]; then
   # Check whether T-REKS is installed and download if necessary
   if [[ ! -f "${wrkDir}/T-REKS/T-Reks.jar" ]]; then
      read -p "T-REKS does not seem to be installed here. Download it? [y/n]" -n 1 INST
      echo
      if [[ ${INST} =~ ^[Yy]$ ]]; then
         wget http://bioinfo.montp.cnrs.fr/t-reks/T-Reks.jar
      fi
   fi
else
   # Check whether TRUST is installed and download if necessary
   if [[ ! -f "${wrkDir}/Align/nl/vu/cs/align/SelfSimilarity.class" ]]; then
      read -p "TRUST does not seem to be installed here. Download it? [y/n]" -n 1 INST
      echo
      if [[ ${INST} =~ ^[Yy]$ ]]; then
         wget http://www.ibi.vu.nl/programs/trustwww/trust.tgz
         tar xzf trust.tgz
      fi
   fi
fi

${BINARY} --repeat_indel_rate 0.1 --repeat_indel_ext 0.3 --mldist --repeats --fasta $@
#--custom_tr_cmd trust2treks.py
#
