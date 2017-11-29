package grails.plugin.cache.memory

import grails.plugin.cache.ConfigLoader
import groovy.util.logging.Slf4j
import org.springframework.context.ApplicationContext

@Slf4j('logger')
class MemoryConfigLoader extends ConfigLoader {
    protected boolean rebuildable

//    public CacheProvider cacheProvider

    private boolean built = false

    void setRebuildable(boolean rebuildable){
        this.rebuildable = rebuildable
    }

    void reload(List<ConfigObject> configs, ApplicationContext ctx) {
        if(!rebuildable && built){
            logger.info("Reload attempted, but reloading has been disabled by configuration. Ignoring the reload attempt.")
            return
        }

        MemoryConfigBuilder builder = new MemoryConfigBuilder()
        for (ConfigObject co : configs) {
            def config = co.config
            if (config instanceof Closure) {
                builder.parse config
            }
        }

        MemoryCacheManager cacheManager = ctx.grailsCacheManager

        // make copy of names to avoid CME
        for (String name in cacheManager.cacheNames) {
            cacheManager.destroyCache name
        }

        cacheManager.buildCaches(builder)

        built = true

    }
}
