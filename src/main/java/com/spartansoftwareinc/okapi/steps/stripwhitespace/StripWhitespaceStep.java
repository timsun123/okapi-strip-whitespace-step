package com.spartansoftwareinc.okapi.steps.stripwhitespace;

import net.sf.okapi.common.Event;
import net.sf.okapi.common.LocaleId;
import net.sf.okapi.common.StringParameters;
import net.sf.okapi.common.UsingParameters;
import net.sf.okapi.common.pipeline.BasePipelineStep;
import net.sf.okapi.common.resource.ITextUnit;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.resource.TextPart;

@UsingParameters(StringParameters.class) // XXX no actual parameters
public class StripWhitespaceStep extends BasePipelineStep {

    public String getDescription() {
        return "Strips whitespace between sentences in target segments.  " +
               "Intended for use in Chinese and Japanese target locales " +
               "where whitespace between sentences is inappropriate.";
    }

    public String getName() {
        return "Strip Target Whitespace";
    }

    /**
     * Handles the {@link net.sf.okapi.common.EventType#TEXT_UNIT} event.
     * @param event event to handle.
     * @return the event returned.
     */
    @Override
    protected Event handleTextUnit(Event event) {
        ITextUnit tu = event.getTextUnit();
        for (LocaleId targetLocale: tu.getTargetLocales()) {
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
            TextContainer target = tu.getTarget(targetLocale);
            for (TextPart tp : target) {
        		tp.text.trim();
            }
        }
        return event;
    }
}
