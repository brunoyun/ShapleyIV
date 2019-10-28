package cisa.ed.ac.uk.mediation.inconsistency;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Iterators;

import cisa.ed.ac.uk.mediation.base.Context;
import cisa.ed.ac.uk.mediation.base.KnowledgeBase;
import cisa.ed.ac.uk.mediation.base.reasoning.Consistency;
import cisa.ed.ac.uk.mediation.base.utilities.AtomSetUtilities;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;

public class InconsistencyValue {

	private KnowledgeBase KB;
	private Context c;
	private HashMap<KnowledgeBase,Double> IMResults;
	private String initialised;

	public InconsistencyValue(KnowledgeBase kB, Context c) {
		this.c =c ;
		IMResults = new HashMap<KnowledgeBase, Double>();
		KB = kB;
		initialised = "no";
	}

	private void drasticInitialise() throws AtomSetException, ChaseException, HomomorphismException {
		IMResults = new HashMap<KnowledgeBase, Double>();
		for(KnowledgeBase K : AtomSetUtilities.allConsistentSubset(KB,c))
			IMResults.put(K, 0.0); //All those that are not here are inconsistent
	}

	public double drasticIV(Object o) throws AtomSetException, ChaseException, HomomorphismException {
		if(!initialised.equals("drastic"))
		{
			drasticInitialise();
			initialised = "drastic";
		}

		double result = 0;
		int KBsize = Iterators.size(KB.getPreference_atoms().iterator()) + Iterators.size(KB.getPreference_rules().iterator());
		ArrayList<KnowledgeBase> allsubsets = AtomSetUtilities.allSubset(KB);

		if(o instanceof Rule) {

			Rule prefrule = (Rule) o;

			for(KnowledgeBase K : allsubsets)
			{
				//We get the knowledge base the inconsistent knowledge base that contain o
				if(K.getPreference_rules().contains(prefrule) && (IMResults.get(K) == null)) { 
					//We create the knowledge base without o

					KnowledgeBase KBoRemoved = K.substract(o);
					if(Consistency.IsConsistent(KBoRemoved, c))
					{
						int KSize = Iterators.size(K.getPreference_atoms().iterator()) + Iterators.size(K.getPreference_rules().iterator());
						result+= Factorial(KBsize - KSize).divide(MultBetween(KSize, KBsize),2, RoundingMode.HALF_UP).doubleValue();
					}

				}
			}
		}

		if(o instanceof Atom) {

			Atom prefatom = (Atom) o;

			for(KnowledgeBase K : allsubsets)
			{
				//We get the knowledge base the inconsistent knowledge base that contain o
				if(K.getPreference_atoms().contains(prefatom) && (IMResults.get(K) == null)) { 
					//We create the knowledge base without o

					KnowledgeBase KBoRemoved = K.substract(o);
					if(Consistency.IsConsistent(KBoRemoved, c))
					{
						int KSize = Iterators.size(K.getPreference_atoms().iterator()) + Iterators.size(K.getPreference_rules().iterator());
						result+= Factorial(KBsize - KSize).divide(MultBetween(KSize, KBsize),2, RoundingMode.HALF_UP).doubleValue();
					}

				}
			}
		}


		return result;
	}






	private void MIInitialise() throws AtomSetException, ChaseException, HomomorphismException {
		IMResults = new HashMap<KnowledgeBase, Double>();
		for(KnowledgeBase K : AtomSetUtilities.allMISubset(KB, c))
			IMResults.put(K, 0.0); //All those are MI

	}





	public double MIIV(Object o) throws AtomSetException, ChaseException, HomomorphismException {
		if(!initialised.equals("MI"))
		{
			MIInitialise();
			initialised = "MI";
		}

		double result = 0;

		if(o instanceof Rule) {

			Rule prefrule = (Rule) o;
			for(KnowledgeBase K : IMResults.keySet()) {
				if(K.getPreference_rules().contains(prefrule)) {
					int KSize = Iterators.size(K.getPreference_atoms().iterator()) + Iterators.size(K.getPreference_rules().iterator());
					result += 1/ ((double) KSize);
				}
			}

		}

		if(o instanceof Atom) {

			Atom prefatom = (Atom) o;
			for(KnowledgeBase K : IMResults.keySet()) {
				if(K.getPreference_atoms().contains(prefatom)) {

					int KSize = Iterators.size(K.getPreference_atoms().iterator()) + Iterators.size(K.getPreference_rules().iterator());
					result += 1/ ((double) KSize);
				}
			}

		}


		return result;
	}



	private void MPInitialise() throws AtomSetException, ChaseException, HomomorphismException {
		IMResults = new HashMap<KnowledgeBase, Double>();
		for(KnowledgeBase K : AtomSetUtilities.allSubset(KB)) {
			IMResults.put(K, (double)InconsistencyMeasure.MPIM(K, c)); //All those are MI
		}

	}



	public double MPIV(Object o) throws AtomSetException, ChaseException, HomomorphismException {
		if(!initialised.equals("MP"))
		{
			MPInitialise();
			initialised = "MP";
		}

		double result = 0;
		ArrayList<KnowledgeBase> allsubsets = AtomSetUtilities.allSubset(KB);
		int KBsize = Iterators.size(KB.getPreference_atoms().iterator()) + Iterators.size(KB.getPreference_rules().iterator());

		if(o instanceof Rule) {

			Rule prefrule = (Rule) o;

			for(KnowledgeBase K : allsubsets)
			{
				//We get inconsistent knowledge base that contain o
				if(K.getPreference_rules().contains(prefrule) && (IMResults.get(K) > 0)) { 
					//We create the knowledge base without o

					KnowledgeBase KBoRemoved = K.substract(o);
					double difference = IMResults.get(K) - IMResults.get(KBoRemoved);
					int KSize = Iterators.size(K.getPreference_atoms().iterator()) + Iterators.size(K.getPreference_rules().iterator());
					result+= Factorial(KBsize - KSize).divide(MultBetween(KSize, KBsize),2, RoundingMode.HALF_UP).doubleValue() * difference;
				}
			}

		}

		if(o instanceof Atom) {

			Atom prefatom = (Atom) o;
			for(KnowledgeBase K : allsubsets)
			{
				//We get inconsistent knowledge base that contain o
				if(K.getPreference_atoms().contains(prefatom) && (IMResults.get(K) > 0)) { 
					//We create the knowledge base without o

					KnowledgeBase KBoRemoved = K.substract(o);
					double difference = IMResults.get(K) - IMResults.get(KBoRemoved);
					int KSize = Iterators.size(K.getPreference_atoms().iterator()) + Iterators.size(K.getPreference_rules().iterator());
					result+= Factorial(KBsize - KSize).divide(MultBetween(KSize, KBsize),2, RoundingMode.HALF_UP).doubleValue() * difference;
				}
			}

		}


		return result;
	}













	private BigDecimal Factorial(int n) {
		BigDecimal result = BigDecimal.ONE;
		for (int i = 2; i <= n; i++)
			result = result.multiply(BigDecimal.valueOf(i));
		return result;
	}

	private BigDecimal MultBetween(long a, long b) {
		BigDecimal result = BigDecimal.ONE;
		for(long i = a ; i<= b ; i++) {
			result = result.multiply(BigDecimal.valueOf(i));
		}
		return result;
	}



}
