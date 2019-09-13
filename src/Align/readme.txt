readme.txt, v1.0
TRUST: Tracking Repeats Using Transitivity and Significance

-------------------------------------------------------------------------------
RUNNING THE PROGRAM

Run the program with the command

java -cp . nl.vu.cs.align.SelfSimilarity -fasta fasta_file 
-matrix substitution_matrix_file [-seg] [-noseg] [-o output_dir] 
[-protein protein] [-max maximum_protein_length] 
-gapo gap_opening_penalty -gapx gap_extension_penalty [-force] [-noforce] 
[-procNr cpu_number -procTotal proc_total]
	

Possible parameters are:

-fasta fasta_file		File with sequences to find repeats in
-seg					(default) Expect fasta_file.seg file, filtered with seg 
						program (obtained with 
						seg -x fasta_file > fasta_file.seg)
-noseg 					Do not use filtered sequences
-matrix substitution_matrix_file 
-gapo gap_opening_penalty 
-gapx gap_extension_penalty 
-o output_dir 
-protein protein 		Protein name from fasta_file 
-max maximum_protein_length 	
-force 					Relaxed significance constraints - see paper for 
						the description
-noforce 				(default)
-procNr cpu_number		When running on a cluster, specify 0-based CPU-number and amount of processors used
-procTotal proc_total	When running on a cluster, specify the amount of processors used

-------------------------------------------------------------------------------
SOURCE CODE

If you're going to change/recompile the source you may need PNGEncoder package 
(available to download from http://catcode.com/pngencoder/).
