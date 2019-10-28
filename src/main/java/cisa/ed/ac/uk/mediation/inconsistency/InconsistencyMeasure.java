package cisa.ed.ac.uk.mediation.inconsistency;

import cisa.ed.ac.uk.mediation.base.Bottom;
import cisa.ed.ac.uk.mediation.base.Context;
import cisa.ed.ac.uk.mediation.base.KnowledgeBase;
import cisa.ed.ac.uk.mediation.base.reasoning.Consistency;
import cisa.ed.ac.uk.mediation.base.utilities.AtomSetUtilities;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;

public class InconsistencyMeasure {

	public static int drasticIM(KnowledgeBase KB, Context c) throws ChaseException, HomomorphismException {
		if(Consistency.IsConsistent(KB, c))
			return 0;
		else
			return 1;
	}
	
	public static int MIIM(KnowledgeBase KB, Context c) throws AtomSetException, ChaseException, HomomorphismException {
		return AtomSetUtilities.allMISubset(KB, c).size();
	}
	
	public static int MPIM(KnowledgeBase KB, Context c) throws ChaseException, HomomorphismException {
		int result =0;
		AtomSet saturation =KB.getSaturation(c);
		
		for(Rule r : KB.getIntegrity_constraints())
		{
			ConjunctiveQuery CQ = ConjunctiveQueryFactory.instance().create(r.getBody());
			CloseableIterator<Substitution> results = StaticHomomorphism.instance().execute(CQ, saturation);
			
			//System.out.println(r);
			while(results.hasNext()) {
				result++;
				results.next();
			}
		}
		
		return result;
		
	}
	
}
