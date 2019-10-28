package cisa.ed.ac.uk.mediation;

import java.util.ArrayList;
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
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;



/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws ChaseException, HomomorphismException, AtomSetException
    {
    	
    	
    	Set<String> options = new LinkedHashSet<String>();
    	options.add("a1");
    	options.add("a2");
    	options.add("a3");
    	options.add("a4");
    	
    	Context C = new Context(options);
    	C.addToContext("mp(a1).\n" + 
    			"mp(a2).\n" + 
    			"ar(a2).\n" + 
    			"ar(a3).\n" + 
    			"t(a3).\n"
    			+ "q(a4).");
    	
    	KnowledgeBase KB = new KnowledgeBase("u1",options);
    	KB.addPreferenceAtom("a1", "a2", false);
    	KB.addPreferenceAtom("a4", "a3", false);
    	KB.addPreferenceRules("stpref(u1,X,Y) :- neq(X,Y), t(X).\n" + 
    			"stpref(u1,X,Y) :- ar(X), mp(Y), neq(X,Y)."
    			+ "stpref(u1,X,Y) :- q(X), t(Y), neq(X,Y)."); //
    	
    	
    	System.out.println(C);
    	System.out.println(KB);
    	
    	System.out.println("Saturation: "+KB.getSaturation(C));
    	
    	System.out.println("CONSISTENCY: "+ (Consistency.IsConsistent(KB, C)? "YES" : "NO"));
    	
    	
    	
    	System.out.println("There are "+KnowledgeBaseSetUtilities.getRepairs(KB, C).size()+" repairs.");
//    	System.out.println("------------------\n "+AtomSetUtilities.allMISubset(KB, C)+"------------------\n");
    	InconsistencyValue IV = new InconsistencyValue(KB, C);
    	
    	
    	System.out.println("\nDRASTIC IV:");
    	for(Atom a : KB.getPreference_atoms())
    		System.out.println(a+" "+IV.drasticIV(a));
    	for(Rule a : KB.getPreference_rules())
    		System.out.println(a+" "+IV.drasticIV(a));
    	
    	System.out.println("\nMI IV:");
    	for(Atom a : KB.getPreference_atoms())
    		System.out.println(a+" "+IV.MIIV(a));
    	for(Rule a : KB.getPreference_rules())
    		System.out.println(a+" "+IV.MIIV(a));
    	
    	System.out.println("\nMP IV:");
    	for(Atom a : KB.getPreference_atoms())
    		System.out.println(a+" "+IV.MPIV(a));
    	for(Rule a : KB.getPreference_rules())
    		System.out.println(a+" "+IV.MPIV(a));
    }
}
