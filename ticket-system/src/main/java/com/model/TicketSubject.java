package com.model;

import java.util.List;
import java.util.ArrayList;
public class TicketSubject {
  private List<TicketObserver> observers = new ArrayList<>();

  public void attach(TicketObserver observer) {
    observers.add(observer);
  }

  public void detach(TicketObserver observer) {
    observers.remove(observer);
  }

  public void notifyObservers(Ticket ticket) {
    for (TicketObserver observer : observers) {
        observer.update(ticket);
    }
  }
    
}
