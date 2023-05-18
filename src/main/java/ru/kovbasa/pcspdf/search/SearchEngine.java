package ru.kovbasa.pcspdf.search;

import ru.kovbasa.pcspdf.model.PageEntry;

import java.util.Set;

public interface SearchEngine {

    Set<PageEntry> search(String word);
}
