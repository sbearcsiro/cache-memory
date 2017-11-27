package grails.plugin.cache.memory

import grails.plugins.*

class CacheMemoryGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.2.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Cache Memory" // Headline display name of the plugin
    def author = "Simon Bear"
    def authorEmail = ""
    def description = '''\
Provide in memory cache providers for the Grails cache plugin
'''
    //def profiles = ['web']

    def dependsOn = [cache: "3.0.0 > 4.0.0"]

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/cache-memory"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "MPL2"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Atlas of Living Australia", url: "http://www.ala.org.au/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() { {->
        boolean reloadable = grailsApplication.config.getProperty('grails.cache.memory.reloadable', Boolean, true)
        CacheProvider provider = CacheProvider.fromString(grailsApplication.config.getProperty('grails.cache.memory.cacheProvider', String))

        grailsCacheConfigLoader(MemoryConfigLoader) {
            rebuildable = reloadable
            cacheProvider = provider
        }

        grailsCacheManager(MemoryCacheManager) {
            cacheProvider = provider
        }
    } }

    void doWithDynamicMethods() {
    }

    void doWithApplicationContext() {
    }

    void onChange(Map<String, Object> event) {
    }

    void onConfigChange(Map<String, Object> event) {
    }

    void onShutdown(Map<String, Object> event) {
    }
}
