#!/usr/bin/env Rscript

#############################
####Kristina Gagalova########
#############################
#########Sept 2019###########
#############################

#Description: Plot phylogeny from nwk and barplot of species frequency

library(ggtree)
library(treeio)
library(RColorBrewer)
library(ggplot2)
library(grid)
library(gridExtra)

#input
options(echo=TRUE) # if you want see commands in output file
args <- commandArgs(trailingOnly = TRUE)
print(args)
# trailingOnly=TRUE means that only your arguments are returned, check:
# print(commandArgs(trailingOnly=FALSE))

treeIn <- args[1]
labsIn <- args[2]
namIn <- args[3]

##read tree
tree = read.tree(treeIn)
##read labels file
labs = read.delim(labsIn, header=FALSE)

labs.df = as.data.frame(table(labs$V1))
names(labs.df) = c("Species","Frequency")

classes = split(labs$V2,as.factor(labs$V1))

tree_labs = groupOTU(tree, classes)

#ctree = ggtree(tree_labs, aes(color=group), layout="circular") 

#ggtree_plot
ggtree_plot= ggtree(tree_labs, aes(color=group), layout="circular") + geom_tiplab(size=2, aes(angle=angle)) +
  theme(legend.position = 'none', 
        legend.title = element_blank(), # no title
        legend.key = element_blank()) + # no keys
  scale_color_brewer("group", palette="Dark2") + 
  labs(title=namIn)

cols_cor = "test"
len_cols = length(unique(attr(tree_labs,"group")))
if ( len_cols != nrow(labs.df) ) {
  cols = brewer.pal(length(unique(attr(tree_labs,"group"))), "Dark2")
  cols = brewer.pal(len_cols, "Dark2")
  cols_cor = cols[-1]
} else {
  cols_cor = brewer.pal(nrow(labs.df), "Dark2")
}

barplot_spec <- ggplot(labs.df, aes(x=Species, y=Frequency, fill=Species) ) +
  geom_bar(stat="identity") + theme_minimal() + scale_fill_manual(values=cols_cor) + xlab("") + 
  theme(legend.position = "none") + 
  geom_text(aes(label = Frequency),vjust = 1)

nam = paste0(namIn,'.png')
png(nam, width = 7, height = 7, units = 'in', res = 300)
grid.arrange(ggtree_plot, barplot_spec,nrow=2,ncol=1,heights=c(0.8,0.2))
dev.off()
