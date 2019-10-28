package cisa.ed.ac.uk.mediation.base.utilities;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cisa.ed.ac.uk.mediation.base.Context;
import cisa.ed.ac.uk.mediation.base.KnowledgeBase;
import cisa.ed.ac.uk.mediation.base.reasoning.Consistency;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;

public class AtomSetUtilities {
	
	public static ArrayList<KnowledgeBase> allConsistentSubset(KnowledgeBase KB, Context c) throws AtomSetException, ChaseException, HomomorphismException {
		ArrayList<KnowledgeBase> S = new ArrayList<KnowledgeBase>();
    	S.add(new KnowledgeBase(KB.getUser(), KB.getOptions()));
    	privateAllConsistentSubset(S, KB,c);
    	return S;
	}
	
	public static ArrayList<KnowledgeBase> allMISubset(KnowledgeBase KB, Context c) throws AtomSetException, ChaseException, HomomorphismException {
		ArrayList<KnowledgeBase> S = new ArrayList<KnowledgeBase>();
    	S.add(new KnowledgeBase(KB.getUser(), KB.getOptions()));
    	privateAllFirstInconsistentSubset(S, KB,c);
    	
    	for(int i=S.size()-1; i>0; i--) {
    		boolean isMI = true;
    		if(Consistency.IsConsistent(S.get(i), c))
    			isMI = false;
    		else
    			for(int j=0; j <i ; j++) {
    				if((!Consistency.IsConsistent(S.get(j), c)) && KnowledgeBaseSetUtilities.IsIncluded(S.get(j), S.get(i)))
    					isMI = false;
    			}
    		if(!isMI)
    			S.remove(i);
    	}
    	
    	//We remove the empty KB
    	S.remove(0);
    	
    	return S;
	}
	
	
	public static ArrayList<KnowledgeBase> allSubset(KnowledgeBase KB) throws AtomSetException{
		ArrayList<KnowledgeBase> S = new ArrayList<KnowledgeBase>();
    	S.add(new KnowledgeBase(KB.getUser(), KB.getOptions()));
    	privateAllSubsets(S, KB);
    	return S;
	}
	
	private static void privateAllSubsets(ArrayList<KnowledgeBase> S, KnowledgeBase KB) throws AtomSetException {
		if(KB.getPreference_atoms().iterator().hasNext())
		{
			
			Iterator<Atom> iterat = KB.getPreference_atoms().iterator();
			Atom a  = iterat.next();
			
			//I have to copy the set S.
			ArrayList<KnowledgeBase> Temp = new ArrayList<KnowledgeBase>();
			for(KnowledgeBase K : S)
			{
				KnowledgeBase sTemp = new KnowledgeBase(K);
				sTemp.getPreference_atoms().add(a);
				Temp.add(sTemp);
				
			}

			//We add the new one
			for(KnowledgeBase s : Temp)
			{
				S.add(s);
			}
			
			KnowledgeBase newKB = new KnowledgeBase(KB.getUser(), KB.getOptions());
			while(iterat.hasNext())
				newKB.getPreference_atoms().add(iterat.next());
			
			privateAllSubsets(S, newKB);
		}
		
		if(KB.getPreference_rules().iterator().hasNext())
		{
			
			Iterator<Rule> iterat = KB.getPreference_rules().iterator();
			Rule a  = iterat.next();
			
			//I have to copy the set S.
			ArrayList<KnowledgeBase> Temp = new ArrayList<KnowledgeBase>();
			for(KnowledgeBase K : S)
			{
				KnowledgeBase sTemp = new KnowledgeBase(K);
				sTemp.getPreference_rules().add(a);
				Temp.add(sTemp);
				
			}

			//We add the new one
			for(KnowledgeBase s : Temp)
			{
				S.add(s);
			}
			
			KnowledgeBase newKB = new KnowledgeBase(KB.getUser(), KB.getOptions());
			while(iterat.hasNext())
				newKB.getPreference_rules().add(iterat.next());
			
			privateAllSubsets(S, newKB);
		}
	}


	private static void privateAllConsistentSubset(ArrayList<KnowledgeBase> S, KnowledgeBase KB, Context c) throws AtomSetException, ChaseException, HomomorphismException {

		
		if(KB.getPreference_atoms().iterator().hasNext())
		{
			
			Iterator<Atom> iterat = KB.getPreference_atoms().iterator();
			Atom a  = iterat.next();
			
			//I have to copy the set S.
			ArrayList<KnowledgeBase> Temp = new ArrayList<KnowledgeBase>();
			for(KnowledgeBase K : S)
			{
				KnowledgeBase sTemp = new KnowledgeBase(K);
				sTemp.getPreference_atoms().add(a);

				if(Consistency.IsConsistent(sTemp, c)) {
					//In this case, it is  consistent
					Temp.add(sTemp);
				}
			}

			//We add the new one
			for(KnowledgeBase s : Temp)
			{
				S.add(s);
			}
			
			KnowledgeBase newKB = new KnowledgeBase(KB.getUser(), KB.getOptions());
			while(iterat.hasNext())
				newKB.getPreference_atoms().add(iterat.next());
			
			privateAllConsistentSubset(S, newKB, c);
		}
		
		if(KB.getPreference_rules().iterator().hasNext())
		{
			
			Iterator<Rule> iterat = KB.getPreference_rules().iterator();
			Rule a  = iterat.next();
			
			//I have to copy the set S.
			ArrayList<KnowledgeBase> Temp = new ArrayList<KnowledgeBase>();
			for(KnowledgeBase K : S)
			{
				KnowledgeBase sTemp = new KnowledgeBase(K);
				sTemp.getPreference_rules().add(a);

				if(Consistency.IsConsistent(sTemp, c)) {
					//In this case, it is  consistent
					Temp.add(sTemp);
				}
			}

			//We add the new one
			for(KnowledgeBase s : Temp)
			{
				S.add(s);
			}
			
			KnowledgeBase newKB = new KnowledgeBase(KB.getUser(), KB.getOptions());
			while(iterat.hasNext())
				newKB.getPreference_rules().add(iterat.next());
			
			privateAllConsistentSubset(S, newKB, c);
		}
		
	}
	
private static void privateAllFirstInconsistentSubset(ArrayList<KnowledgeBase> S, KnowledgeBase KB, Context c) throws AtomSetException, ChaseException, HomomorphismException {

		
		if(KB.getPreference_atoms().iterator().hasNext())
		{
			
			Iterator<Atom> iterat = KB.getPreference_atoms().iterator();
			Atom a  = iterat.next();
			
			//I have to copy the set S.
			ArrayList<KnowledgeBase> Temp = new ArrayList<KnowledgeBase>();
			for(KnowledgeBase K : S)
			{
				KnowledgeBase sTemp = new KnowledgeBase(K);
				

				if(Consistency.IsConsistent(sTemp, c)) {
					//In this case, it is  consistent
					sTemp.getPreference_atoms().add(a);
					Temp.add(sTemp);
				}
			}

			//We add the new one
			for(KnowledgeBase s : Temp)
			{
				S.add(s);
			}
			
			KnowledgeBase newKB = new KnowledgeBase(KB.getUser(), KB.getOptions());
			while(iterat.hasNext())
				newKB.getPreference_atoms().add(iterat.next());
			
			privateAllFirstInconsistentSubset(S, newKB, c);
		}
		
		if(KB.getPreference_rules().iterator().hasNext())
		{
			
			Iterator<Rule> iterat = KB.getPreference_rules().iterator();
			Rule a  = iterat.next();
			
			//I have to copy the set S.
			ArrayList<KnowledgeBase> Temp = new ArrayList<KnowledgeBase>();
			for(KnowledgeBase K : S)
			{
				KnowledgeBase sTemp = new KnowledgeBase(K);
				

				if(Consistency.IsConsistent(sTemp, c)) {
					//In this case, it is  consistent
					sTemp.getPreference_rules().add(a);
					Temp.add(sTemp);
				}
			}

			//We add the new one
			for(KnowledgeBase s : Temp)
			{
				S.add(s);
			}
			
			KnowledgeBase newKB = new KnowledgeBase(KB.getUser(), KB.getOptions());
			while(iterat.hasNext())
				newKB.getPreference_rules().add(iterat.next());
			
			privateAllFirstInconsistentSubset(S, newKB, c);
		}
		
		
		
	}
	
	public static boolean containsAll(AtomSet A, AtomSet B) throws AtomSetException {
		boolean result = true;
		for(Atom b : B)
			if(! A.contains(b))
				result = false;
		return result;
	}
	
	public static boolean containsAll(RuleSet A, RuleSet B)  {
		boolean result = true;
		for(Rule b : B)
			if(! A.contains(b))
				result = false;
		return result;
	}
	
	public static int hashcode(AtomSet A) {
		int hashCode = 1;
	    for (Atom e : A)
	        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
	    return hashCode;
	}
	
	public static int hashcode(RuleSet A) {
		int hashCode = 1;
	    for (Rule e : A)
	        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
	    return hashCode;
	}
	
}
