package grails.plugin.cache.memory

import grails.plugin.cache.memory.builders.Cache2kBuilder
import grails.plugin.cache.memory.builders.CacheBuilder
import grails.plugin.cache.memory.builders.CaffeineBuilder
import grails.plugin.cache.memory.builders.GuavaBuilder
import spock.lang.Specification
import spock.lang.Unroll

class CacheProviderSpec extends Specification {

    @Unroll
    def "test #name Builder"(String name, Class<? extends CacheBuilder> builderClass) {
        when:
        def provider = CacheProvider.fromString(name)
        def builder = provider.cacheBuilder

        then:
        builder.class == builderClass

        where:
        name | builderClass
        'caffeine' | CaffeineBuilder
        'cache2k'  | Cache2kBuilder
        'guava'    | GuavaBuilder
    }

}
