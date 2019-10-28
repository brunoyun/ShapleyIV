package cisa.ed.ac.uk.mediation.base;

import java.util.LinkedHashSet;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

public class Context {
	private InMemoryAtomSet context;
	private Set<String> features = new LinkedHashSet<String>();
	Set<String> options;
	
	public Context(Set<String> options) {
		this.options = options;
		this.context = new LinkedListAtomSet();
	}
	
	public void addToContext(String s) {
		DlgpParser parser = new DlgpParser(s);
		while(parser.hasNext()) {
			Atom parsedAtom = (Atom) parser.next();
			
			//We check that it is a ground atom with only options
			boolean onlyOptions = true;
			for(Term t : parsedAtom.getTerms()) {
				if(!options.contains(t.getLabel()))
					onlyOptions = false;
			}
			
			if(onlyOptions) {
				String predicate_name = parsedAtom.getPredicate().getIdentifier().toString();
				if(!features.contains(predicate_name))
					features.add(predicate_name);
				
				if(!context.contains(parsedAtom))
					context.add(parsedAtom);
			}
			else
				System.err.println("Atom "+parsedAtom+" not added. It contains constants that are not options.");
			
		}
	}
	
	
	public AtomSet getContext() {
		return context;
	}

	public String toString() {
		return "Context:\n"+context.toString()+"\n";
	}
	
}
