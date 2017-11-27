package grails.plugin.cache.memory

import spock.lang.Specification

import static grails.plugin.cache.memory.MemoryConfigBuilder.*

class MemoryConfigBuilderSpec extends Specification {

    def "test parse with defaults"() {
        when:
        def b = new MemoryConfigBuilder()

        b.parse({
            defaults {
                provider 'guava'
                timeToIdleSeconds 10
                softValues true
                weakValues false
                weakKeys true
            }

            defaultCache {
                provider 'caffeine'
                timeToLiveSeconds 20
                softValues false
                weakValues true
            }

            cache {
                name 'potato'
                timeToIdleSeconds 60
                maxElementsInMemory 100
            }
        })

        def caches = b.caches
        def defaultCache = b.defaultCache

        then:

        caches.size() == 1
        def cache = caches[0]

        cache[TIME_TO_IDLE_SECONDS] == 60
        cache[MAX_ELEMENTS_IN_MEMORY] == 100
        cache[PROVIDER] == 'guava'
        cache[SOFT_VALUES] == true
        cache[WEAK_VALUES] == false
        cache[WEAK_KEYS] == true

        defaultCache != null
        defaultCache[TIME_TO_IDLE_SECONDS] == 10
        defaultCache[TIME_TO_LIVE_SECONDS] == 20
        defaultCache[PROVIDER] == 'caffeine'
        defaultCache[SOFT_VALUES] == false
        defaultCache[WEAK_VALUES] == true
        defaultCache[WEAK_KEYS] == true
    }

}
