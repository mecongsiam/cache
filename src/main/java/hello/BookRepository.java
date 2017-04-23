package hello;

import java.util.List;

public interface BookRepository {

    Book getByIsbn(String isbn);

    List<Book> getBooks();

    List<Book> updateBooksCache(String id);

}