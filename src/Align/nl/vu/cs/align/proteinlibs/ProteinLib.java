package nl.vu.cs.align.proteinlibs;

import java.util.*;

public interface ProteinLib {
	
	/** 
	 * @return Protein of a given name
	 */
	String get(String name);
	
	/** 
	 * @return All the names of the protein
	 */
	Collection getAllNames(String name);
	
	/** 
	 * When the protein has multiple names, the first name is returned
	 * @return The first name of the protein
	 */
	String getFirstName(String protein);
	
	/** 
	 * @return All the names of proteins in database
	 */
	Collection getAllNames();

	/** 
	 * @return All the first names of proteins in database
	 */
	Collection getAllFirstNames();

	/** 
	 * @return All the proteins
	 */
	Collection getAllProteins();
}
