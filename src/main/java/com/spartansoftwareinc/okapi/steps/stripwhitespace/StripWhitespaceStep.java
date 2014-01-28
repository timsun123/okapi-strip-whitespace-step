package com.spartansoftwareinc.okapi.steps.stripwhitespace;

import java.util.List;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.pipeline.annotations.StepParameterMapping;
import net.sf.okapi.common.pipeline.annotations.StepParameterType;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextPart;

@UsingParameters(StripWhitespaceParameters.class) // XXX no actual parameters
public class StripWhitespaceStep extends BasePipelineStep {

    public String getDescription() {
        return "Strips whitespace between sentences in target segments.  " +
               "Intended for use in Chinese and Japanese target locales " +
               "where whitespace between sentences is inappropriate.";
    }

    public String getName() {
        return "Strip Target Whitespace";
    }

    private List<LocaleId> targetLocales;
    
	@StepParameterMapping(parameterType = StepParameterType.TARGET_LOCALES)
	public void setTargetLocales (List<LocaleId> targetLocales) {
		this.targetLocales = targetLocales;
	}
	
	public List<LocaleId> getTargetLocales() {
		return targetLocales;
	}
	
    /**
     * Handles the {@link net.sf.okapi.common.EventType#TEXT_UNIT} event.
     * @param event event to handle.
     * @return the event returned.
     */
    @Override
    protected Event handleTextUnit(Event event) {
        ITextUnit tu = event.getTextUnit();
        // Ontram only: XINI files don't give us full TUs, just the source.
        // This means we need to check for the target locales a different way.
        // (All Ontram merges are also single-target, so this is safe.)
        // For XLIFF or other formats, this loop should iterate over the 
        // the TU targets and check the locale of each.  However, there's
        // no StepParameterMapping that we could use to inject a flag to 
        // let us determine it.
        for (LocaleId targetLocale: targetLocales) {
            if (!LocaleId.JAPANESE.sameLanguageAs(targetLocale) &&
                !LocaleId.CHINA_CHINESE.sameLanguageAs(targetLocale)) {
                    continue;
            }
            /**
             * If whitespace trimming was enabled during segmentation, the
             * whitespace will be trapped in non-Segment TextParts.  So
             * we need to check everything in the container, not just the
             * results of tu.getTargetSegments();
             */
            TextContainer target = tu.getSource();
            for (TextPart tp : target) {
        		tp.text.trim();
            }
            break;
        }
        return event;
    }
}
