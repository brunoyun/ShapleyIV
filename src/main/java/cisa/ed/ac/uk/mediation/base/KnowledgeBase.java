package cisa.ed.ac.uk.mediation.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import cisa.ed.ac.uk.mediation.base.reasoning.Closure;
import cisa.ed.ac.uk.mediation.base.utilities.AtomSetUtilities;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.GIterator;

public class KnowledgeBase {

	private InMemoryAtomSet preference_atoms;
	private InMemoryAtomSet difference_atoms;
	private RuleSet restriction_rules;
	private RuleSet preference_rules;
	private RuleSet integrity_constraints;
	private String user;
	private Set<String> options;

	public Set<String> getOptions() {
		return options;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public AtomSet getPreference_atoms() {
		return preference_atoms;
	}

	public AtomSet getDifference_atoms() {
		return difference_atoms;
	}

	public InMemoryAtomSet getInMemoryAtomSetPreference_atoms() {
		return preference_atoms;
	}

	public InMemoryAtomSet getInMemoryAtomSetDifference_atoms() {
		return difference_atoms;
	}

	public RuleSet getRestriction_rules() {
		return restriction_rules;
	}

	public RuleSet getPreference_rules() {
		return preference_rules;
	}

	public RuleSet getIntegrity_constraints() {
		return integrity_constraints;
	}

	public KnowledgeBase(String user, Set<String> options) {
		this.user = user;

		this.options = options;
		difference_atoms = new LinkedListAtomSet();
		initializeDifferenceAtoms(options);

		preference_atoms = new LinkedListAtomSet();

		preference_rules = new LinkedListRuleSet();

		integrity_constraints = new LinkedListRuleSet();
		initializeIntegrityConstraints();

		restriction_rules = new LinkedListRuleSet();
		initializeRestrictionRules();
	}


	public KnowledgeBase(KnowledgeBase K) {
		this.user = K.getUser();

		this.options = new LinkedHashSet<String>();
		this.options.addAll(K.getOptions());

		difference_atoms = new LinkedListAtomSet();
		difference_atoms.addAll(K.getInMemoryAtomSetDifference_atoms());

		preference_atoms = new LinkedListAtomSet();
		preference_atoms.addAll(K.getInMemoryAtomSetPreference_atoms());

		preference_rules = new LinkedListRuleSet();
		preference_rules.addAll(K.getPreference_rules().iterator());

		integrity_constraints = new LinkedListRuleSet();
		integrity_constraints.addAll(K.getIntegrity_constraints().iterator());

		restriction_rules = new LinkedListRuleSet();
		restriction_rules.addAll(K.getRestriction_rules().iterator());
	}

	public void addPreferenceAtom(String a, String b, boolean strict) {
		String toBeParsed;
		if(options.contains(a) && options.contains(b))
		{
			if(!strict)
				toBeParsed = "pref("+user+","+a+","+b+").";
			else
				toBeParsed = "stpref("+user+","+a+","+b+").";

			Atom parsedAtom = DlgpParser.parseAtom(toBeParsed);
			if(! preference_atoms.contains(parsedAtom))
				preference_atoms.add(parsedAtom);
		}
		else
			System.err.println("Cannot add the preference atom, parameters are not options.");
	}

	public void addPreferenceRules(String s) {
		DlgpParser parser = new DlgpParser(s);
		while(parser.hasNext()) {
			Object o = parser.next();
			if(o instanceof Rule) {
				preference_rules.add((Rule) o);
			}
		}
	}

	public KnowledgeBase substract(Object o) throws AtomSetException {
		KnowledgeBase result = new KnowledgeBase(this);
		
		if(o instanceof Rule)
		{
			Rule ro = (Rule) o;
			result.getPreference_rules().remove(ro);
		}
		else if (o instanceof Atom)
		{
			Atom ro = (Atom) o;
			result.getPreference_atoms().remove(ro);
		}
		
		return result;
	}
	
	public void initializeDifferenceAtoms(Set<String> Options) {
		String KB = "";
		for(String s : Options)
			for(String s2 : Options)
			{
				if(! s.equals(s2))
					KB = KB.concat("neq("+s+","+s2+").");
			}

		DlgpParser parser = new DlgpParser(KB);
		while(parser.hasNext()) {
			difference_atoms.add((Atom) parser.next());
		}
	}

	private void initializeRestrictionRules() {
		String KB = "pref(U,X,Z) :- pref(U,X,Y), pref(U,Y,Z).\n" + 
				"eqpref(U,X,Y) :- eqpref(U,Y,X).\n" + 
				"eqpref(U,X,Y) :- pref(U,X,Y), pref(U,Y,X).\n" + 
				"pref(U,X,Y), pref(U,Y,X) :- eqpref(U,X,Y).\n" +
				"stpref(U,X,Z) :- stpref(U,X,Y), pref(U,Y,Z).\n" + 
				"stpref(U,X,Z) :- stpref(U,Y,Z), pref(U,X,Y).";

		DlgpParser parser = new DlgpParser(KB);
		while(parser.hasNext()) {
			restriction_rules.add((Rule) parser.next());
		}
	}

	private void initializeIntegrityConstraints() {
		String KB = "! :- stpref(U,X,X).\n" + 
				"! :- stpref(U,X,Y), eqpref(U,X,Y).\n" + 
				"! :- stpref(U,X,Y), stpref(U,Y,X).";

		DlgpParser parser = new DlgpParser(KB);
		while(parser.hasNext()) {
			integrity_constraints.add((Rule) parser.next());
		}
	}

	public AtomSet getSaturation(Context c) throws ChaseException{
		InMemoryAtomSet facts = new LinkedListAtomSet();
		facts.addAll(preference_atoms);
		facts.addAll(difference_atoms);
		facts.addAll(c.getContext());

		RuleSet ruleset = new LinkedListRuleSet();
		ruleset.addAll(restriction_rules.iterator());
		ruleset.addAll(preference_rules.iterator());

		return Closure.getClosure(facts, ruleset);
	}




	public String toString() {
		String result = "--- KB FOR "+user+" ---\nPreference atoms:\n";
		result = result.concat(preference_atoms.toString()+"\n");

		result = result.concat("Preference rules: \n");
		result = result.concat(preference_rules+"\n");

		//		result = result.concat("\nDifference atoms: \n");
		//		result = result.concat(difference_atoms.toString()+"\n");
		//		
		//		result = result.concat("\nRestriction rules: \n");
		//		for(Rule r : restriction_rules)
		//			result = result.concat(r.toString()+"\n");
		//		
		//		result = result.concat("\nIntegrity constraints: \n");
		//		for(Rule r : integrity_constraints)
		//			result = result.concat(r.toString()+"\n");

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((preference_atoms == null) ? 0 : AtomSetUtilities.hashcode(preference_atoms));
		result = prime * result + ((preference_rules == null) ? 0 : AtomSetUtilities.hashcode(preference_rules));
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		KnowledgeBase other = (KnowledgeBase) obj;
		
		if (options == null) {
			if (other.options != null)
				return false;
		} else if (! (options.containsAll(other.options) && other.options.containsAll(options)))
			return false;
		if (preference_atoms == null) {
			if (other.preference_atoms != null)
				return false;
		} else
			try {
				if (! (AtomSetUtilities.containsAll(preference_atoms,other.preference_atoms) && AtomSetUtilities.containsAll(other.preference_atoms,preference_atoms)))
					return false;
			} catch (AtomSetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (preference_rules == null) {
			if (other.preference_rules != null)
				return false;
		} else if (! (AtomSetUtilities.containsAll(preference_rules,other.preference_rules) && AtomSetUtilities.containsAll(other.preference_rules,preference_rules)))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}


	

	
	
}
