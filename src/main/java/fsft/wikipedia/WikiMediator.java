package fsft.wikipedia;

import java.util.*;
import io.github.fastily.jwiki.core.*;
import io.github.fastily.jwiki.dwrap.Revision;

public class WikiMediator {

    /* TODO: Implement this datatype

        You must implement the methods with the exact signatures
        as provided in the statement for this mini-project.

        You must add method signatures even for the methods that you
        do not plan to implement. You should provide skeleton implementation
        for those methods, and the skeleton implementation could return
        values like null.

        You can add constructors that could help with your implementation of
        WikiMediatorServer but there should be a default constructor that takes
        no arguments for Task 3.

     */

    public WikiMediator() {
    }

    /**
     * Adds a user to the wiki.
     *
     * @param userName the user to add
     * @return the user that was added, or null if the user was already
     *     in the database
     */
    private final Set<String> users = new HashSet<>();
    public String addUser(String userName) {
        if (users.contains(userName)) {
            return null;
        }
        users.add(userName);
        return userName;
    }

    /**
     * Shutdown the WikiMediator.
     *
     * After calling this method, any future calls to the other methods
     * of the WikiMediator will result in a RuntimeException being thrown.
     *
     * This method is idempotent.
     */
    private boolean isShutdown = false;
    public void shutdown() {
        if (isShutdown) {
            return;
        }
        isShutdown = true;
        users.clear();
    }

    /**
     * Retrieves the categories that are associated with a page on the wiki.
     *
     * @param pageTitle the title of the page to retrieve categories for
     * @return a set of the categories associated with the page
     */
    public Set<String> getCategoriesOnPage(String pageTitle) {
        Wiki wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        return (Set<String>) wiki.getCategoriesOnPage(pageTitle);
    }

    /**
     * Retrieves the links that are associated with a page on the wiki.
     *
     * @param pageTitle the title of the page to retrieve links for
     * @return a set of the links associated with the page
     */
    public Set<String> getLinksOnPage(String pageTitle) {
        Wiki wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        return (Set<String>) wiki.getLinksOnPage(pageTitle);
    }

    /**
     * Search the wiki for a given query.
     *
     * @param searchQuery the query to search for
     * @param limit the maximum number of results to return
     * @return a set of up to <code>limit</code> matching pages
     */
    public Set<String> search(String searchQuery, int limit) {
        Wiki wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        return (Set<String>) wiki.search(searchQuery, limit);
    }
}
