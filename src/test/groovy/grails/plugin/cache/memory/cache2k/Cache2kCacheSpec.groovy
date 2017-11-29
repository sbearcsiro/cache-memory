package grails.plugin.cache.memory.cache2k

import org.cache2k.Cache2kBuilder
import spock.lang.Specification
import spock.lang.Subject

@Subject(Cache2kCache)
class Cache2kCacheSpec extends Specification {


    Cache2kCache cache

    def setup() {
        cache = new Cache2kCache('name', Cache2kBuilder.forUnknownTypes().name('name').build())
    }

    def cleanup() {
        cache.nativeCache.clearAndClose()
    }

    def "test get"() {
        when:
        cache.put('a', 'b')

        then:
        cache.get('a').get() == 'b'
    }

    def "test getType"() {
        when:
        cache.put('a', 'b')

        then:
        cache.get('a', String) == 'b'
    }

    def "test getWithLoader"() {
        when:
        def a1 = cache.get('a') {
            'b'
        }

        def a2 = cache.get('a') {
            'c'
        }

        cache.put('b', 'c')

        def b1 = cache.get('b') {
            'd'
        }

        then:
        a1 == 'b'
        a2 == 'b'
        cache.get('a', String) == 'b'

        b1 == 'c'
        cache.get('b', String) == 'c'
    }

    def "test clear"() {
        when:
        cache.put 'a', 'b'
        cache.clear()

        then:
        cache.get('a') == null
        cache.get('a', String) == null
    }

    def "test putIfAbsent"() {
        when:
        cache.putIfAbsent('a', 'b')
        cache.putIfAbsent('a', 'c')

        cache.put('b', 'c')
        cache.putIfAbsent('b', 'd')

        then:
        cache.get('a', String) == 'b'
        cache.get('b', String) == 'c'
    }

    def "test evict"() {
        when:
        cache.put('a', 'b')
        cache.evict('a')

        then:
        cache.get('a', String) == null
    }

    def "test lookup"() {
        when:
        cache.put('a', 'b')

        then:
        cache.lookup('a') == 'b'
    }

}
