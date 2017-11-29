package grails.plugin.cache.memory.builders

import grails.plugin.cache.memory.cache2k.Cache2kCache
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache

import static java.util.concurrent.TimeUnit.SECONDS
import static grails.plugin.cache.memory.MemoryConfigBuilder.*

@Slf4j
class Cache2kBuilder implements CacheBuilder{
    @Override
    Cache buildCache(Map<String, Object> args) {
        def builder = org.cache2k.Cache2kBuilder.forUnknownTypes()
        def name = args[NAME]?.toString()
        builder.name(name)
        if (args[MAX_ELEMENTS_IN_MEMORY]) builder.entryCapacity(args[MAX_ELEMENTS_IN_MEMORY] as Long)
        if (args[TIME_TO_LIVE_SECONDS]) builder.expireAfterWrite(args[TIME_TO_LIVE_SECONDS] as Long, SECONDS)
        def unsupported = [SOFT_VALUES, WEAK_KEYS, WEAK_VALUES, TIME_TO_IDLE_SECONDS].findAll { args[it] }.join(', ')
        if (unsupported) {
            log.warn("Cache2k unsupported properties: {} specified for cache: {}", unsupported, name)
        }
        new Cache2kCache(name, builder.build())
    }
}
