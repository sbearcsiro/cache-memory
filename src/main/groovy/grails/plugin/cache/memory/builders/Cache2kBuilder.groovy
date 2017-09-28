package grails.plugin.cache.memory.builders

import grails.plugin.cache.memory.cache2k.Cache2kCache
import org.springframework.cache.Cache

import static java.util.concurrent.TimeUnit.SECONDS

class Cache2kBuilder implements CacheBuilder{
    @Override
    Cache buildCache(Map<String, Object> args) {
        def builder = org.cache2k.Cache2kBuilder.forUnknownTypes()
        def name = args['name']?.toString()
        builder.name(name)
        if (args['maxElementsInMemory']) builder.entryCapacity(args['maxElementsInMemory'] as Long)
        if (args['timeToLiveSeconds']) builder.expireAfterWrite(args['timeToLiveSeconds'] as Long, SECONDS)
        new Cache2kCache(name, builder.build())
    }
}
