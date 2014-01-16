package com.spartansoftwareinc.okapi.steps.stripwhitespace;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import net.sf.okapi.common.ClassUtil;
import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.Util;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.filters.html.HtmlFilter;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatch;
import net.sf.okapi.lib.extra.pipelinebuilder.XBatchItem;
import net.sf.okapi.lib.extra.pipelinebuilder.XParameter;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipeline;
import net.sf.okapi.lib.extra.pipelinebuilder.XPipelineStep;
import net.sf.okapi.lib.extra.steps.EventListBuilderStep;
import net.sf.okapi.steps.common.RawDocumentToFilterEventsStep;
import net.sf.okapi.steps.segmentation.Parameters;
import net.sf.okapi.steps.segmentation.SegmentationStep;

import org.junit.*;

import static org.junit.Assert.*;

public class TestStripWhitespaceStep {

	private String pathBase = Util.ensureSeparator(ClassUtil.getTargetPath(this.getClass()), true);
	
    @Test
    public void testStripWhitespaceInJapanese() throws URISyntaxException {
		List<ITextUnit> tus = filterTus(runPipelineFor("/test1.html", LocaleId.JAPANESE));
		assertEquals(1, tus.size());
		ITextUnit tu = tus.get(0);
		assertEquals("Sentence 1.Sentence 2.", tu.getTarget(LocaleId.JAPANESE).toString());
    }
    
    @Test
    public void testStripWhitespaceInChinese() throws URISyntaxException {
		List<ITextUnit> tus = filterTus(runPipelineFor("/test1.html", LocaleId.CHINA_CHINESE));
		assertEquals(1, tus.size());
		ITextUnit tu = tus.get(0);
		assertEquals("Sentence 1.Sentence 2.", tu.getTarget(LocaleId.CHINA_CHINESE).toString());
    }

    @Test
    public void testStripWhitespaceInTraditionalChinese() throws URISyntaxException {
		List<ITextUnit> tus = filterTus(runPipelineFor("/test1.html", LocaleId.TAIWAN_CHINESE));
		assertEquals(1, tus.size());
		ITextUnit tu = tus.get(0);
		assertEquals("Sentence 1.Sentence 2.", tu.getTarget(LocaleId.TAIWAN_CHINESE).toString());
    }
    
    @Test
    public void testNoStripWhitespaceInFrench() throws URISyntaxException {
		List<ITextUnit> tus = filterTus(runPipelineFor("/test1.html", LocaleId.FRENCH));
		assertEquals(1, tus.size());
		ITextUnit tu = tus.get(0);
		assertEquals("Sentence 1. Sentence 2.", tu.getTarget(LocaleId.FRENCH).toString());
    }
    
    private List<Event> runPipelineFor(String filename, LocaleId targetLocale) throws URISyntaxException {
    	EventListBuilderStep elbs = new EventListBuilderStep();
    	new XPipeline(
				"Test pipeline for StripWhitespaceStep",
				new XBatch(
						new XBatchItem(
								getClass().getResource(filename).toURI(),
								"UTF-8",
								LocaleId.ENGLISH, targetLocale)
				),
						
				new RawDocumentToFilterEventsStep(new HtmlFilter()),
				new XPipelineStep(new SegmentationStep(),
					new XParameter("copySource", true),
					new XParameter("sourceSrxPath", pathBase + "default.srx"),
					new XParameter("trimSrcLeadingWS", Parameters.TRIM_YES),
					new XParameter("trimSrcTrailingWS", Parameters.TRIM_YES)
				),
				new StripWhitespaceStep(),
				elbs
		).execute();
    	return elbs.getList();
    }
    
    List<ITextUnit> filterTus(List<Event> events) {
    	List<ITextUnit> tus = new ArrayList<ITextUnit>();
    	for (Event e : events) {
    		if (e.isTextUnit()) {
    			tus.add(e.getTextUnit());
    		}
    	}
    	return tus;
    }
}
