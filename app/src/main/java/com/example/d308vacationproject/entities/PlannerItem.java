package com.example.d308vacationproject.entities;

//Abstract base class for all "planned travel" items (trips, excursions, etc.)
//
//Child classes merely extend this and inherit the base methods from PlannerItem

public abstract class PlannerItem {
    // Polymorphism:
    // “A common (and very clean) way to get polymorphism is to declare a method abstract (or virtual) in a base class/interface, leave it
    // unimplemented there, require subclasses to implement it differently, and then let the language call the right version at runtime based on the
    // real object type.”

    // Abstract -> Means that each kind of planner item (trip, excursion) must have this implemented in its definition
    public abstract String getItemName();

    public abstract String getSummary();

    public boolean isValid() {
        String name = getItemName();
        return (name != null) && (!name.trim().isEmpty());
    }
}
