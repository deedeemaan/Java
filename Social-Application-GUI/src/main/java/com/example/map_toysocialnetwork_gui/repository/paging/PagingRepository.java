package com.example.map_toysocialnetwork_gui.repository.paging;

import com.example.map_toysocialnetwork_gui.domain.Entity;
import com.example.map_toysocialnetwork_gui.domain.FriendRequest;
import com.example.map_toysocialnetwork_gui.domain.Tuple;
import com.example.map_toysocialnetwork_gui.repository.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAllOnPage(Pageable pageable);
}

