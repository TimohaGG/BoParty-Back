package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Menu.Waiter;
import com.bezkoder.springjwt.payload.request.Menus.WaiterRequest;
import com.bezkoder.springjwt.payload.response.Menu.WaiterResponse;
import com.bezkoder.springjwt.repository.WaiterRepos;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.OrderCreateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WaiterService {
    private final WaiterRepos waiterRepos;

    public WaiterService(WaiterRepos waiterRepos) {
        this.waiterRepos = waiterRepos;
    }

    public List<WaiterResponse> getAll() {
        return this.waiterRepos.findAll()
                .stream()
                .map(WaiterResponse::from)
                .toList();
    }

    @Transactional
    public WaiterResponse create(WaiterRequest req) {
        Waiter waiter = new Waiter();
        waiter.setName(req.getName());
        waiter.setType(normalizeType(req.getType()));

        try {
            return WaiterResponse.from(this.waiterRepos.save(waiter));
        } catch (Exception e) {
            throw new OrderCreateException("Can't create staff: " + e.getMessage());
        }
    }

    @Transactional
    public WaiterResponse edit(WaiterRequest req) {
        if (req.getId() == null) {
            throw new NoContentException("Staff id is required");
        }

        Waiter waiter = this.waiterRepos.findById(req.getId())
                .orElseThrow(() -> new NoContentException("Staff not found"));

        waiter.setName(req.getName());
        waiter.setType(normalizeType(req.getType()));

        try {
            return WaiterResponse.from(this.waiterRepos.save(waiter));
        } catch (Exception e) {
            throw new OrderCreateException("Can't edit staff: " + e.getMessage());
        }
    }

    @Transactional
    public Long delete(Long id) {
        Waiter waiter = this.waiterRepos.findById(id)
                .orElseThrow(() -> new NoContentException("Staff not found"));

        this.waiterRepos.delete(waiter);
        return id;
    }

    private String normalizeType(String type) {
        return "COOK".equalsIgnoreCase(type) ? "COOK" : "WAITER";
    }
}
