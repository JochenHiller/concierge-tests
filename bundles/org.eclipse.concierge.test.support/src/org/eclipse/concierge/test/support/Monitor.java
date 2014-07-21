package org.eclipse.concierge.test.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Monitor {

	public static int noOfCallsOfStart = 0;
	public static int noOfCallsOfStop = 0;
	public static int noOfCallsOfActivate = 0;
	public static int noOfCallsOfDeactivate = 0;
	public static SAXParserFactory saxParserFactory;
	public static SAXParser saxParser;

	public static Collection<String> callSequence = Collections
			.synchronizedList(new ArrayList<String>());

	public static void clean() {
		noOfCallsOfStart = 0;
		noOfCallsOfStop = 0;
		noOfCallsOfActivate = 0;
		noOfCallsOfDeactivate = 0;
		saxParserFactory = null;
		saxParser = null;
	}

	public static void addCall(String call) {
		callSequence.add(call);
		System.out.println("Monitor: called " + call);
	}
}
