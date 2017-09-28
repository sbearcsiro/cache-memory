package grails.plugin.cache.memory.builders

import org.springframework.cache.Cache

interface CacheBuilder {

    Cache buildCache(Map<String, Object> args)

}