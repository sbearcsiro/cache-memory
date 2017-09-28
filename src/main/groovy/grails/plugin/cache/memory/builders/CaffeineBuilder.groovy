package grails.plugin.cache.memory.builders

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.caffeine.CaffeineCache

import static java.util.concurrent.TimeUnit.SECONDS

class CaffeineBuilder implements CacheBuilder {
    @Override
    Cache buildCache(Map<String, Object> args) {
        def builder = Caffeine.newBuilder()
        if (args['maxElementsInMemory']) builder.maximumSize(args['maxElementsInMemory'] as Long)
        if (args['softValues']) builder.softValues()
        if (args['weakKeys']) builder.weakKeys()
        if (args['weakValues']) builder.weakValues()
        if (args['timeToLiveSeconds']) builder.expireAfterWrite(args['timeToLiveSeconds'] as Long, SECONDS)
        if (args['timeToIdleSeconds']) builder.expireAfterAccess(args['timeToIdleSeconds'] as Long, SECONDS)
        new CaffeineCache(args['name']?.toString(), builder.build())
    }
}
