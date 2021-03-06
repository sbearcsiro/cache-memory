package grails.plugin.cache.memory

import groovy.util.logging.Slf4j

@Slf4j
class MemoryConfigBuilder extends BuilderSupport {

    public static final String SOFT_VALUES = 'softValues'
    public static final String WEAK_KEYS = 'weakKeys'
    public static final String WEAK_VALUES = 'weakValues'
    public static final String CACHE_LOADER_TIMEOUT_MILLIS = 'cacheLoaderTimeoutMillis'
    public static final String PROVIDER = 'provider'
    public static final String MAX_ELEMENTS_IN_MEMORY = 'maxElementsInMemory'
    public static final String TIME_TO_IDLE_SECONDS = 'timeToIdleSeconds'
    public static final String TIME_TO_LIVE_SECONDS = 'timeToLiveSeconds'
    public static final String NAME = 'name'
    protected int unrecognizedElementDepth = 0
    protected List<String> stack = []
    protected List<Map<String, Object>> caches = []
    protected Map<String, Object> defaultCache
    protected Map<String, Object> defaults = [:]
    protected Map<String, Object> current

    protected static final List DEFAULT_CACHE_PARAM_NAMES = [
            SOFT_VALUES, WEAK_KEYS, WEAK_VALUES,
            CACHE_LOADER_TIMEOUT_MILLIS, PROVIDER,
            MAX_ELEMENTS_IN_MEMORY,
            TIME_TO_IDLE_SECONDS, TIME_TO_LIVE_SECONDS]

    protected static final List CACHE_PARAM_NAMES = DEFAULT_CACHE_PARAM_NAMES + [
            NAME]

    def parse(Closure c) {
        c.delegate = this
        c.resolveStrategy = Closure.DELEGATE_FIRST
        c()

        resolveProperties()
    }

    @Override
    protected void setParent(Object parent, Object child) {
        log.trace("setParent {}, child: {}", parent, child)
        // do nothing
    }

    @Override
    protected createNode(name) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn("ignoring node {} contained in unrecognized parent node", name)
            return
        }

        log.trace("createNode {}", name)

        switch (name) {
            case 'cacheProvider':
            case 'defaults':
                stack.push name
                return name

            case 'defaultCache':
                if (defaultCache == null) {
                    defaultCache = [:]
                }
                stack.push name
                return name

            case 'cache':
                current = [:]
                caches << current
                stack.push name
                return name
        }

        unrecognizedElementDepth++
        log.warn("Cannot create empty node with name '{}'", name)
    }

    @Override
    protected createNode(name, value) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn("ignoring node {} with value {} contained in unrecognized parent node", name, value)
            return
        }

        log.trace("createNode {}, value: {}", name, value)

        String level = stack[-1]
        stack.push name

        switch (level) {
            case 'defaultCache':
                if (name in DEFAULT_CACHE_PARAM_NAMES) {
                    defaultCache[name] = value
                    return name
                }
                break

            case 'defaults':

                if (name in CACHE_PARAM_NAMES) {
                    defaults[name] = value
                    return name
                }

                break

            case 'cache':

                if (NAME == name || 'cache' == name  || name in CACHE_PARAM_NAMES) {
                    current[name] = value
                    return name
                }

                break
        }

        unrecognizedElementDepth++
        log.warn("Cannot create node with name '{}' and value '{}' for parent '{}'", name, value, level)
    }

    @Override
    protected createNode(name, Map attributes) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn("ignoring node {} with attributes {} contained in unrecognized parent node", name, attributes)
            return
        }

        log.trace("createNode {} + attributes: {}", name, attributes)
    }

    @Override
    protected createNode(name, Map attributes, value) {
        if (unrecognizedElementDepth) {
            unrecognizedElementDepth++
            log.warn("ignoring node {} with value {} and attributes {} contained in unrecognized parent node", name, value, attributes)
            return
        }

        log.trace("createNode {} + value: {} attributes: {}", name, value, attributes)
    }

    @Override
    protected void nodeCompleted(parent, node) {
        log.trace("nodeCompleted {} {}", parent,  node)

        if (unrecognizedElementDepth) {
            unrecognizedElementDepth--
        }
        else {
            stack.pop()
        }
    }

    protected void resolveProperties() {
        mergeCaches()

        setDefaults()

    }

    protected void setDefaults() {
        for (data in caches) {
            Map<String, Object> withDefaults = [:]
            withDefaults.putAll defaults
            withDefaults.putAll data
            data.clear()
            data.putAll withDefaults
        }

        if (defaultCache != null) {
            Map<String, Object> withDefaults = [:]
            withDefaults.putAll defaults
            withDefaults.putAll defaultCache
            defaultCache.clear()
            defaultCache.putAll withDefaults
        }
    }

    protected void mergeCaches() {
        mergeDefinitions caches, NAME
    }

    protected void mergeDefinitions(List<Map<String, Object>> definitions, String propertyName) {
        int count = definitions.size()
        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {
                if (definitions[j][propertyName] == definitions[i][propertyName]) {
                    definitions[i].putAll definitions[j]
                    definitions.remove j
                    count--
                    j--
                }
            }
        }
    }

}
