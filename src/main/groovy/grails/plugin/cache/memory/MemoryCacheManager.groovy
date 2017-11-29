package grails.plugin.cache.memory

import grails.plugin.cache.GrailsCacheManager
import grails.plugin.cache.memory.builders.Cache2kBuilder
import grails.plugin.cache.memory.builders.CacheBuilder
import grails.plugin.cache.memory.builders.CaffeineBuilder
import grails.plugin.cache.memory.builders.GuavaBuilder
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Slf4j
class MemoryCacheManager implements GrailsCacheManager {

    protected ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>()

    CacheProvider cacheProvider

    Map<String, Object> defaultCache

    @Override
    boolean cacheExists(String name) {
        return caches.containsKey(name)
    }

    @Override
    boolean destroyCache(String name) {
        def cache = caches.remove(name)
        cache?.clear()
        if (cache instanceof Closeable) {
            cache?.close()
        }
        return cache != null
    }

    @Override
    Cache getCache(String name) {
        Cache cache = caches.get(name)
        if (cache == null) {
            cache = createDefaultCache(name)
            Cache existing = caches.putIfAbsent(name, cache)
            if (existing != null) {
                cache = existing
            }
        }
        return cache
    }

    Cache createDefaultCache(String name) {
        def builder = (CacheProvider.fromString(defaultCache.get('provider')) ?: cacheProvider)?.cacheBuilder ?: getCacheBuilder()
        builder.buildCache(defaultCache + [name: name])
    }

    @Override
    Collection<String> getCacheNames() {
        return new HashSet<>(caches.keySet())
    }

    void buildCaches(MemoryConfigBuilder memoryConfigBuilder) {
        this.defaultCache = memoryConfigBuilder.defaultCache
        def defaultBuilder = cacheProvider?.cacheBuilder ?: getCacheBuilder()
        for (cache in memoryConfigBuilder.caches) {
            def provider = CacheProvider.fromString(cache['provider'])
            def builtCache = provider?.cacheBuilder?.buildCache(cache) ?: defaultBuilder.buildCache(cache)
            def name = cache.get('name')?.toString()
            caches.put(name, builtCache)
        }
    }

    static CacheBuilder getCacheBuilder() {
        try {
            Class.forName('com.github.benmanes.caffeine.cache.Caffeine')
            log.debug("Caffeine found on classpath")
            return new CaffeineBuilder()
        } catch (ClassNotFoundException e) {
            log.debug("Caffeine not found on classpath")
        }
        try {
            Class.forName('org.cache2k.Cache')
            log.debug("Cache2k found on classpath")
            return new Cache2kBuilder()
        } catch (ClassNotFoundException e) {
            log.debug("Cache2k not found on classpath")
        }
        try {
            Class.forName('com.google.common.cache.Cache')
            log.debug("Guava Cache found on classpath")
            return new GuavaBuilder()
        } catch (ClassNotFoundException e) {
            log.debug("Guava not found on classpath")
        }
        throw new IllegalStateException("Caffeine, Cache2k or Guava are required")
    }
}
