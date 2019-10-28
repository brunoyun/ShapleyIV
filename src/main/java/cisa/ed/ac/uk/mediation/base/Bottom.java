package cisa.ed.ac.uk.mediation.base;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.util.stream.GIterator;

public class Bottom {

	public static ConjunctiveQuery BottomConjunctiveQuery(){
		ConjunctiveQuery bottomQuery = null;
		InMemoryAtomSet bottomAtomSet = new LinkedListAtomSet();
		bottomAtomSet.add(DefaultAtomFactory.instance().getBottom());
		return new DefaultConjunctiveQuery(bottomAtomSet, Collections.<Term>emptyList());
	}
}
