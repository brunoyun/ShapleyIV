package cisa.ed.ac.uk.mediation.base.reasoning;

import cisa.ed.ac.uk.mediation.base.Bottom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.forward_chaining.DefaultChase;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;

public class ParticularChase extends DefaultChase{
	private AtomSet atomset;
	
	public ParticularChase(Iterable<Rule> rules, AtomSet atomset) {
		super(rules, atomset, new DefaultRuleApplier<AtomSet>());
		this.atomset = atomset;
	}
	
	public boolean hasNext() {
		boolean b = super.hasNext();
		if(b) {
			CloseableIterator<Substitution> results;
			try {
				results = StaticHomomorphism.instance().execute(Bottom.BottomConjunctiveQuery(), atomset);
				return !results.hasNext();
			}catch (HomomorphismException e) {
				throw new Error(e);
			}
		}
		return b;
	}
}
