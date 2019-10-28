package cisa.ed.ac.uk.mediation.base.reasoning;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

public class Closure {

	public static AtomSet getClosure(AtomSet s, RuleSet ruleset) throws ChaseException {
		InMemoryAtomSet sTemp = new LinkedListAtomSet();
		sTemp.addAll(s);
		
		Chase myChase = new ParticularChase(ruleset, sTemp);
		myChase.execute();
		
		return sTemp;
	}
}
