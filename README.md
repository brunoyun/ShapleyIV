# Acknowledgements
==================

This tool is inspired from previous works in the literature:

    * On the Measure of Conflicts: Shapley Inconsistency Values. Hunter and Konieczny (2010).
    * Inconsistency Measures for Repair Semantics in OBDA. Yun et al (2018).
    * DLGP: An extended Datalog Syntax for Existential Rules and Datalog±. GraphIK Team∗, LIRMM (2017)
    * Graal: A Toolkit for Query Answering with Existential Rules. Baget, M. Leclère, M.-L. Mugnier, S. Rocher, and C. Sipieter (2015)

# Description
==================

This tool is designed for decision-making using three types of inconsistency measures (and values) from an inconsistent knowledge base.

The input of the tool are:

    * A set of options in debate
    * A context that describes the characteristics of the options
    * A knowledge base for each user composed of preference statements and rules.
    
The output of the tool is:

    * A consistent partial preference for each user that is computing using a Shapley inconsistency value.
    
# Usage
==================

The tool should be launched using the following command: `java -jar preference_resolution.jar [Option file] [Context file] [Preference file]` where:

    * [Option file] is the file containing the options. The options should be given one per line and should follow the constant notation of the DLGP format.
    * [Context file] is the file containing the atoms describing the characteristics of the options. They should be ground atoms using only options and follow the fact notation of the DLGP format.
    * [Preference file] is the file containing the preference rules/atoms given by the users.

Example:
  
    **java -jar preference_resolution.jar Examples/AlgorithmSelection/options.txt Examples/AlgorithmSelection/context.dlgp Examples/AlgorithmSelection/preferences.dlgp**

Content
==================

This repository contains the source-code needed to compile a jar application.


Contacts
==================

In order to contact me, send me an email at: yun@lirmm.fr
