package cisa.ed.ac.uk.mediation.base.reasoning;

import cisa.ed.ac.uk.mediation.base.Bottom;
import cisa.ed.ac.uk.mediation.base.Context;
import cisa.ed.ac.uk.mediation.base.KnowledgeBase;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;

public class Consistency {
	
	public static boolean IsConsistent(KnowledgeBase KB, Context c) throws ChaseException, HomomorphismException {
		
		InMemoryAtomSet sTemp = new LinkedListAtomSet();
		
		sTemp.addAll(KB.getPreference_atoms());
		sTemp.addAll(KB.getDifference_atoms());
		sTemp.addAll(c.getContext());
		
		RuleSet ruleset = new LinkedListRuleSet();
		ruleset.addAll(KB.getRestriction_rules().iterator());
		ruleset.addAll(KB.getPreference_rules().iterator());
		ruleset.addAll(KB.getIntegrity_constraints().iterator());
		
		Chase myChase = new ParticularChase(ruleset, sTemp);
		myChase.execute();
		
		CloseableIterator<Substitution> results = StaticHomomorphism.instance().execute(Bottom.BottomConjunctiveQuery(), sTemp);
		if(! results.hasNext())
			return true;
		else
			return false;
	}

}
