package hello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleBookRepository implements BookRepository {

    @Value("#{cacheManager.getCache('booksList')}")
    private Cache myCache;

    @Override
    @Cacheable("books")
    public Book getByIsbn(String isbn) {
        simulateSlowService();
        return new Book(isbn, "Some book");
    }

    @Override
    @Cacheable(cacheNames = "booksList")
    public List<Book> getBooks() {
        simulateSlowService();
        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book("00", "kind"));
        bookList.add(new Book("01", "man"));
        return bookList;
    }

    @Override
    public List<Book> updateBooksCache(String id) {
        List<Book> bookList = new ArrayList<>();
        ConcurrentHashMap<SimpleKey[], List<Book>> cacheMap = (ConcurrentHashMap<SimpleKey[], List<Book>>) myCache.getNativeCache();
        if (!cacheMap.isEmpty()) {
            Cache.ValueWrapper wrapper = myCache.get(cacheMap.keys().nextElement());
            bookList = (List<Book>) wrapper.get();
            Optional<Book> bookOptional = bookList.stream().filter(e -> e.getIsbn().equals(id)).findFirst();
            System.out.println(bookOptional.isPresent());
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                book.setTitle("man!!!");
            }
        }
        return bookList;
    }

    // Don't do this at home
    private void simulateSlowService() {
        try {
            long time = 3000L;
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
