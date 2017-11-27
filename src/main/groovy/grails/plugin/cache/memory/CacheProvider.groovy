package grails.plugin.cache.memory

import grails.plugin.cache.memory.builders.Cache2kBuilder
import grails.plugin.cache.memory.builders.CacheBuilder
import grails.plugin.cache.memory.builders.CaffeineBuilder
import grails.plugin.cache.memory.builders.GuavaBuilder

enum CacheProvider {
    CAFFEINE(CaffeineBuilder.metaClass.&invokeConstructor), CACHE2K(Cache2kBuilder.metaClass.&invokeConstructor), GUAVA(GuavaBuilder.metaClass.&invokeConstructor)

    private CacheProvider(Closure<CacheBuilder> cacheBuilderClosure) {
        this.cacheBuilderClosure = cacheBuilderClosure
    }

    private Closure<CacheBuilder> cacheBuilderClosure

    CacheBuilder getCacheBuilder() {
        return cacheBuilderClosure()
    }

    static CacheProvider fromString(String cacheProvider) {
        return values().find { it.name().equalsIgnoreCase(cacheProvider) }
    }
}