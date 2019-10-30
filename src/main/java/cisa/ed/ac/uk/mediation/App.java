package cisa.ed.ac.uk.mediation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import cisa.ed.ac.uk.mediation.base.Context;
import cisa.ed.ac.uk.mediation.base.KnowledgeBase;
import cisa.ed.ac.uk.mediation.base.reasoning.Consistency;
import cisa.ed.ac.uk.mediation.base.utilities.AtomSetUtilities;
import cisa.ed.ac.uk.mediation.base.utilities.KnowledgeBaseSetUtilities;
import cisa.ed.ac.uk.mediation.inconsistency.InconsistencyMeasure;
import cisa.ed.ac.uk.mediation.inconsistency.InconsistencyValue;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

/**
 * Hello world!
 *
 */
public class App 
{
	private static DecimalFormat df2 = new DecimalFormat("#.##");


	public static void main( String[] args ) throws ChaseException, HomomorphismException, AtomSetException, IOException
	{
		if(args.length != 3) {
			System.err.println("Please enter the correct number of arguments.");
			
			argumentsDisplay();
			
		}
		else {
			//Initialising the tool
			Set<String> options = new LinkedHashSet<String>();
			df2.setRoundingMode(RoundingMode.UP);
			BufferedReader br = Files.newBufferedReader(Paths.get(args[0]));
			String line;
			HashMap<String,KnowledgeBase> UserKBs = new HashMap<String, KnowledgeBase>();

			//Reading the options
			while ((line = br.readLine()) != null) {
				options.add(line);
			}

			//Reading the context
			Context C = new Context(options);
			br = Files.newBufferedReader(Paths.get(args[1]));

			C.addToContext(readDocument(br));

			//TODO CREATE THE KNOWLEDGE BASE FOR EACH USER WHEN READING THE PREFERENCES


			//We read the preference file
			br = Files.newBufferedReader(Paths.get(args[2]));
			DlgpParser parser = new DlgpParser(readDocument(br));
			while(parser.hasNext()) {
				Object o = parser.next();

				if(o instanceof Atom) //If it is an atom
				{
					Atom parsedAtom = (Atom) o;
					String PApredicate = parsedAtom.getPredicate().getIdentifier().toString();
					//We verify that it starts with the correct predicate
					if( PApredicate.equals("pref") || PApredicate.equals("eqpref") || PApredicate.equals("stpref")) {  
						//We select the user
						String userParsed = parsedAtom.getTerm(0).toString();

						//We create the user knowledge base if it was not known before
						if(UserKBs.get(userParsed) == null)
							UserKBs.put(userParsed, new KnowledgeBase(userParsed, options));

						UserKBs.get(userParsed).addPreferenceAtom(parsedAtom);
					}
					else
						System.err.println("You have given a preference atom that does not have pref or eqpref as a predicate.");
				}
				else if (o instanceof Rule){ //If it is a rule
					Rule parsedRule = (Rule) o;
					AtomSet head = parsedRule.getHead();
					boolean onlyOneUser =true, onlyPreferenceAtoms= true;
					String userParsed = head.iterator().next().getTerm(0).toString();

					for(Atom a : head) //We verify that it only concerns one user and that it has only preference atoms in the head
					{
						String PApredicate = a.getPredicate().getIdentifier().toString();
						if(!(PApredicate.equals("pref") || PApredicate.equals("eqpref") || PApredicate.equals("stpref"))) {
							System.err.println("You have given a preference rule that contains a non preference atom in the head.");
							onlyPreferenceAtoms = false;
						}

						if(! (a.getTerm(0).toString().equals(userParsed))) {
							System.err.println("You have given a preference rule that contains multiple users in the head.");
							onlyOneUser= false;
						}
					}

					if(onlyOneUser && onlyPreferenceAtoms) {
						if(UserKBs.get(userParsed) == null)
							UserKBs.put(userParsed, new KnowledgeBase(userParsed, options));

						UserKBs.get(userParsed).addPreferenceRules(parsedRule);
					}
				}
			}

			System.out.println(C);


			for(String user : UserKBs.keySet())
			{
				KnowledgeBase KB = UserKBs.get(user);

				System.out.println("USER: "+KB.getUser());
				System.out.println(KB);

				//System.out.println("Saturation: "+KB.getSaturation(C));

				System.out.println("CONSISTENCY: "+ (Consistency.IsConsistent(KB, C)? "YES" : "NO"));

				//System.out.println("There are "+KnowledgeBaseSetUtilities.getRepairs(KB, C).size()+" repairs.");
				//System.out.println("------------------\n "+AtomSetUtilities.allMISubset(KB, C)+"------------------\n");
				InconsistencyValue IV = new InconsistencyValue(KB, C);


				System.out.println("\nDRASTIC IV:");
				for(Atom a : KB.getPreference_atoms())
					System.out.println(a+" "+df2.format(IV.drasticIV(a)));
				for(Rule a : KB.getPreference_rules())
					System.out.println(a+" "+df2.format(IV.drasticIV(a)));

				System.out.println("\nMI IV:");
				for(Atom a : KB.getPreference_atoms())
					System.out.println(a+" "+df2.format(IV.MIIV(a)));
				for(Rule a : KB.getPreference_rules())
					System.out.println(a+" "+df2.format(IV.MIIV(a)));

				System.out.println("\nMP IV:");
				for(Atom a : KB.getPreference_atoms())
					System.out.println(a+" "+df2.format(IV.MPIV(a)));
				for(Rule a : KB.getPreference_rules())
					System.out.println(a+" "+df2.format(IV.MPIV(a)));
			}





		}







	}


	private static String readDocument(BufferedReader br) throws IOException {
		String line, contextString="";
		while ((line = br.readLine()) != null) {
			contextString= contextString.concat(line+"\n");
		}
		return contextString;
	}

	private static void argumentsDisplay() {
		System.out.println("\n\nThe arguments should be [Option file] [Context file] [Preference file] where:\n\n"
				+ "- [Option file] is the file containing the options. The options should be given one per line and should follow the constant notation of the DLGP format.\n"
				+ "- [Context file] is the file containing the atoms describing the characteristics of the options. They should be ground atoms using only options and follow the fact notation of the DLGP format.\n"
				+ "- [Preference file] is the file containing the preference rules/atoms given by the users.\n");
	}

}
