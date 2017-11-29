package grails.plugin.cache.memory

import grails.plugin.cache.memory.builders.CaffeineBuilder
import grails.plugin.cache.memory.cache2k.Cache2kCache
import org.cache2k.Cache
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.guava.GuavaCache
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.SECONDS

class MemoryCacheManagerSpec extends Specification {

    def "test getCacheBuilder"() {
        when:
        def b = MemoryCacheManager.cacheBuilder

        then:
        b instanceof CaffeineBuilder // caffeine always on classpath
    }

    def "test buildCaches"() {
        when:
        def mcb = GroovyStub(MemoryConfigBuilder)
        mcb.defaultCache >> [
                'timeToLiveSeconds': 10,
                'timeToIdleSeconds': 10,
                'weakKeys': false,
                'softValues': false,
                'weakValues': true,
                'cacheLoaderTimeoutMillis': 100,
                'provider': 'guava',
                'maxElementsInMemory': 1
        ]

        mcb.caches >> [
                [
                        'name': 'a',
                        'timeToLiveSeconds': 20,
                        'timeToIdleSeconds': 20,
                        'weakKeys': true,
                        'softValues': true,
                        'weakValues': false,
                        'cacheLoaderTimeoutMillis': 200,
                        'provider': 'cache2k',
                        'maxElementsInMemory': 2
                ],
                [
                        'name': 'b',
                        'timeToLiveSeconds': 20,
                        'timeToIdleSeconds': 20,
                        'weakKeys': true,
                        'softValues': true,
                        'weakValues': false,
                        'cacheLoaderTimeoutMillis': 200,
                        'provider': 'caffeine',
                        'maxElementsInMemory': 2
                ],
                [
                        'name': 'c',
                        'timeToLiveSeconds': 20,
                        'timeToIdleSeconds': 20,
                        'weakKeys': true,
                        'softValues': true,
                        'weakValues': false,
                        'cacheLoaderTimeoutMillis': 200,
                        'maxElementsInMemory': 2
                ]
        ]

        def manager = new MemoryCacheManager()

        manager.buildCaches(mcb)

        then:

        def a = manager.getCache('a')
        def b = manager.getCache('b')
        def c = manager.getCache('c')
        def d = manager.getCache('d')
        a instanceof Cache2kCache
        b instanceof CaffeineCache
        c instanceof CaffeineCache
        d instanceof GuavaCache

        ((Cache)a.nativeCache).name == 'a'

        b.name == 'b'
        def bCache = (com.github.benmanes.caffeine.cache.Cache)b.nativeCache

        def bPolicy = bCache.policy()
        bPolicy.expireAfterWrite().get().getExpiresAfter(SECONDS) == 20
        bPolicy.expireAfterAccess().get().getExpiresAfter(SECONDS) == 20
        bPolicy.eviction().get().getMaximum() == 2

        c.name == 'c'

        d.name == 'd'

        cleanup:
        a.close()
    }
}
