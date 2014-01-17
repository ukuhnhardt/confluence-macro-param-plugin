package test;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import java.util.Date;
import java.util.Map;

public class TestMacro implements Macro {
    private final XhtmlContent xhtmlContent;
    private final PageManager pageManager;

    public TestMacro(XhtmlContent xhtmlContent, PageManager pageManager) {
        this.xhtmlContent = xhtmlContent;
        this.pageManager = pageManager;
    }

    @Override
    public String execute(Map<String, String> stringStringMap, String s, ConversionContext conversionContext) throws MacroExecutionException {

        try {
            final ContentEntityObject ceo = conversionContext.getEntity();
            String body = xhtmlContent.updateMacroDefinitions(ceo.getBodyAsString(), conversionContext, new MacroDefinitionUpdater(){
                @Override
                public MacroDefinition update(MacroDefinition macroDefinition) {
                    if (macroDefinition.getName().equals("macro-test")){
                        final Map<String, Object> macroDefinitionParameters = macroDefinition.getTypedParameters();
                        macroDefinitionParameters.put("visited", new Date().toString());
                        macroDefinition.setTypedParameters(macroDefinitionParameters);
                    }
                    return macroDefinition;
                }
            });
            ceo.setBodyAsString(body);
            final DefaultSaveContext saveContext = new DefaultSaveContext(true, false, true);
            pageManager.saveContentEntity(ceo, saveContext);

        } catch (XhtmlException e) {
            throw new MacroExecutionException(e);
        }
        return "bla bla";
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }
}
