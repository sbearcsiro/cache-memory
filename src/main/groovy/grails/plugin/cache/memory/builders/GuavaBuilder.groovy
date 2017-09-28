package grails.plugin.cache.memory.builders

import org.springframework.cache.Cache
import org.springframework.cache.guava.GuavaCache

import static java.util.concurrent.TimeUnit.SECONDS

class GuavaBuilder implements CacheBuilder {
    @Override
    Cache buildCache(Map<String, Object> args) {
        def builder = com.google.common.cache.CacheBuilder.newBuilder()
        if (args['maxElementsInMemory']) builder.maximumSize(args['maxElementsInMemory'] as Long)
        if (args['softValues']) builder.softValues()
        if (args['weakKeys']) builder.weakKeys()
        if (args['weakValues']) builder.weakValues()
        if (args['timeToLiveSeconds']) builder.expireAfterWrite(args['timeToLiveSeconds'] as Long, SECONDS)
        if (args['timeToIdleSeconds']) builder.expireAfterAccess(args['timeToIdleSeconds'] as Long, SECONDS)
        new GuavaCache(args['name']?.toString(), builder.build())
    }
}
