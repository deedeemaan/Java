package com.example.map_toysocialnetwork_gui.repository.paging;

public class Page<E> {
    private Iterable<E> elemsOnPage;

    private int totalNrOfElems;

    public Page(Iterable<E> elemsOnPage, int totalNrOfElems) {
        this.elemsOnPage = elemsOnPage;
        this.totalNrOfElems = totalNrOfElems;
    }

    public Iterable<E> getElemsOnPage() {
        return elemsOnPage;
    }

    public int getTotalNrOfElems() {
        return totalNrOfElems;
    }
}
