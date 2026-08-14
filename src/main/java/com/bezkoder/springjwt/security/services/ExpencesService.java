package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Menu.Expences;
import com.bezkoder.springjwt.models.Menu.ExpencesWaiter;
import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Menu.OtherExpences;
import com.bezkoder.springjwt.models.Menu.ShoppingSum;
import com.bezkoder.springjwt.models.Menu.Waiter;
import com.bezkoder.springjwt.payload.request.Menus.ExpencesRequest;
import com.bezkoder.springjwt.payload.request.Menus.ExpencesWaiterRequest;
import com.bezkoder.springjwt.payload.request.Menus.OtherExpencesRequest;
import com.bezkoder.springjwt.payload.request.Menus.ShoppingSumRequest;
import com.bezkoder.springjwt.payload.response.Menu.ExpencesResponse;
import com.bezkoder.springjwt.repository.ExpencesRepos;
import com.bezkoder.springjwt.repository.MenuRepos;
import com.bezkoder.springjwt.repository.WaiterRepos;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.OrderCreateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExpencesService {
    private final ExpencesRepos expencesRepos;
    private final MenuRepos menuRepos;
    private final WaiterRepos waiterRepos;
    private final UserDetailsServiceImpl userDetailsService;

    public ExpencesService(ExpencesRepos expencesRepos, MenuRepos menuRepos, WaiterRepos waiterRepos, UserDetailsServiceImpl userDetailsService) {
        this.expencesRepos = expencesRepos;
        this.menuRepos = menuRepos;
        this.waiterRepos = waiterRepos;
        this.userDetailsService = userDetailsService;
    }

    public List<ExpencesResponse> getAll(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return this.expencesRepos.findAll(this.userDetailsService.getCurrentUser().getId())
                    .stream()
                    .map(ExpencesResponse::from)
                    .toList();
        }

        LocalDateTime start = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? null : endDate.atTime(LocalTime.MAX);

        return this.expencesRepos.findAllInDateRange(start, end,this.userDetailsService.getCurrentUser().getId())
                .stream()
                .map(ExpencesResponse::from)
                .toList();
    }

    @Transactional
    public ExpencesResponse create(ExpencesRequest req) {
        validateMenuIsNotDuplicated(req.getMenuId(), null);

        Expences expences = new Expences();
        applyRequest(expences, req);

        try {
            return ExpencesResponse.from(this.expencesRepos.save(expences));
        } catch (Exception e) {
            throw new OrderCreateException("Can't create expences: " + e.getMessage());
        }
    }

    @Transactional
    public ExpencesResponse edit(ExpencesRequest req) {
        if (req.getId() == null) {
            throw new NoContentException("Expences id is required");
        }

        Expences expences = this.expencesRepos.findById(req.getId())
                .orElseThrow(() -> new NoContentException("Expences not found"));

        validateMenuIsNotDuplicated(req.getMenuId(), expences.getId());
        applyRequest(expences, req);

        try {
            return ExpencesResponse.from(this.expencesRepos.save(expences));
        } catch (Exception e) {
            throw new OrderCreateException("Can't edit expences: " + e.getMessage());
        }
    }

    @Transactional
    public Long delete(Long id) {
        Expences expences = this.expencesRepos.findById(id)
                .orElseThrow(() -> new NoContentException("Expences not found"));

        new ArrayList<>(expences.getWaiters()).forEach(expences::removeWaiter);
        new ArrayList<>(expences.getOtherExpences()).forEach(expences::removeOtherExpence);
        new ArrayList<>(expences.getShoppingSums()).forEach(expences::removeShoppingSum);
        this.expencesRepos.delete(expences);
        return id;
    }

    private void applyRequest(Expences expences, ExpencesRequest req) {
        Menu menu = this.menuRepos.findById(req.getMenuId())
                .orElseThrow(() -> new NoContentException("Menu not found"));

        expences.setMenu(menu);
        replaceWaiters(expences, req.getStaff());
        replaceOtherExpences(expences, req.getOtherExpences());
        replaceShoppingSums(expences, req.getShoppingSums());
    }

    private void validateMenuIsNotDuplicated(Long menuId, Long expencesId) {
        if (menuId == null) {
            throw new NoContentException("Menu id is required");
        }

        boolean duplicated = expencesId == null
                ? this.expencesRepos.existsByMenuId(menuId)
                : this.expencesRepos.existsByMenuIdAndIdNot(menuId, expencesId);

        if (duplicated) {
            throw new OrderCreateException("Expences already exist for this menu");
        }
    }

    private void replaceWaiters(Expences expences, List<ExpencesWaiterRequest> requestedStaff) {
        new ArrayList<>(expences.getWaiters()).forEach(expences::removeWaiter);

        List<ExpencesWaiterRequest> items = requestedStaff == null ? List.of() : requestedStaff;
        List<Long> ids = items.stream()
                .map(ExpencesWaiterRequest::getStaffId)
                .toList();
        Map<Long, Waiter> waiters = this.waiterRepos.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Waiter::getId, Function.identity()));

        if (waiters.size() != new HashSet<>(ids).size()) {
            throw new NoContentException("Staff not found");
        }

        items.stream()
                .map(req -> toExpencesWaiter(req, waiters.get(req.getStaffId())))
                .forEach(expences::addWaiter);
    }

    private void replaceOtherExpences(Expences expences, List<OtherExpencesRequest> requestedOtherExpences) {
        new ArrayList<>(expences.getOtherExpences()).forEach(expences::removeOtherExpence);

        List<OtherExpencesRequest> items = requestedOtherExpences == null ? List.of() : requestedOtherExpences;
        items.stream()
                .map(this::toOtherExpences)
                .forEach(expences::addOtherExpence);
    }

    private OtherExpences toOtherExpences(OtherExpencesRequest req) {
        return OtherExpences.builder()
                .name(req.getName())
                .amount(req.getAmount())
                .build();
    }

    private void replaceShoppingSums(Expences expences, List<ShoppingSumRequest> requestedShoppingSums) {
        new ArrayList<>(expences.getShoppingSums()).forEach(expences::removeShoppingSum);

        List<ShoppingSumRequest> items = requestedShoppingSums == null ? List.of() : requestedShoppingSums;
        items.stream()
                .map(this::toShoppingSum)
                .forEach(expences::addShoppingSum);
    }

    private ShoppingSum toShoppingSum(ShoppingSumRequest req) {
        return ShoppingSum.builder()
                .name(req.getName())
                .date(req.getDate() == null ? LocalDate.now() : req.getDate())
                .sum(req.getSum())
                .build();
    }

    private ExpencesWaiter toExpencesWaiter(ExpencesWaiterRequest req, Waiter waiter) {
        return ExpencesWaiter.builder()
                .waiter(waiter)
                .price(req.getPrice())
                .payed(req.isPayed())
                .build();
    }
}
