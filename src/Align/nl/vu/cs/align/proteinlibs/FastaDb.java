package nl.vu.cs.align.proteinlibs;

import java.io.*;
import java.util.*;

public class FastaDb implements ProteinLib {

	/** first_name -> protein mapping */	
	Map proteinFromFirstName = new HashMap();
	
	/**  protein -> first_name mapping */	
	Map firstNameFromProtein = new HashMap();
	
	/** first_name -> names mapping */	
	Map namesFromFirstName = new HashMap();

	/** name -> first_name mapping */	
	Map firstNameFromName = new HashMap();
	
	public FastaDb() {
	}
	
	public FastaDb(String fname) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fname)));
		String line;
		
		line = br.readLine();
		while (line != null) {
			// the line are the names of protein, delimited by "|"
			// skip the starting ">"
			String pnames = line.substring(1);
			
			
			line = br.readLine();
			if (line == null) line = "";
			StringBuffer proteinFragment = new StringBuffer(line);
			
			// read the protein
			line = br.readLine();
			while (line != null && !line.startsWith(">")) {
				proteinFragment.append(line);
				line = br.readLine();
			}
			String protein = proteinFragment.toString();
			
			// pnames = ">sp|P3|FF_FF Protein description"
			int space = pnames.indexOf(' ');
			if (space != -1) {
				// cut the description of the protein
				pnames = pnames.substring(0, space);
			}
			StringTokenizer st = new StringTokenizer(pnames, "|");
			String pname = st.nextToken(); // most likely "sp"
			if ("sp".equals(pname)) {
				pname = st.nextToken();
			}
			put(pname, protein);
			
			while (st.hasMoreTokens()) {
				String altpname = st.nextToken();
				put(altpname, protein);
			}
		}
	}
	
	public void put(String name, String protein) {
		String firstName = (String)firstNameFromProtein.get(protein);
		if (firstName == null) {
			proteinFromFirstName.put(name, protein);
			firstNameFromProtein.put(protein, name);
			namesFromFirstName.put(name, new Vector());
			firstName = name;
		} else if (firstName.equals(name)) {
			return;
		}
		
		// the entry already exists in database
		((Collection)(namesFromFirstName.get(firstName))).add(name);
		firstNameFromName.put(name, firstName);
	}
	
	public String get(String name) {
		return (String)proteinFromFirstName.get(firstNameFromName.get(name));
	}

	public Collection getAllFirstNames() {
		return proteinFromFirstName.keySet();
	}

	public Collection getAllNames() {
		return namesFromFirstName.keySet();
	}

	public Collection getAllNames(String name) {
		return (Collection)namesFromFirstName.get(firstNameFromName.get(name));
	}

	public Collection getAllProteins() {
		return proteinFromFirstName.values();
	}

	public String getFirstName(String protein) {
		return (String)firstNameFromProtein.get(protein);
	}

}
