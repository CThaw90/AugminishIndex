package com.augminish.app.crawl;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

public class CrawlerTest {

	private static final String html = "<html><head><title>This is a title</title></head><body></body></html>";
	
	Document document = null;
	
	public CrawlerTest() {
		try {
			document = Jsoup.connect("http://localhost/indexing/tests/music.html").get();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void HtmlParserTest() {
		
		Document d = Jsoup.parse(html);
		
		Element html = d.child(0);
		Assert.assertEquals("Root Document node should and Html tag", "html", html.tagName());
		
		Element head = html.child(0);
		Assert.assertEquals("Child element at position 0 of Html tag node should be Head", "head", head.tagName());
		
		Element title = head.child(0);
		Assert.assertEquals("Child element at position 0 of Head node should be title", "title", title.tagName());
		Assert.assertEquals("The content of the title tag should match", "This is a title", title.text());
		
		Element body = html.child(1);
		Assert.assertEquals("Child element at position 1 of Html node should be body", "body", body.tagName());
	}
	
	@Test
	public void anchorTagAndHyperReferenceTest() {
		
		String[] values = {
			"/lessons", 	"/exercises", 		"/tools", 			"/", 
			"/products", 	"/news", 			"/contact", 		"/products",
			"/buy/lessons", "/products/lessons", "/lessons/10",		"/lessons/11",	
			"/lessons/12",	"/lessons/13",		"/lessons/14",		"/lessons/20",	
			"/lessons/15",	"/lessons/16",		"/lessons/21",		"/lessons/22",	
			"/lessons/23",	"/lessons/24",		"/lessons/25",		"/lessons/30",	
			"/lessons/31",	"/lessons/32",		"/lessons/33",		"/lessons/40",	
			"/lessons/42",	"/lessons/45",		"/lessons/48",		"/lessons/47",	
			"/lessons/43",	"/lessons/44",		"/lessons/46",		"/lessons/49",	
			"/lessons/50",	"/lessons/51",		"/lessons/52",		"/lessons/53",	
			"/lessons/55",	"/lessons/56",		"/lessons/57",		"/lessons/60",	
			"/lessons/61",	"/lessons/62",		"/lessons/120",		"/lessons/121",	
			"/lessons/122",	"/faq",				"/legal",			"/privacy",		
			"http://musictheorynet.tumblr.com", "http://facebook.com/musictheory.net",
			"https://twitter.com/intent/user?screen_name=musictheorynet"
		};
		
		Element music_html = document.child(0);
		int value = 0;
		
		for (Element attribute : music_html.select("a")) {
			Assert.assertEquals("Attribute value should be "+values[value], values[value], attribute.attr("href"));
			value++;
		}
	}
	
	@Test
	public void metaTagAndAttributesTest() {
		
		int a = 0, v = 1, avi = 0, tags = 0;
		String[] av = {
			"charset:utf-8",			"name:viewport",			"content:width=540",
			"property:og:site_name",	"content:musictheory.net",	"property:og:type",
			"content:website",			"property:og:url",			"content:http://www.musictheory.net/",
			"property:og:image",		
			"content:http://www.musictheory.net/vc/4/0/718b0256f94435645de3ab98cacff4575ce7ea88/logo.png"
		};
		Elements metaTags = document.select("meta");
		
		for (Element meta : metaTags) {
			
			for (Attribute attribute : meta.attributes()) {
				String[] attr = av[avi++].split(":", 2);
				Assert.assertEquals("Attribute name should be '"+ attribute.getKey() +"'", attr[a], attribute.getKey());
				Assert.assertEquals("Attribute value should be '"+ attribute.getValue()+"'", attr[v], attribute.getValue());
			}
			tags++;
		}
		
		Assert.assertEquals("There should be six meta tags in the head node", 6, tags);
	}
	
	@Test
	public void extractAllTextTest() {
		
		String[] texts = {
			"musictheory.net - Lessons", "Our lessons are provided online for free. If they help you, please to support the site.",
			"purchase our apps", "Purchase", "Theory Lessons", "On-the-go mobile access to the lessons, for your iPhone, iPad, and iPod touch.",
			"Learn&nbsp;more", "THE BASICS", "The Staff, Clefs, and Ledger Lines", "Learn about the staff, treble and bass clefs, and ledger lines.",
			"Note Duration", "Learn about five types of notes and how flags affect note duration.", "Measures and Time Signature",
			"Learn about measures and how many notes each can contain.", "Rest Duration", "Learn about the different types of rest.",
			"Dots and Ties", "Learn how dots and ties modify the duration of notes.", "Steps and Accidentals", "Learn about half steps, whole steps, and  accidentals.",
			"the different types of", "RHYTHM AND METER", "Simple and Compound Meter", "Learn how basic time signatures are classified.", "Odd Meter",
			"Learn about more complex time signatures.", "SCALES AND KEY SIGNATURES", "The Major Scale", "Learn how to construct the major scale.",
			"The Minor Scales", "Learn how to construct the three different types of minor scales.", "Scale Degrees", "Learn the special names for each note of a scale.",
			"Learn a method for mathematically calculating key signatures.", "INTERVALS", "Generic Intervals", "Learn how two notes are measured on the staff.",
			"Specific Intervals", "Learn how two notes are specifically measured.", "Writing Intervals", "Learn how to correctly spell intervals with a three-step process.",
			"Interval Inversion", "Learn how to invert intervals.", "CHORDS", "Introduction to Chords", "Learn about the four types of triads.", "Triad Inversion",
			"Learn how to invert triads.", "Seventh Chords", "Learn about the five types of seventh chords.", "More Seventh Chords",
			"Learn about three additional types  used in popular music and jazz.", "of seventh chords", "Seventh Chord Inversion", "Learn how to invert seventh chords.",
			"DIATONIC CHORDS", "Diatonic Triads", "Learn how a scale's notes form special triads.", "Roman Numeral Analysis: Triads", "Learn how diatonic triads are identified.",
			"Diatonic Seventh Chords", "Learn about the diatonic seventh chords of major and minor scales.", "Roman Numeral Analysis: Seventh Chords",
			"Learn how seventh chords are identified in Roman numeral analysis.", "Composing with Minor Scales", "Learn how the natural and harmonic minor scales combine.",
			"Voicing Chords", "Learn how composers arrange the notes of chords.", "Analysis: O Canada", "Watch an analysis of O Canada.", "CHORD PROGRESSIONS",
			"Nonharmonic Tones", "Learn about the different types of nonharmonic tones.", "Phrases and Cadences", "Learn about musical phrases and the different types of cadences.",
			"Circle Progressions", "Learn about root motion and circular chord progressions.", "Common Chord Progressions",
			"Learn how chord progressions tend to follow a common pattern.", "Triads in First Inversion", "Learn how first inversion triads are commonly used",
			" in chord progressions", "Triads in Second Inversion", "Learn how second inversion triads are commonly used", " in chord progressions", "Analysis: Auld Lang Syne",
			"Watch an analysis of Auld Lang Syne.", "NEAPOLITAN CHORDS", "Building Neapolitan Chords", "Learn how to build a Neapolitan chord.", "Using Neapolitan Chords",
			"Learn how a Neapolitan chord in first inversion is commonly used.", "Analysis: Moonlight Sonata", "Watch an analysis Beethoven's Moonlight Sonata (measures 49-51).",
			"FAQ", "Legal", "Privacy", "Tumblr", "Facebook", "Twitter", "&copy; 2000-2015 musictheory.net, LLC Apple, the Apple logo, iPhone, iPad, and iPod are trademarks of Apple "+
			"Inc., registered in the U.S. and other countries. App Store is a service mark of Apple Inc."
		};
		traverseNodesForText(document.children(), texts, 0);
	}
	
	private static int traverseNodesForText(Elements elements, String[] texts, int index) {
		
		for (Element el : elements) {
			String text = el.ownText().trim();
			if (!text.isEmpty()) {
				Assert.assertEquals(texts[index++], text);
			}
			
			index = traverseNodesForText(el.children(), texts, index);
		}
		
		return index;
	}
}
