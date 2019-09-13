package nl.vu.cs.align.substtable;

import java.io.*;


public class VariableTable extends SubstitutionTable {

	public VariableTable(String fname) throws IOException {
		super(fname);
	}

	public void increaseValues(int inc) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
//				if (matrix[y][x]>0)
					matrix[y][x] += inc;
			}
		}
	}
	
}
