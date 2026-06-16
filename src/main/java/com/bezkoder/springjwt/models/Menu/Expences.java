package com.bezkoder.springjwt.models.Menu;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Expences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "menu_id", unique = true)
    private Menu menu;

    private int cook;

    @Builder.Default
    @OneToMany(mappedBy = "expences", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpencesWaiter> waiters = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "expences", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OtherExpences> otherExpences = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "expences", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingSum> shoppingSums = new ArrayList<>();

    public void addWaiter(ExpencesWaiter waiter) {
        this.waiters.add(waiter);
        waiter.setExpences(this);
    }

    public void removeWaiter(ExpencesWaiter waiter) {
        this.waiters.remove(waiter);
        waiter.setExpences(null);
    }

    public void addOtherExpence(OtherExpences otherExpence) {
        this.otherExpences.add(otherExpence);
        otherExpence.setExpences(this);
    }

    public void removeOtherExpence(OtherExpences otherExpence) {
        this.otherExpences.remove(otherExpence);
        otherExpence.setExpences(null);
    }

    public void addShoppingSum(ShoppingSum shoppingSum) {
        this.shoppingSums.add(shoppingSum);
        shoppingSum.setExpences(this);
    }

    public void removeShoppingSum(ShoppingSum shoppingSum) {
        this.shoppingSums.remove(shoppingSum);
        shoppingSum.setExpences(null);
    }
}
