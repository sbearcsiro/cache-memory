package grails.plugin.cache.memory.builders

import org.springframework.cache.Cache
import org.springframework.cache.guava.GuavaCache

import static java.util.concurrent.TimeUnit.SECONDS
import static grails.plugin.cache.memory.MemoryConfigBuilder.*

class GuavaBuilder implements CacheBuilder {
    @Override
    Cache buildCache(Map<String, Object> args) {
        def builder = com.google.common.cache.CacheBuilder.newBuilder()
        if (args[MAX_ELEMENTS_IN_MEMORY]) builder = builder.maximumSize(args[MAX_ELEMENTS_IN_MEMORY] as Long)
        if (args[WEAK_KEYS]) builder = builder.weakKeys()
        if (args[SOFT_VALUES]) builder = builder.softValues()
        if (args[WEAK_VALUES]) builder = builder.weakValues()
        if (args[TIME_TO_LIVE_SECONDS]) builder = builder.expireAfterWrite(args[TIME_TO_LIVE_SECONDS] as Long, SECONDS)
        if (args[TIME_TO_IDLE_SECONDS]) builder = builder.expireAfterAccess(args[TIME_TO_IDLE_SECONDS] as Long, SECONDS)
        new GuavaCache(args[NAME]?.toString(), builder.build())
    }
}
