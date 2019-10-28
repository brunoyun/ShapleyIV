package cisa.ed.ac.uk.mediation.base.utilities;

import java.util.ArrayList;

import cisa.ed.ac.uk.mediation.base.Context;
import cisa.ed.ac.uk.mediation.base.KnowledgeBase;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;

public class KnowledgeBaseSetUtilities {

	public static ArrayList<KnowledgeBase> getRepairs(KnowledgeBase KB, Context C) throws AtomSetException, ChaseException, HomomorphismException{
		ArrayList<KnowledgeBase> ConsistentSubsets = AtomSetUtilities.allConsistentSubset(KB, C);
		ArrayList<KnowledgeBase> reversedList = new ArrayList<KnowledgeBase>();
		for(int i= ConsistentSubsets.size()-1; i>0; i--)
			reversedList.add(ConsistentSubsets.get(i));
		
		for(int i=reversedList.size()-1; i>0; i--) {
			boolean isRepair = true;
			for(int j= 0; j<i; j++)
			{
				if(IsIncluded(reversedList.get(i), reversedList.get(j)))
					isRepair = false;
			}
			if(!isRepair)
				reversedList.remove(i);
		}
		
		return reversedList;
		
	}
	
	public static boolean IsIncluded(KnowledgeBase A, KnowledgeBase B) throws AtomSetException {
		
		boolean result = true;
		
		if(!(A.getUser().equals(B.getUser()) && A.getOptions().equals(B.getOptions()))) {
			result = false;
		}
		
		
		
		for(Atom a : A.getPreference_atoms())
			if(! B.getPreference_atoms().contains(a))
				result = false;
		
		for(Rule a : A.getPreference_rules())
			if(! B.getPreference_rules().contains(a))
				result = false;
		
		
		return result;
	}
}
