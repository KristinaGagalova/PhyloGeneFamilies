# PhyloGeneFamilies
Cluster gene families and display phylogeny tree by species

The software is based on [ProGraphMSA](https://github.com/acg-team/ProGraphMSA) and R - ggtree.

## Dependencies:
T-Reks (java) - wget http://bioinfo.montp.cnrs.fr/t-reks/T-Reks.jar           
TRUST: Tracking Repeats Using Transitivity and Significance - wget http://www.ibi.vu.nl/programs/trustwww/trust.tgz             

## R packages
* ggtree   
* treeio      
* RColorBrewer    
* ggplot2      
* grid       
* gridExtra        

## Installation
Download the GitHub repo and add the ```src``` directory to your PATH (linux users). The directory contains the necessary software and scripts to run.


## Run PhyloGene

Run the tool as a bash script

```
bash RunPhyloGene.sh <fasta_seq><prefix><sequence>
#fasta_seq - fasta file of sequences to be aligned and plotted
#prefix - name for files and plot
#either prot or dna, based on the sequences in fasta_seq
```


![Test image](/images/TestImage.png)


## References
* Adam M Szalkowski, Fast and robust multiple sequence alignment with phylogeny-aware gap placement, BMC Bioinformatics. 2012; 13: 129.
* Julien Jorda, Andrey V. Kajava, T-REKS: identification of Tandem REpeats in sequences with a K-meanS based algorithm, Bioinformatics, Volume 25, Issue 20, 15 October 2009
