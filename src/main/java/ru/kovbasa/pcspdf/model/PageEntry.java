package ru.kovbasa.pcspdf.model;

public class PageEntry implements Comparable<PageEntry> {

    private String pdfName;
    private int page;
    private int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(PageEntry entry) {
        return count > entry.count ? -1 : 1;
    }

    @Override
    public String toString() {
        return "PageEntry{pdf:" + pdfName +
                ", page:" + page +
                ", count:" + count + "}";
    }
}