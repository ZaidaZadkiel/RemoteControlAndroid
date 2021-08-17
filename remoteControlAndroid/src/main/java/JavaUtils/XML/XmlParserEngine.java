package JavaUtils.XML;

import java.util.HashMap;

public enum XmlParserEngine {
    DEPRECATED, RECOMMENDED;

    HashMap<EngineOption, Boolean> options = new HashMap<EngineOption, Boolean>();

    public void setParserEngineOption(EngineOption option, boolean enabled) {
        options.put(option, enabled);
    }

    public HashMap<EngineOption, Boolean> getParserEngineOptions() {
        return options;
    }

    public enum EngineOption {
        MULTI_THREADED, ON_MAIN_THREAD
    }
}
