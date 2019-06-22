package ch.caro62.service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.Image;

/**
 *
 * @author Karalina Chureyna
 */
public class ImageCache {
    
    private static final LoadingCache<String, Image> IMAGE_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(128)
            .recordStats()
            .build((String key) -> {
                return new Image(key, true);
            });
    
    public static LoadingCache<String, Image> getCache() {
        return IMAGE_CACHE;
    }
    
}
