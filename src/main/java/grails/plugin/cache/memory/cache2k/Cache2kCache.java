package grails.plugin.cache.memory.cache2k;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.util.Assert;

import java.util.concurrent.Callable;

/**
 * Adapter for a Cache2k cache to a Spring Cache
 */
public class Cache2kCache extends AbstractValueAdaptingCache {

    private final String name;

    private final org.cache2k.Cache<Object, Object> cache;


    /**
     * Create a {@link org.springframework.cache.guava.GuavaCache} instance with the specified name and the
     * given internal {@link com.google.common.cache.Cache} to use.
     * @param name the name of the cache
     * @param cache the backing Guava Cache instance
     */
    public Cache2kCache(String name, org.cache2k.Cache<Object, Object> cache) {
        this(name, cache, true);
    }

    /**
     * Create a {@link org.springframework.cache.guava.GuavaCache} instance with the specified name and the
     * given internal {@link com.google.common.cache.Cache} to use.
     * @param name the name of the cache
     * @param cache the backing Guava Cache instance
     * @param allowNullValues whether to accept and convert {@code null}
     * values for this cache
     */
    public Cache2kCache(String name, org.cache2k.Cache<Object, Object> cache, boolean allowNullValues) {
        super(allowNullValues);
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(cache, "Cache must not be null");
        this.name = name;
        this.cache = cache;
    }


    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final org.cache2k.Cache<Object, Object> getNativeCache() {
        return this.cache;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, final Callable<T> valueLoader) {
        try {
            return (T) fromStoreValue(this.cache.computeIfAbsent(key, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return toStoreValue(valueLoader.call());
                }
            }));
        }
        catch (Exception ex) {
            throw new Cache.ValueRetrievalException(key, valueLoader, ex);
        }
    }

    @Override
    protected Object lookup(Object key) {
        return this.cache.peek(key);
    }

    @Override
    public void put(Object key, Object value) {
        this.cache.put(key, toStoreValue(value));
    }

    @Override
    public Cache.ValueWrapper putIfAbsent(Object key, final Object value) {
        try {
            Object result = this.cache.putIfAbsent(key, toStoreValue(value));
            return toValueWrapper(result);
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void evict(Object key) {
        this.cache.remove(key);
    }

    @Override
    public void clear() {
        this.cache.clear();
    }
}
