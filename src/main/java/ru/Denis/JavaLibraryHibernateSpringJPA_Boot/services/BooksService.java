package ru.Denis.JavaLibraryHibernateSpringJPA_Boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Denis.JavaLibraryHibernateSpringJPA_Boot.models.Book;
import ru.Denis.JavaLibraryHibernateSpringJPA_Boot.models.Person;
import ru.Denis.JavaLibraryHibernateSpringJPA_Boot.repositories.BooksRepository;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> index(boolean sortByYear) {
        return (sortByYear) ?
                (booksRepository.findAll(Sort.by("releaseDate"))) :
                (booksRepository.findAll());
    }

    public Optional<Book> show(Integer id) {
        return booksRepository.findById(id);
    }

    public Optional<Person> showOwnerOfBook(Integer id) {
        return Optional.ofNullable(booksRepository.findById(id).get().getOwner());
    }

    public List<Book> findWithPagination(int page, int booksPerPage, boolean sortByYear) {

        return (sortByYear) ?
                (booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("releaseDate"))).getContent()) :
                (booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent());
    }

    public List<Book> findBook(String findBook) {
        if (findBook == null || findBook.equals("")) return Collections.emptyList();
        return booksRepository.findBookByTitleStartingWith(findBook);
    }

    @Transactional
    public void save(Book newBook) {
        booksRepository.save(newBook);
    }

    @Transactional
    public void update(Book updateBook, int id) {
        Book bookToBeUpdate = booksRepository.findById(id).get();

        updateBook.setId(id);
        updateBook.setOwner(bookToBeUpdate.getOwner());
        updateBook.setTakingBook(bookToBeUpdate.getTakingBook());

        booksRepository.save(updateBook);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void toFree(int id) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakingBook(null);
                });
    }

    @Transactional
    public void assignBook(int book_id, Person person) {
        booksRepository.findById(book_id).ifPresent(
                book -> {
                    book.setOwner(person);
                    book.setTakingBook(new Date());
                });
    }

}
